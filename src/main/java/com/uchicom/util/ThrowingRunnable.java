// (C) 2023 uchicom
package com.uchicom.util;

public interface ThrowingRunnable<T extends Throwable> {
  void run() throws T;
}
