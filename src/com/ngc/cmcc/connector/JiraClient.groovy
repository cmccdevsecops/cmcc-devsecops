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
    def text = "SRF for security tools";
    def username = "dexterpeter.danao@ngc.com";
    def password = "8DmDLR0ad0PhSszeUka68B77"

    JiraConnector connector = new JiraConnector(issueId);
    // Get issue
    connector.open(username, password);
    connector.getIssue();
    // Update description of an issue
    connector.open(username, password);
    connector.updateDescription(text);
    // Add attachment to an issue
    connector.open(entity, username, password);
    connector.addAttachment("test-1.txt");
  }
 }