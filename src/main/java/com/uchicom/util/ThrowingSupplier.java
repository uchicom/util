// (C) 2023 uchicom
package com.uchicom.util;

@FunctionalInterface
public interface ThrowingSupplier<T, R extends Throwable> {
  T get() throws R;
}
