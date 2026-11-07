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
 *   <li>値の位置にある引数が {@code -} で始まる場合、負数(例: {@code -5}, {@code -1.5})のときだけ値として扱う。 それ以外(例: {@code
 *       -x})は次のキーとみなし、直前のキーはフラグ扱いになる。 したがって {@code -v -x file} は v=フラグ, x="file" と解釈される。
 *   <li>値を伴わないフラグ({@code -flag} が末尾、または直後がキー)は、ダッシュを除いたキーで登録し、値はキー自身とする。
 *   <li>{@code -} で始まらない引数 {@code arg} は、キー {@code arg} 値 {@code arg} のフラグとして登録する。
 *   <li>同じキーが複数回指定された場合は後勝ち。
 * </ul>
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class Parameter {

  private static final Pattern NEGATIVE_NUMBER = Pattern.compile("-\\d+(\\.\\d+)?");

  private Map<String, String> parameterMap = new HashMap<>();

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

  public void put(String key, String value) {
    parameterMap.put(key, value);
  }

  public String get(String key) {
    return parameterMap.get(key);
  }

  public String get(String key, String defaultValue) {
    if (is(key)) {
      return get(key);
    }
    return defaultValue;
  }

  public File getFile(String key) {
    String path = parameterMap.get(key);
    if (path == null) {
      return null;
    }
    return new File(path);
  }

  public File getFile(String key, File defaultValue) {
    if (is(key)) {
      return getFile(key);
    }
    return defaultValue;
  }

  public Path getPath(String key) {
    String path = parameterMap.get(key);
    if (path == null) {
      return null;
    }
    return Path.of(path);
  }

  public Path getPath(String key, Path defaultValue) {
    if (is(key)) {
      return getPath(key);
    }
    return defaultValue;
  }

  public Integer getInteger(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      return null;
    }
    return Integer.valueOf(value);
  }

  public Integer getInteger(String key, Integer defaultValue) {
    if (is(key)) {
      return getInteger(key);
    }
    return defaultValue;
  }

  public int getInt(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      throw new NumberFormatException("parameter is not specified: " + key);
    }
    return Integer.parseInt(value);
  }

  public int getInt(String key, int defaultValue) {
    if (is(key)) {
      return getInt(key);
    }
    return defaultValue;
  }

  public Long getLong(String key) {
    var value = parameterMap.get(key);
    if (value == null) {
      return null;
    }
    return Long.valueOf(value);
  }

  public Long getLong(String key, Long defaultValue) {
    if (is(key)) {
      return getLong(key);
    }
    return defaultValue;
  }

  public boolean is(String key) {
    return parameterMap.containsKey(key);
  }

  public LocalDate getLocalDate(String key) {
    if (!parameterMap.containsKey(key)) {
      return null;
    }
    return LocalDate.parse(parameterMap.get(key));
  }

  public LocalDate getLocalDate(String key, LocalDate defaultValue) {
    if (is(key)) {
      return getLocalDate(key);
    }
    return defaultValue;
  }

  public static List<Long> parseIntPatterns(String[] args) {
    var list = new ArrayList<Long>();
    Stream.of(args).map(Parameter::parseIntPattern).forEach(list::addAll);
    return list;
  }

  protected static List<Long> parseIntPattern(String arg) {
    return Numbre.expandLong(arg);
  }
}
