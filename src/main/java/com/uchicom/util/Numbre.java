// (C) 2022 uchicom
package com.uchicom.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Numbre {

  public static List<Integer> expand(String arg) {
    var list = new ArrayList<Integer>();
    Stream.of(arg.split(","))
        .forEach(
            id -> {
              if (id.contains("-")) {
                var splits = id.split("\\-", 0);
                var start = Integer.parseInt(splits[0]);
                var end = Integer.parseInt(splits[1]);
                for (int i = start; i <= end; i++) {
                  list.add(i);
                }
              } else {
                list.add(Integer.parseInt(id));
              }
            });
    return list;
  }

  public static List<Long> expandLong(String arg) {
    var list = new ArrayList<Long>();
    Stream.of(arg.split(","))
        .forEach(
            id -> {
              if (id.contains("-")) {
                var splits = id.split("\\-", 0);
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
