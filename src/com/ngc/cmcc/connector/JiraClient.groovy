/*------------------------------------------------------------------------------
 * Classification: UNCLASSIFIED
 * Organization:   Northrop Grumman Aeronautics, CMCC
 *                 16750 Via Del Campo Ct, San Diego, California 92127, U.S.A
 *
 * Project:        Jira Client
 * Usage: 
 *
 -----------------------------------------------------------------------------*/
 package com.ngc.cmcc.connector;

 import com.ngc.cmcc.connector.JiraConnector;

public class JiraClient {
  public JiraClient() {}

  static void main(String[] args) {
    def issueId = "CNN-1";
    def entity = "attachments";
    def text = "hello world 2"

    JiraConnector connector = new JiraConnector();
    // Get issue
    connector.open(issueId);
    println(connector.getIssue());
    // Update description of an issue
    connector.open(issueId);
    connector.updateDescription(text);
    // Add attachment to an issue
    connector.open(issueId, entity);
    connector.addAttachment("test-1.txt");
    connector.close();
  }
 }