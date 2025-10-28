// (C) 2023 uchicom
package com.uchicom.util;

@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {
  void run() throws T;
}
