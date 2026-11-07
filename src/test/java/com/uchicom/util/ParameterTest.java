// (C) 2026 uchicom
package com.uchicom.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ParameterTest {

  @Test
  public void constructor_parsesKeyValuePairs() {
    var parameter = new Parameter(new String[] {"-name", "value", "-other", "x"});

    assertThat(parameter.get("name")).isEqualTo("value");
    assertThat(parameter.get("other")).isEqualTo("x");
  }

  @Test
  public void constructor_emptyArgs() {
    var parameter = new Parameter(new String[] {});

    assertThat(parameter.is("name")).isFalse();
  }

  @Test
  public void constructor_argWithoutDash_isStoredAsKeyAndValue() {
    var parameter = new Parameter(new String[] {"flag"});

    assertThat(parameter.is("flag")).isTrue();
    assertThat(parameter.get("flag")).isEqualTo("flag");
  }

  @Test
  public void constructor_lastDashArg_isStoredWithDashAsKeyAndValue() {
    var parameter = new Parameter(new String[] {"-v"});

    assertThat(parameter.is("-v")).isTrue();
    assertThat(parameter.is("v")).isFalse();
    assertThat(parameter.get("-v")).isEqualTo("-v");
  }

  @Test
  public void put_and_get() {
    var parameter = new Parameter(new String[] {});
    parameter.put("key", "value");

    assertThat(parameter.get("key")).isEqualTo("value");
  }

  @Test
  public void is_returnsFalse_whenAbsent() {
    var parameter = new Parameter(new String[] {"-a", "1"});

    assertThat(parameter.is("a")).isTrue();
    assertThat(parameter.is("b")).isFalse();
  }

  @Test
  public void get_returnsNull_whenAbsent() {
    var parameter = new Parameter(new String[] {});

    assertThat(parameter.get("none")).isNull();
  }

  @Test
  public void getWithDefault() {
    var parameter = new Parameter(new String[] {"-a", "1"});

    assertThat(parameter.get("a", "def")).isEqualTo("1");
    assertThat(parameter.get("b", "def")).isEqualTo("def");
  }

  @Test
  public void getFile() {
    var parameter = new Parameter(new String[] {"-f", "dir/file.txt"});

    assertThat(parameter.getFile("f")).isEqualTo(new File("dir/file.txt"));
    assertThat(parameter.getFile("none")).isNull();
  }

  @Test
  public void getFileWithDefault() {
    var parameter = new Parameter(new String[] {"-f", "dir/file.txt"});
    var def = new File("default.txt");

    assertThat(parameter.getFile("f", def)).isEqualTo(new File("dir/file.txt"));
    assertThat(parameter.getFile("none", def)).isSameAs(def);
  }

  @Test
  public void getPath() {
    var parameter = new Parameter(new String[] {"-p", "dir/file.txt"});

    assertThat(parameter.getPath("p")).isEqualTo(Path.of("dir/file.txt"));
    assertThat(parameter.getPath("none")).isNull();
  }

  @Test
  public void getPathWithDefault() {
    var parameter = new Parameter(new String[] {"-p", "dir/file.txt"});
    var def = Path.of("default.txt");

    assertThat(parameter.getPath("p", def)).isEqualTo(Path.of("dir/file.txt"));
    assertThat(parameter.getPath("none", def)).isSameAs(def);
  }

  @Test
  public void getInteger() {
    var parameter = new Parameter(new String[] {"-n", "42", "-bad", "abc"});

    assertThat(parameter.getInteger("n")).isEqualTo(42);
    assertThat(parameter.getInteger("none")).isNull();
    assertThatThrownBy(() -> parameter.getInteger("bad")).isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void getIntegerWithDefault() {
    var parameter = new Parameter(new String[] {"-n", "42"});

    assertThat(parameter.getInteger("n", 1)).isEqualTo(42);
    assertThat(parameter.getInteger("none", 1)).isEqualTo(1);
    assertThat(parameter.getInteger("none", null)).isNull();
  }

  @Test
  public void getInt() {
    var parameter = new Parameter(new String[] {"-n", "42", "-bad", "abc"});

    assertThat(parameter.getInt("n")).isEqualTo(42);
    assertThatThrownBy(() -> parameter.getInt("none")).isInstanceOf(NumberFormatException.class);
    assertThatThrownBy(() -> parameter.getInt("bad")).isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void getIntWithDefault() {
    var parameter = new Parameter(new String[] {"-n", "42"});

    assertThat(parameter.getInt("n", 1)).isEqualTo(42);
    assertThat(parameter.getInt("none", 1)).isEqualTo(1);
  }

  @Test
  public void getLocalDate() {
    var parameter = new Parameter(new String[] {"-d", "2026-01-02"});

    assertThat(parameter.getLocalDate("d")).isEqualTo(LocalDate.of(2026, 1, 2));
    assertThat(parameter.getLocalDate("none")).isNull();
  }

  @Test
  public void getLocalDateWithDefault() {
    var parameter = new Parameter(new String[] {"-d", "2026-01-02"});
    var def = LocalDate.of(2000, 1, 1);

    assertThat(parameter.getLocalDate("d", def)).isEqualTo(LocalDate.of(2026, 1, 2));
    assertThat(parameter.getLocalDate("none", def)).isEqualTo(def);
  }

  @Test
  public void parseIntPatterns_single() {
    assertThat(Parameter.parseIntPatterns(new String[] {"5"})).isEqualTo(List.of(5L));
  }

  @Test
  public void parseIntPatterns_commaSeparated() {
    assertThat(Parameter.parseIntPatterns(new String[] {"1,3,5"})).isEqualTo(List.of(1L, 3L, 5L));
  }

  @Test
  public void parseIntPatterns_range() {
    assertThat(Parameter.parseIntPatterns(new String[] {"2-4"})).isEqualTo(List.of(2L, 3L, 4L));
  }

  @Test
  public void parseIntPatterns_mixedAndMultipleArgs() {
    assertThat(Parameter.parseIntPatterns(new String[] {"1,3-5", "9"}))
        .isEqualTo(List.of(1L, 3L, 4L, 5L, 9L));
  }

  @Test
  public void parseIntPatterns_emptyArgs() {
    assertThat(Parameter.parseIntPatterns(new String[] {})).isEmpty();
  }

  @Test
  public void parseIntPatterns_reversedRange_isEmpty() {
    assertThat(Parameter.parseIntPatterns(new String[] {"5-3"})).isEmpty();
  }

  @Test
  public void parseIntPatterns_invalid_throws() {
    assertThatThrownBy(() -> Parameter.parseIntPatterns(new String[] {"a"}))
        .isInstanceOf(NumberFormatException.class);
  }
}
