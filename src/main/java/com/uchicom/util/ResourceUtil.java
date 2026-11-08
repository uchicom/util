// (C) 2017 uchicom
package com.uchicom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
    return load(new Properties(), file, toCharset(charset));
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
    return load(new Properties(), is, toCharset(charset));
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
    return load(new EnvProperties(), file, toCharset(charset));
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
    return load(new EnvProperties(), is, toCharset(charset));
  }

  /**
   * ファイルからPropertiesを生成する。ファイルが存在しない場合は空のPropertiesを返す.
   *
   * @param file propertiesファイル
   * @param charset 文字コード
   * @return 読み込んだProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static Properties createProperties(File file, Charset charset) throws IOException {
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
  public static Properties createProperties(InputStream is, Charset charset) throws IOException {
    return load(new Properties(), is, charset);
  }

  /**
   * ファイルからEnvPropertiesを生成する。ファイルが存在しない場合は空のEnvPropertiesを返す.
   *
   * @param file propertiesファイル
   * @param charset 文字コード
   * @return 読み込んだEnvProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static EnvProperties createEnvProperties(File file, Charset charset) throws IOException {
    return load(new EnvProperties(), file, charset);
  }

  /**
   * ストリームからEnvPropertiesを生成する。ストリームは閉じられる.
   *
   * @param is 入力ストリーム
   * @param charset 文字コード
   * @return 読み込んだEnvProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static EnvProperties createEnvProperties(InputStream is, Charset charset)
      throws IOException {
    return load(new EnvProperties(), is, charset);
  }

  /**
   * クラスパス上のリソースからPropertiesを生成する。リソースが存在しない場合は空のPropertiesを返す.
   *
   * @param name クラスパス上のリソース名(先頭の{@code /}なし)
   * @param charset 文字コード
   * @return 読み込んだProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static Properties createPropertiesFromClasspath(String name, Charset charset)
      throws IOException {
    return loadClasspath(new Properties(), name, charset);
  }

  /**
   * クラスパス上のリソースからEnvPropertiesを生成する。リソースが存在しない場合は空のEnvPropertiesを返す.
   *
   * @param name クラスパス上のリソース名(先頭の{@code /}なし)
   * @param charset 文字コード
   * @return 読み込んだEnvProperties
   * @throws IOException 読み込みに失敗した場合
   */
  public static EnvProperties createEnvPropertiesFromClasspath(String name, Charset charset)
      throws IOException {
    return loadClasspath(new EnvProperties(), name, charset);
  }

  private static Charset toCharset(String charset) throws UnsupportedEncodingException {
    try {
      return Charset.forName(charset);
    } catch (IllegalArgumentException e) {
      var ex = new UnsupportedEncodingException(charset);
      ex.initCause(e);
      throw ex;
    }
  }

  static <T extends Properties> T loadClasspath(T properties, String name, Charset charset)
      throws IOException {
    try (var is = ResourceUtil.class.getClassLoader().getResourceAsStream(name)) {
      if (is != null) {
        load(properties, is, charset);
      }
    }
    return properties;
  }

  static <T extends Properties> T load(T properties, File file, Charset charset)
      throws IOException {
    if (file.exists() && file.isFile()) {
      try (var fis = new FileInputStream(file)) {
        load(properties, fis, charset);
      }
    }
    return properties;
  }

  static <T extends Properties> T load(T properties, InputStream is, Charset charset)
      throws IOException {
    try (var isr = new InputStreamReader(is, charset)) {
      properties.load(isr);
    }
    return properties;
  }
}
