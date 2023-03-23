// (C) 2014 uchicom
package com.uchicom.util;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Parameter
 *
 * @author uchicom: Shigeki Uchiyama
 */
public class Parameter {

  private Map<String, String> parameterMap = new HashMap<>();

  public Parameter(String[] args) {
    setParameters(args);
  }

  private void setParameters(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-") && i < args.length - 1) {
        parameterMap.put(args[i].substring(1), args[++i]);
      } else {
        parameterMap.put(args[i], args[i]);
      }
    }
  }

  public void put(String key, String value) {
    parameterMap.put(key, value);
  }

  public String get(String key) {
    return parameterMap.get(key);
  }

  public File getFile(String key) {
    String path = parameterMap.get(key);
    if (path == null) {
      return null;
    }
    return new File(path);
  }

  public Integer getInteger(String key) {
    String value = parameterMap.get(key);
    if (value == null) {
      return null;
    }
    return Integer.valueOf(value);
  }

  public int getInt(String key) {
    return Integer.parseInt(parameterMap.get(key));
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
  public static List<Long> parseIntPatterns(String[] args) {
    var list = new ArrayList<Long>();
    Stream.of(args).map(Parameter::parseIntPattern).forEach(list::addAll);
    return list;
  }

  protected static List<Long> parseIntPattern(String arg) {
    var list = new ArrayList<Long>();
    Stream.of(arg.split(","))
        .forEach(
            id -> {
              if (id.contains("-")) {
                var splits = id.split("\\-");
                var start = Long.parseLong(splits[0]);
                var end = Long.parseLong(splits[1]);
                for (long i = start; i <= end; i++) {
                  list.add(i);
                }
              } else {
                list.add(Long.parseLong(id));
              }
            });
    return list;
  }
}
