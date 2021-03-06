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
  public static final JIRA_CLOUD = "https://cmcc-ngc.atlassian.net";

  static final JIRA_API1 = "/rest/api/2/issue/%s";
  static final JIRA_API2 = "/rest/api/2/issue/%s/%s";
  static final JIRA_API3 = "/rest/api/2/issue/%s?fields=%s";
  static final JIRA_PAYLOAD_DESCRIPTION = '{ "fields": {"description": "%s"} }';
  static final LINE_FEED = "\r\n";
  static final CHARSET_ISO_8859 = "ISO-8859-1";
  static final BOUNDARY = "*****";
  static final EOFILE = -1;

  // Declare variables
  def steps;
  def uri;
  def client;
  def issueId;

  public Connector(issueId) {
    this.uri = URI.create(JIRA_CLOUD);
    this.issueId = issueId;
  }

  /**
   * Constructor
   * @param steps Pipeline steps
   * @param endpoint Jira server endpoint
   * @param issueId Jira issue Id
   */
  public Connector(steps, endpoint, issueId) {
    this.steps = steps;
    this.uri = URI.create(endpoint);
    this.issueId = issueId;
  }

  public open(username, password) {
    def api = String.format(JIRA_API1, this.issueId);
    try {
      this.client = this.uri.resolve(this.uri.getPath() + api).toURL().openConnection();
      authorization(username, password);
      this.client.setRequestProperty("Accept", "application/json"); 
    }catch (Exception xe) {
      throw new Exception(xe);
    }
  }

  public open(username, password, entity) {
    def api = String.format(JIRA_API2, this.issueId, entity);
    try {
      this.client = this.uri.resolve(this.uri.getPath() + api).toURL().openConnection();
      authorization(username, password);
      this.client.setRequestProperty("Accept", "application/json");
    }catch (Exception xe) {
      throw new Exception(xe);
    }
  }

  public void attach(String name, File file) {
    this.client.setRequestMethod("POST");
    this.client.setDoOutput(true);
    this.client.setDoInput(true);
    this.client.setUseCaches(false);
    this.client.setRequestProperty("Connection", "Keep-Alive");
    this.client.setRequestProperty("Cache-Control", "no-cache");
    this.client.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

    OutputStream outputStream = this.client.getOutputStream();
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET_ISO_8859), true);

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
      while ((bytesRead = inputStream.read(buffer)) != EOFILE) {
        // Handle index that is out of bounds
        if(bytesRead == EOFILE) {
          break;
        }
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
      inputStream.close();

      writer.append(LINE_FEED);
      writer.flush();
    } catch(Exception xe) {
      throw new Exception(xe);
    } finally {
      close(writer);
    }
  }

  public update(payload) {
    this.client.setRequestProperty("Content-Type", "application/json");
    this.client.setRequestMethod("PUT");
    this.client.setDoOutput(true);
    this.client.setDoInput(true);
    try {
      def out = new OutputStreamWriter(this.client.outputStream);
      out.write(payload);
      out.close();
      if(this.client.responseCode==401) {
        throw new Exception(String.format("Unauthorized access to API resource, %s", payload));
      }
    } catch (Exception xe) {
      throw new Exception(xe);
    } finally {
      this.client.disconnect();
    }
  }

  public read() {
    def responseText;
    try {
      this.client.setRequestMethod("GET");
      responseText = this.client.inputStream.text;
    }catch (Exception xe) {
      throw new Exception(xe);
    } finally {
      this.client.disconnect();
    }

    return responseText;
  }

  public close(PrintWriter writer) {
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
      }
    }catch(Exception e) {
      throw new Exception(e);
    }finally {
      this.client.disconnect();
    }
  }
  
  public debug(text) {
    println(String.format("DEBUG: %s %s", new Date(), text));
  }

  public error(text) {
    println(String.format("ERROR: %s %s", new Date(), text));
  }

  public authorization(username, password) {
    def credential = String.format("%s:%s", username, password);
    this.client.setRequestProperty("Authorization", "Basic " + credential.bytes.encodeBase64().toString());
    this.client.setRequestProperty("X-Atlassian-Token", "no-check");
  }
}