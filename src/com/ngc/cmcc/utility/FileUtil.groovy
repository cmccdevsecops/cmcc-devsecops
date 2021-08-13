/*------------------------------------------------------------------------------
 * Classification: UNCLASSIFIED
 * Organization:   Northrop Grumman Aeronautics, CMCC
 *                 16750 Via Del Campo Ct, San Diego, California 92127, U.S.A
 *
 * Project:        File Utility
 *
 -----------------------------------------------------------------------------*/
package com.ngc.cmcc.utility;

import java.util.zip.*;

public class FileUtil {
  public static final MANIFEST_KEYS = [
    'SRF-Ticket',
    'Image-Tag',
    'Image-Version',
    'Image-Registry',
    'Created',
    'Created-By',
  ]

  def manifestProperties = [:];

  /**
   * Constructor
   * 
   * @param name Name of archive file
   */
  public FileUtil(name) {
    // Extract manifest attributes
    try {
      File archive = new File(name);
      def ar = new java.util.zip.ZipFile(archive)
      ar.entries().findAll { !it.directory }.each {
        if(it.name.contains("MANIFEST.MF")) {
          def attributes = ar.getInputStream(it).text.split('\n');
          attributes.each {
            def tokens = it.split(':');
            if(tokens.size() == 2) {
              manifestProperties[tokens[0]] = tokens[1].trim();
            } else if(tokens.size() == 4) {
              // date created
              manifestProperties[tokens[0]] = tokens[1].trim() + ":" + tokens[2] + ":" + tokens[2];
            }
          }
        }
      }
    }catch (FileNotFoundException xe) {
      throw new Exception(xe);
    }catch (Exception xe) {
      throw new Exception(xe);
    }
  }

  /**
   * Validate manifest file
   * 
   * @return valid
   */
  public validate() {
    def isValid = true;
    MANIFEST_KEYS.each {
      def value = getValue(it);
      if( value==null) {
        isValid = false;
      }
    }
    return isValid;
  }

  /**
   * Get key value
   * 
   * @param key key
   * @return value
   */
  public getValue(key) {
    return manifestProperties[key];
  }

  public getValues() {
    return manifestProperties.values();
  }

  public getKeys() {
    return manifestProperties.keySet();
  }

  public getProperties() {
    manifestProperties.toMapString();
  }

  public getList() {
    def text = "----\\n";
    manifestProperties.each {
      text = text + it.key + ": " +  it.value + "\\n ";
    }
    return text;
  }
}