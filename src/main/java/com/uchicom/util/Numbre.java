// (C) 2022 uchicom
package com.uchicom.util;

import java.util.ArrayList;
import java.util.List;

/**
 * "1,3,5-7"形式の数値パターンを展開するユーティリティ.
 *
 * <p>カンマ区切りで複数指定でき、{@code a-b}で範囲(両端を含む)を指定できる。
 *
 * <p>クラス名 {@code Numbre} は、{@link java.lang.Number} との名前衝突を避けるため、他の言語での綴りを意図的に使用している。
 */
public class Numbre {

  /** 1つのパターンで展開できる最大要素数 */
  public static final long MAX_EXPAND_SIZE = 1_000_000L;

  /**
   * "1,3,5-7"形式の文字列をint値のリストに展開する。
   *
   * @param arg 展開するパターン(例: {@code "1,3,5-7"})
   * @return 展開後の値のリスト(例: {@code [1, 3, 5, 6, 7]})
   * @throws IllegalArgumentException 不正な形式、範囲の逆転、展開数が上限超過、int範囲外の場合
   */
  public static List<Integer> expand(String arg) {
    var longs = expandLong(arg);
    var list = new ArrayList<Integer>(longs.size());
    for (var value : longs) {
      if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("Value out of int range: " + value);
      }
      list.add(value.intValue());
    }
    return list;
  }

  /**
   * "1,3,5-7"形式の文字列をlong値のリストに展開する。
   *
   * @param arg 展開するパターン(例: {@code "1,3,5-7"})
   * @return 展開後の値のリスト
   * @throws IllegalArgumentException 不正な形式、範囲の逆転、展開数が上限({@link #MAX_EXPAND_SIZE})超過の場合
   */
  public static List<Long> expandLong(String arg) {
    if (arg == null) {
      throw new IllegalArgumentException("Pattern must not be null");
    }
    var list = new ArrayList<Long>();
    for (var token : arg.split(",", -1)) {
      var splits = token.split("-", -1);
      if (splits.length == 1) {
        add(list, parse(splits[0], arg), arg);
      } else if (splits.length == 2) {
        var start = parse(splits[0], arg);
        var end = parse(splits[1], arg);
        if (start > end) {
          throw new IllegalArgumentException(
              "Range start must not be greater than end: '" + token + "' in '" + arg + "'");
        }
        if (end - start >= MAX_EXPAND_SIZE || end - start < 0) {
          throw new IllegalArgumentException(
              "Range is too large (max " + MAX_EXPAND_SIZE + "): '" + token + "' in '" + arg + "'");
        }
        for (long i = start; i <= end; i++) {
          add(list, i, arg);
        }
      } else {
        throw new IllegalArgumentException("Invalid range: '" + token + "' in '" + arg + "'");
      }
    }
    return list;
  }

  private static void add(List<Long> list, long value, String arg) {
    if (list.size() >= MAX_EXPAND_SIZE) {
      throw new IllegalArgumentException(
          "Too many elements (max " + MAX_EXPAND_SIZE + ") in '" + arg + "'");
    }
    list.add(value);
  }

  private static long parse(String text, String arg) {
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Empty number in '" + arg + "'");
    }
    try {
      return Long.parseLong(text);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number '" + text + "' in '" + arg + "'", e);
    }
  }
}
