// (C) 2023 uchicom
package com.uchicom.util;

/** 検査例外をスローできるSupplier. */
@FunctionalInterface
public interface ThrowingSupplier<T, R extends Throwable> {
  T get() throws R;
}
