// (C) 2026 uchicom
package com.uchicom.util.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DailyRollingFileHandlerTest {

  private static final ZoneId ZONE = ZoneId.of("Asia/Tokyo");
  private static final String LOG_FORMAT = "%1$tF %1$tT [%4$s] %2$s %5$s%6$s%n";
  private static final String FILE_NAME_FORMAT = "app-%d.log";

  @TempDir Path tempDir;

  private DailyRollingFileHandler handler;

  @AfterEach
  public void tearDown() {
    if (handler != null) {
      handler.close();
    }
  }

  private DailyRollingFileHandler createHandler(Path dir) throws IOException {
    handler = new DailyRollingFileHandler(dir.toString(), LOG_FORMAT, FILE_NAME_FORMAT, ZONE);
    return handler;
  }

  private LogRecord record(Level level, String message, ZonedDateTime time) {
    var record = new LogRecord(level, message);
    record.setInstant(time.toInstant());
    record.setLoggerName("test.logger");
    return record;
  }

  private String read(Path path) throws IOException {
    return Files.readString(path, StandardCharsets.UTF_8);
  }

  @Test
  public void constructor_createsLogDirectories() throws Exception {
    var logDir = tempDir.resolve("a").resolve("b");

    createHandler(logDir);

    assertThat(logDir).isDirectory();
    assertThat(logDir.resolve(handler.generateFilename(LocalDate.now(ZONE)))).exists();
  }

  @Test
  public void constructor_existingDirectory() throws Exception {
    createHandler(tempDir);

    assertThat(tempDir).isDirectory();
  }

  @Test
  public void generateFilename_replacesDate() throws Exception {
    createHandler(tempDir);

    assertThat(handler.generateFilename(LocalDate.of(2026, 11, 8))).isEqualTo("app-2026-11-08.log");
  }

  @Test
  public void publish_formatsMessage() throws Exception {
    createHandler(tempDir);
    var now = ZonedDateTime.now(ZONE);
    var record = record(Level.INFO, "hello", now);
    record.setSourceClassName("com.example.Foo");
    record.setSourceMethodName("bar");

    handler.publish(record);

    var file = tempDir.resolve(handler.generateFilename(now.toLocalDate()));
    var expected =
        String.format(
            LOG_FORMAT,
            now,
            "com.example.Foo bar",
            null,
            Level.INFO.getLocalizedName(),
            "hello",
            "");
    assertThat(read(file)).isEqualTo(expected);
  }

  @Test
  public void publish_withoutSourceClass_usesLoggerName() throws Exception {
    createHandler(tempDir);
    var now = ZonedDateTime.now(ZONE);

    handler.publish(record(Level.WARNING, "msg", now));

    var file = tempDir.resolve(handler.generateFilename(now.toLocalDate()));
    assertThat(read(file)).contains("[" + Level.WARNING.getLocalizedName() + "] test.logger msg");
  }

  @Test
  public void publish_withThrowable_includesStackTrace() throws Exception {
    createHandler(tempDir);
    var now = ZonedDateTime.now(ZONE);
    var record = record(Level.SEVERE, "error", now);
    record.setThrown(new IllegalStateException("boom"));

    handler.publish(record);

    var file = tempDir.resolve(handler.generateFilename(now.toLocalDate()));
    assertThat(read(file))
        .contains("error")
        .contains("java.lang.IllegalStateException: boom")
        .contains("DailyRollingFileHandlerTest");
  }

  @Test
  public void publish_acrossDays_switchesFile() throws Exception {
    createHandler(tempDir);
    var today = ZonedDateTime.now(ZONE);
    var tomorrow = today.plusDays(1);

    handler.publish(record(Level.INFO, "today-log", today));
    handler.publish(record(Level.INFO, "tomorrow-log", tomorrow));
    handler.publish(record(Level.INFO, "tomorrow-log2", tomorrow));

    var todayFile = tempDir.resolve(handler.generateFilename(today.toLocalDate()));
    var tomorrowFile = tempDir.resolve(handler.generateFilename(tomorrow.toLocalDate()));
    assertThat(read(todayFile)).contains("today-log").doesNotContain("tomorrow-log");
    assertThat(read(tomorrowFile)).contains("tomorrow-log").contains("tomorrow-log2");
    assertThat(read(tomorrowFile)).doesNotContain("today-log");
  }

  @Test
  public void publish_appendsToExistingFile() throws Exception {
    var today = LocalDate.now(ZONE);
    var file = tempDir.resolve("app-" + today + ".log");
    Files.writeString(file, "existing\n", StandardCharsets.UTF_8);
    createHandler(tempDir);

    handler.publish(record(Level.INFO, "added", ZonedDateTime.now(ZONE)));

    assertThat(read(file)).startsWith("existing\n").contains("added");
  }

  @Test
  public void logInShutdownHook_writesFormattedLog() throws Exception {
    createHandler(tempDir);

    handler.logInShutdownHook(Level.INFO, "shutdown");

    var file = tempDir.resolve(handler.generateFilename(LocalDate.now(ZONE)));
    assertThat(read(file))
        .contains("[" + Level.INFO.getLocalizedName() + "]")
        .contains("shutdown")
        .contains(getClass().getName() + " logInShutdownHook_writesFormattedLog");
  }

  @Test
  public void logInShutdownHook_appendsToExistingLog() throws Exception {
    createHandler(tempDir);
    handler.publish(record(Level.INFO, "first", ZonedDateTime.now(ZONE)));

    handler.logInShutdownHook(Level.WARNING, "second");

    var file = tempDir.resolve(handler.generateFilename(LocalDate.now(ZONE)));
    assertThat(read(file)).contains("first").contains("second");
  }

  @Test
  public void getThrowableString_noThrown_isEmpty() throws Exception {
    createHandler(tempDir);

    assertThat(handler.getThrowableString(new LogRecord(Level.INFO, "x"))).isEmpty();
  }

  @Test
  public void getSource_variants() throws Exception {
    createHandler(tempDir);
    var record = new LogRecord(Level.INFO, "x");
    record.setLoggerName("logger");
    assertThat(handler.getSource(record)).isEqualTo("logger");

    record.setSourceClassName("C");
    assertThat(handler.getSource(record)).isEqualTo("C");

    record.setSourceMethodName("m");
    assertThat(handler.getSource(record)).isEqualTo("C m");
  }
}
