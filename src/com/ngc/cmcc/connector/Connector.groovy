/*------------------------------------------------------------------------------
 * Classification: UNCLASSIFIED
 * Organization:   Northrop Grumman Aeronautics, CMCC
 *                 16750 Via Del Campo Ct, San Diego, California 92127, U.S.A
 *
 * Project:        Base Connector
 -----------------------------------------------------------------------------*/
package com.ngc.cmcc.connector;

abstract public class Connector implements Serializable {
  // Define constants
  static final JIRA_CLOUD = "https://cmcc-ngc.atlassian.net";
  static final JIRA_API1 = "/rest/api/2/issue/%s";
  static final JIRA_API2 = "/rest/api/2/issue/%s/%s";
  static final JIRA_PAYLOAD_DESCRIPTION = '{ "fields": {"description": "%s"} }';
  static final LINE_FEED = "\r\n";
  static final CHARSET_ISO_8859 = "ISO-8859-1";
  static final BOUNDARY = "*****";

  // Declare variables
  private OutputStream outputStream;
  private PrintWriter writer;

  def steps;
  def uri;
  def client;

  public Connector() {
    this.uri = URI.create(JIRA_CLOUD);
  }

  public Connector(steps) {
    this.steps = steps;
    Connector();
  }

  /**
   * Constructor
   * @param steps Pipeline steps
   * @param endpoint Jira server endpoint
   */
  public Connector(steps, endpoint) {
    this.steps = steps;
    this.uri = URI.create(endpoint);
  }

  public open(String issueId) {
    def api = String.format(JIRA_API1, issueId);

    this.client = this.uri.resolve(this.uri.getPath() + api).toURL().openConnection();
    authorization();
    this.client.setRequestProperty("Accept", "application/json");
  }

  public open(String issueId, entity) {
    def api = String.format(JIRA_API2, issueId, entity);

    this.client = this.uri.resolve(this.uri.getPath() + api).toURL().openConnection();
    authorization();
    this.client.setRequestProperty("Accept", "application/json");
  }

  public void multipart(String name, File file) {
    this.client.setRequestMethod("POST");
    this.client.setDoOutput(true);
    this.client.setDoInput(true);
    this.client.setUseCaches(false);
    this.client.setRequestProperty("Connection", "Keep-Alive");
    this.client.setRequestProperty("Cache-Control", "no-cache");
    this.client.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

    outputStream = this.client.getOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET_ISO_8859), true);

    String fileName = file.getName();
    try {
      writer.append("--" + BOUNDARY).append(LINE_FEED);
      writer.append( "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + 
          fileName + "\"").append(LINE_FEED);
      writer.append( "Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).
          append(LINE_FEED);
      writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
      writer.append(LINE_FEED);
      writer.flush();

      FileInputStream inputStream = new FileInputStream(file);
      byte[] buffer = new byte[4096];
      int bytesRead = -1;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
      inputStream.close();

      writer.append(LINE_FEED);
      writer.flush();
    } catch(Exception e) {
      error(e.toString());
    }
  }

  public update(payload) {
    debug(payload);
    this.client.setRequestProperty("Content-Type", "application/json");
    this.client.setRequestMethod("PUT");
    this.client.setDoOutput(true);
    this.client.setDoInput(true);
    try {
      def out = new OutputStreamWriter(this.client.outputStream);
      out.write(payload);
      out.close();
      debug(this.client.responseCode);
    } catch (Exception e) {
      error(e.toString());
    }
  }

  public read() {
    this.client.setRequestMethod("GET");
    debug(this.client.responseCode);
  }

  public void close() {
    StringBuffer response = new StringBuffer();

    try {
      writer.append(LINE_FEED).flush();
      writer.append("--" + BOUNDARY + "--").append(LINE_FEED);
      writer.close();
      int status = this.client.getResponseCode();
      debug(status);
      if (status == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                this.client.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
        reader.close();
        this.client.disconnect();
      }
    }catch(Exception e) {
      error(e.toString());
    }
  }

  public debug(text) {
    println(String.format("DEBUG: %s %s", new Date(), text));
  }

  public error(text) {
    println(String.format("ERROR: %s %s", new Date(), text));
  }

  public authorization() {
    this.client.setRequestProperty("Authorization", "Basic " + 
        "dexterpeter.danao@ngc.com:8DmDLR0ad0PhSszeUka68B77".bytes.encodeBase64().toString());
    this.client.setRequestProperty("X-Atlassian-Token", "no-check");
  }

}