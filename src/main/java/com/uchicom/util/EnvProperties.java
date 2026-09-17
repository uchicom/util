// (C) 2026 uchicom
package com.uchicom.util;

import java.util.Locale;
import java.util.Properties;

/**
 * 環境変数、システムプロパティ、propertiesファイルの優先順位で値を取得するProperties.
 *
 * <p>優先順位: 環境変数 &gt; システムプロパティ(-D) &gt; propertiesファイル
 */
public class EnvProperties extends Properties {

  @Override
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

    return super.getProperty(key);
  }
}
