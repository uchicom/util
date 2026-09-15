// (C) 2026 uchicom
package com.uchicom.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * 環境変数、システムプロパティ、propertiesファイルの優先順位で値を取得するProperties.
 *
 * <p>優先順位: 環境変数 &gt; システムプロパティ(-D) &gt; propertiesファイル
 */
public class EnvProperties {

  private final Properties properties = new Properties();

  public EnvProperties(String resourcePath) {
    try (InputStream input =
        EnvProperties.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (input != null) {
        properties.load(input);
      } else {
        System.err.println("Warning: Resource not found: " + resourcePath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load properties file: " + resourcePath, e);
    }
  }

  public String getProperty(String key) {
    var envKey = key.replace('.', '_').toUpperCase(Locale.ROOT);

    var envValue = System.getenv(envKey);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    var sysValue = System.getProperty(key);
    if (sysValue != null && !sysValue.isBlank()) {
      return sysValue;
    }

    return properties.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    var value = getProperty(key);
    return value != null ? value : defaultValue;
  }
}
