// (C) 2026 uchicom
package com.uchicom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;
import java.util.Properties;

/**
 * システムプロパティ、環境変数、propertiesファイルの優先順位で値を取得するProperties.
 *
 * <p>優先順位: システムプロパティ(-D) &gt; 環境変数 &gt; propertiesファイル
 *
 * <p>環境変数・システムプロパティによる上書きはload時に解決し、値をそのまま保持する。 上書き対象はpropertiesファイルに存在するキーのみで、空白のみの値は無視される。
 * 環境変数名はキーの{@code .}を{@code _}に置換し大文字にしたもの(例: {@code db.url} → {@code DB_URL})。
 */
public class EnvProperties extends Properties {

  /**
   * Readerから読み込み、システムプロパティ・環境変数で上書きする.
   *
   * @param reader 入力
   * @throws IOException 読み込みに失敗した場合
   */
  @Override
  public synchronized void load(Reader reader) throws IOException {
    super.load(reader);
    resolveOverrides();
  }

  /**
   * InputStreamから読み込み、システムプロパティ・環境変数で上書きする.
   *
   * @param inStream 入力
   * @throws IOException 読み込みに失敗した場合
   */
  @Override
  public synchronized void load(InputStream inStream) throws IOException {
    super.load(inStream);
    resolveOverrides();
  }

  private void resolveOverrides() {
    for (var key : stringPropertyNames()) {
      var sysValue = System.getProperty(key);
      if (sysValue != null && !sysValue.isBlank()) {
        setProperty(key, sysValue);
        continue;
      }

      var envKey = key.replace('.', '_').toUpperCase(Locale.ROOT);
      var envValue = getEnv(envKey);
      if (envValue != null && !envValue.isBlank()) {
        setProperty(key, envValue);
      }
    }
  }

  /**
   * 環境変数を取得する. テストで差し替え可能にするため分離している.
   *
   * @param name 環境変数名
   * @return 環境変数の値。未設定の場合はnull
   */
  String getEnv(String name) {
    return System.getenv(name);
  }
}
