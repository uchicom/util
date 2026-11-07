// (C) 2022 uchicom
package com.uchicom.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

public class NumbreTest {

  @Test
  public void expand() {
    List<Integer> actual = Numbre.expand("1,3");
    assertThat(actual).hasSize(2);
    assertThat(actual.get(0)).isEqualTo(1);
    assertThat(actual.get(1)).isEqualTo(3);
    List<Integer> actual2 = Numbre.expand("5-7");
    assertThat(actual2).hasSize(3);
    assertThat(actual2.get(0)).isEqualTo(5);
    assertThat(actual2.get(1)).isEqualTo(6);
    assertThat(actual2.get(2)).isEqualTo(7);
    List<Integer> actual3 = Numbre.expand("1,3,5-7");
    assertThat(actual3).hasSize(5);
    assertThat(actual3.get(0)).isEqualTo(1);
    assertThat(actual3.get(1)).isEqualTo(3);
    assertThat(actual3.get(2)).isEqualTo(5);
    assertThat(actual3.get(3)).isEqualTo(6);
    assertThat(actual3.get(4)).isEqualTo(7);
  }

  @Test
  public void expandLong() {
    List<Long> actual = Numbre.expandLong("1,3");
    assertThat(actual).hasSize(2);
    assertThat(actual.get(0)).isEqualTo(1L);
    assertThat(actual.get(1)).isEqualTo(3L);
    List<Long> actual2 = Numbre.expandLong("5-7");
    assertThat(actual2).hasSize(3);
    assertThat(actual2.get(0)).isEqualTo(5L);
    assertThat(actual2.get(1)).isEqualTo(6L);
    assertThat(actual2.get(2)).isEqualTo(7L);
    List<Long> actual3 = Numbre.expandLong("1,3,5-7");
    assertThat(actual3).hasSize(5);
    assertThat(actual3.get(0)).isEqualTo(1L);
    assertThat(actual3.get(1)).isEqualTo(3L);
    assertThat(actual3.get(2)).isEqualTo(5L);
    assertThat(actual3.get(3)).isEqualTo(6L);
    assertThat(actual3.get(4)).isEqualTo(7L);
  }

  @Test
  public void expandLong_invalid() {
    for (var arg :
        new String[] {"5-", "-5", "", "1, 3", "1-2-3", "a", "1,,3", "5-3", "1-1000001"}) {
      assertThatThrownBy(() -> Numbre.expandLong(arg))
          .as(arg)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(arg);
    }
  }

  @Test
  public void expandLong_null() {
    assertThatThrownBy(() -> Numbre.expandLong(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void expandLong_maxRange() {
    assertThat(Numbre.expandLong("1-1000000")).hasSize(1_000_000);
  }

  @Test
  public void expandLong_tooManyElements() {
    assertThatThrownBy(() -> Numbre.expandLong("1-1000000,2000000"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void expand_outOfIntRange() {
    assertThatThrownBy(() -> Numbre.expand("2147483648"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
