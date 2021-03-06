/*------------------------------------------------------------------------------
 * Classification: UNCLASSIFIED
 * Organization:   Northrop Grumman Aeronautics, CMCC
 *                 16750 Via Del Campo Ct, San Diego, California 92127, U.S.A
 *
 * Project:        Jira Connector
 * Usage: curl -D- -u admin:admin -X POST -H "X-Atlassian-Token: no-check" 
 *   -F "file=@myfile.txt" 
 *       https://cmcc-ngc.atlassian.net/rest/api/2/issue/CNN-1/attachments
 *
 -----------------------------------------------------------------------------*/
package com.ngc.cmcc.connector;

import com.ngc.cmcc.connector.Connector;
import com.ngc.cmcc.utility.JsonUtil;

public class JiraConnector extends Connector {

  def jsonUtil;

  public JiraConnector(issueId) {
    super(issueId);

    jsonUtil = new JsonUtil();
  }

  public JiraConnector(steps, endpoint, issueId) {
    super(steps, endpoint, issueId);

    jsonUtil = new JsonUtil();
  }

  /**
   * Adds attachment to an issue
   * @param name filename to attach
   */
  public addAttachment(name) throws Exception {
    File attachment = new File(name);
    attach("file", attachment);
  }

  /**
   * Update issue description
    * @param description description of an issue
   */
  public updateDescription(text) throws Exception {
    def payload = String.format(JIRA_PAYLOAD_DESCRIPTION, text);
    update(payload);
  }

  public getDescription() throws Exception {
    def responseText = read();
    def issueObject = jsonUtil.parse(responseText);

    return issueObject.fields.description.replaceAll("\\\n", "\\\\n");
  }
  /**
   * Get issue
   */
  public getIssue() throws Exception {
    def responseText = read();

    return jsonUtil.parse(responseText);
  }
}