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

/** propertiesファイル・ストリームを読み込むユーティリティ. */
public class ResourceUtil {

  /**
   * ファイルからPropertiesを生成する。ファイルが存在しない場合は空のPropertiesを返す.
   *
   * @param file propertiesファイル
   * @param charset 文字コード
   * @return 読み込んだProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static Properties createProperties(File file, String charset)
      throws FileNotFoundException, IOException {
    return load(new Properties(), file, charset);
  }

  /**
   * ストリームからPropertiesを生成する。ストリームは閉じられる.
   *
   * @param is 入力ストリーム
   * @param charset 文字コード
   * @return 読み込んだProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static Properties createProperties(InputStream is, String charset)
      throws IOException, UnsupportedEncodingException {
    return load(new Properties(), is, charset);
  }

  /**
   * ファイルからEnvPropertiesを生成する。ファイルが存在しない場合は空のEnvPropertiesを返す.
   *
   * @param file propertiesファイル
   * @param charset 文字コード
   * @return 読み込んだEnvProperties
   * @throws IOException 読み込みに失敗した場合
   * @see EnvProperties
   */
  public static EnvProperties createEnvProperties(File file, String charset)
      throws FileNotFoundException, IOException {
    return load(new EnvProperties(), file, charset);
  }

  /**
   * ストリームからEnvPropertiesを生成する。ストリームは閉じられる.
   *
   * @param is 入力ストリーム
   * @param charset 文字コード
   * @return 読み込んだEnvProperties
   * @throws IOException 読み込みに失敗した場合
   * @see EnvProperties
   */
  public static EnvProperties createEnvProperties(InputStream is, String charset)
      throws IOException, UnsupportedEncodingException {
    return load(new EnvProperties(), is, charset);
  }

  static <T extends Properties> T load(T properties, File file, String charset)
      throws FileNotFoundException, IOException {
    if (file.exists() && file.isFile()) {
      try (var fis = new FileInputStream(file)) {
        load(properties, fis, charset);
      }
    }
    return properties;
  }

  static <T extends Properties> T load(T properties, InputStream is, String charset)
      throws IOException, UnsupportedEncodingException {
    try (var isr = new InputStreamReader(is, charset)) {
      properties.load(isr);
    }
    return properties;
  }
}
