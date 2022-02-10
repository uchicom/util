// (C) 2022 uchicom
package com.uchicom.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** @author uchicom: Shigeki Uchiyama */
public class Numbre {

  public static List<Integer> expand(String arg) {
    List<Integer> list = new ArrayList<>();
    Stream.of(arg.split(","))
        .forEach(
            id -> {
              if (id.contains("-")) {
                String[] splits = id.split("\\-");
                int start = Integer.parseInt(splits[0]);
                int end = Integer.parseInt(splits[1]);
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
    List<Long> list = new ArrayList<>();
    Stream.of(arg.split(","))
        .forEach(
            id -> {
              if (id.contains("-")) {
                String[] splits = id.split("\\-");
                long start = Long.parseLong(splits[0]);
                long end = Long.parseLong(splits[1]);
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
