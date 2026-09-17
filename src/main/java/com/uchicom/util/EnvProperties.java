// (C) 2026 uchicom
package com.uchicom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.Properties;

/**
 * 環境変数、システムプロパティ、propertiesファイルの優先順位で値を取得するProperties.
 *
 * <p>優先順位: 環境変数 &gt; システムプロパティ(-D) &gt; propertiesファイル
 *
 * <p>環境変数・システムプロパティによる上書きはload時に解決し、値をそのまま保持する。
 */
public class EnvProperties extends Properties {

  @Override
  public synchronized void load(Reader reader) throws IOException {
    super.load(reader);
    resolveOverrides();
  }

  @Override
  public synchronized void load(InputStream inStream) throws IOException {
    super.load(inStream);
    resolveOverrides();
  }

  private void resolveOverrides() {
    for (var key : stringPropertyNames()) {
      var envKey = key.replace('.', '_').toUpperCase(Locale.ROOT);
      var envValue = System.getenv(envKey);
      if (envValue != null && !envValue.isBlank()) {
        setProperty(key, envValue);
        continue;
      }

      var sysValue = System.getProperty(key);
      if (sysValue != null && !sysValue.isBlank()) {
        setProperty(key, sysValue);
      }
    }
  }
}
