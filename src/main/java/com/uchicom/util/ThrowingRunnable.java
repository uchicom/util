// (C) 2023 uchicom
package com.uchicom.util;

/** 検査例外をスローできるRunnable. */
@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {
  void run() throws T;
}
