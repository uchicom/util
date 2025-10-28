// (C) 2023 uchicom
package com.uchicom.util;

@FunctionalInterface
public interface ThrowingConsumer<T, R extends Throwable> {
  void accept(T t) throws R;
}
