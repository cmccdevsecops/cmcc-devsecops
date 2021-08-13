/*------------------------------------------------------------------------------
 * Classification: UNCLASSIFIED
 * Organization:   Northrop Grumman Aeronautics, CMCC
 *                 16750 Via Del Campo Ct, San Diego, California 92127, U.S.A
 *
 * Project:        Json Utility
 *
 -----------------------------------------------------------------------------*/
package com.ngc.cmcc.utility;

import groovy.json.JsonSlurper;

public class JsonUtil {
  def jsonSlurper;

  public JsonUtil() {
    jsonSlurper = new JsonSlurper();
  }

  public parse(text) {
    def object = jsonSlurper.parseText(text);

    return object;
  }
}

