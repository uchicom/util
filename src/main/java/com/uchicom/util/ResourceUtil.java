// (C) 2017 uchicom
package com.uchicom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ResourceUtil {

  public static Properties createProperties(File file, String charset)
      throws FileNotFoundException, IOException {
    var properties = new Properties();
    if (file.exists() && file.isFile()) {
      try (var fis = new FileInputStream(file)) {
        properties.putAll(createProperties(fis, charset));
      }
    }
    return properties;
  }

  public static Properties createProperties(InputStream is, String charset)
      throws IOException, UnsupportedEncodingException {

    var properties = new Properties();
    try (var isr = new InputStreamReader(is, charset)) {
      properties.load(isr);
    }
    return properties;
  }
}
