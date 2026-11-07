// (C) 2023 uchicom
package com.uchicom.util;

/** 検査例外をスローできるFunction. */
@FunctionalInterface
public interface ThrowingFunction<T, R, S extends Throwable> {
  R apply(T t) throws S;
}
