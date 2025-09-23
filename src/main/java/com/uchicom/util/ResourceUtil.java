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

/**
 * @author uchicom: Shigeki Uchiyama
 */
public class ResourceUtil {

  public static Properties createProperties(File file, String charset) {
    Properties properties = new Properties();
    if (file.exists() && file.isFile()) {
      try (FileInputStream fis = new FileInputStream(file)) {
        properties.putAll(createProperties(fis, charset));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return properties;
  }

  public static Properties createProperties(InputStream is, String charset) {

    Properties properties = new Properties();
    try (InputStreamReader isr = new InputStreamReader(is, charset)) {
      properties.load(isr);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return properties;
  }
}
