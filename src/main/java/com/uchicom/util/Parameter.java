// (C) 2014 uchicom
package com.uchicom.util;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * コマンドライン引数を解釈するクラス。
 *
 * <p>引数の解釈仕様:
 *
 * <ul>
 *   <li>{@code -key value}: キー {@code key} に値 {@code value} を登録する。
 *   <li>値の位置の引数が {@code -} で始まる場合、負数(例: {@code -5})のときだけ値として扱う。
 *   <li>負数以外の {@code -x} は次のキーとみなし、直前のキーはフラグ扱いになる。
 *   <li>例: {@code -v -x file} は v=フラグ, x="file" と解釈される。
 *   <li>値を伴わないフラグは、ダッシュを除いたキーで登録し、値はキー自身とする。
 *   <li>{@code -} で始まらない引数 {@code arg} は、キー・値とも {@code arg} のフラグとして登録する。
 *   <li>同じキーが複数回指定された場合は後勝ち。
 * </ul>
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class Parameter {

  private static final Pattern NEGATIVE_NUMBER = Pattern.compile("-\\d+(\\.\\d+)?");

  private Map<String, String> parameterMap = new HashMap<>();

  /**
   * 引数を解釈して生成する。
   *
   * @param args コマンドライン引数
   */
  public Parameter(String[] args) {
    setParameters(args);
  }

  private void setParameters(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (!args[i].startsWith("-")) {
        parameterMap.put(args[i], args[i]);
        continue;
      }
      var key = args[i].substring(1);
      if (i < args.length - 1 && isValue(args[i + 1])) {
        parameterMap.put(key, args[++i]);
      } else {
        parameterMap.put(key, key);
      }
    }
  }

  private static boolean isValue(String arg) {
    return !arg.startsWith("-") || NEGATIVE_NUMBER.matcher(arg).matches();
  }

  /**
   * キーと値を登録する。既存のキーは上書きされる。
   *
   * @param key キー
   * @param value 値
   */
  public void put(String key, String value) {
    parameterMap.put(key, value);
  }

  /**
   * キーに対応する値を取得する。
   *
   * @param key キー
   * @return 値。キーが未指定の場合はnull
   */
  public String get(String key) {
    return parameterMap.get(key);
  }

  /**
   * キーに対応する値を取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return 値。キーが未指定の場合はdefaultValue
   */
  public String get(String key, String defaultValue) {
    if (is(key)) {
      return get(key);
    }
    return defaultValue;
  }

  /**
   * キーに対応する値をFileとして取得する。
   *
   * @param key キー
   * @return Fileオブジェクト。キーが未指定の場合はnull
   */
  public File getFile(String key) {
    String path = parameterMap.get(key);
    if (path == null) {
      return null;
    }
    return new File(path);
  }

  /**
   * キーに対応する値をFileとして取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return Fileオブジェクト。キーが未指定の場合はdefaultValue
   */
  public File getFile(String key, File defaultValue) {
    if (is(key)) {
      return getFile(key);
    }
    return defaultValue;
  }

  /**
   * キーに対応する値をPathとして取得する。
   *
   * @param key キー
   * @return Pathオブジェクト。キーが未指定の場合はnull
   */
  public Path getPath(String key) {
    String path = parameterMap.get(key);
    if (path == null) {
      return null;
    }
    return Path.of(path);
  }

  /**
   * キーに対応する値をPathとして取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return Pathオブジェクト。キーが未指定の場合はdefaultValue
   */
  public Path getPath(String key, Path defaultValue) {
    if (is(key)) {
      return getPath(key);
    }
    return defaultValue;
  }

  /**
   * キーに対応する値をIntegerとして取得する。
   *
   * @param key キー
   * @return 値。キーが未指定の場合はnull
   * @throws NumberFormatException 数値として解釈できない場合
   */
  public Integer getInteger(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      return null;
    }
    return Integer.valueOf(value);
  }

  /**
   * キーに対応する値をIntegerとして取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return 値。キーが未指定の場合はdefaultValue
   * @throws NumberFormatException 数値として解釈できない場合
   */
  public Integer getInteger(String key, Integer defaultValue) {
    if (is(key)) {
      return getInteger(key);
    }
    return defaultValue;
  }

  /**
   * キーに対応する値をintとして取得する。
   *
   * @param key キー
   * @return 値
   * @throws NumberFormatException キーが未指定、または数値として解釈できない場合
   */
  public int getInt(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      throw new NumberFormatException("parameter is not specified: " + key);
    }
    return Integer.parseInt(value);
  }

  /**
   * キーに対応する値をintとして取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return 値。キーが未指定の場合はdefaultValue
   * @throws NumberFormatException 数値として解釈できない場合
   */
  public int getInt(String key, int defaultValue) {
    if (is(key)) {
      return getInt(key);
    }
    return defaultValue;
  }

  /**
   * キーに対応する値をLongとして取得する。
   *
   * @param key キー
   * @return 値。キーが未指定の場合はnull
   * @throws NumberFormatException 数値として解釈できない場合
   */
  public Long getLong(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      return null;
    }
    return Long.valueOf(value);
  }

  /**
   * キーに対応する値をLongとして取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return 値。キーが未指定の場合はdefaultValue
   * @throws NumberFormatException 数値として解釈できない場合
   */
  public Long getLong(String key, Long defaultValue) {
    if (is(key)) {
      return getLong(key);
    }
    return defaultValue;
  }

  /**
   * キーが指定されているかを判定する。
   *
   * @param key キー
   * @return 指定されている場合true
   */
  public boolean is(String key) {
    return parameterMap.containsKey(key);
  }

  /**
   * キーに対応する値をLocalDate(ISO-8601形式、例: 2025-01-31)として取得する。
   *
   * @param key キー
   * @return 日付。キーが未指定の場合はnull
   * @throws java.time.format.DateTimeParseException 日付として解釈できない場合
   */
  public LocalDate getLocalDate(String key) {
    if (!parameterMap.containsKey(key)) {
      return null;
    }
    return LocalDate.parse(parameterMap.get(key));
  }

  /**
   * キーに対応する値をLocalDate(ISO-8601形式)として取得する。
   *
   * @param key キー
   * @param defaultValue キーが未指定の場合の値
   * @return 日付。キーが未指定の場合はdefaultValue
   * @throws java.time.format.DateTimeParseException 日付として解釈できない場合
   */
  public LocalDate getLocalDate(String key, LocalDate defaultValue) {
    if (is(key)) {
      return getLocalDate(key);
    }
    return defaultValue;
  }

  /**
   * "1,3,5-7"形式の複数のパターンを展開して連結する。
   *
   * @param args パターンの配列
   * @return 展開後の値のリスト
   * @throws IllegalArgumentException 不正なパターンの場合
   * @see Numbre#expandLong(String)
   */
  public static List<Long> parseIntPatterns(String[] args) {
    var list = new ArrayList<Long>();
    Stream.of(args).map(Parameter::parseIntPattern).forEach(list::addAll);
    return list;
  }

  protected static List<Long> parseIntPattern(String arg) {
    return Numbre.expandLong(arg);
  }
}
