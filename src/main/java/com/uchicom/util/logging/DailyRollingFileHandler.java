// (C) 2025 uchicom
package com.uchicom.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class DailyRollingFileHandler extends StreamHandler {

  private final String fileNameFormat;
  private final String logFormat;
  private final Path logDirPath;
  private LocalDate logDate;
  private final ZoneId zoneId;

  public DailyRollingFileHandler(
      String logDir, String logFormat, String fileNameFormat, ZoneId zoneId) throws IOException {
    this.fileNameFormat = fileNameFormat;
    this.logFormat = logFormat;
    this.logDirPath = Paths.get(logDir);
    this.logDate = LocalDate.now(zoneId);
    this.zoneId = zoneId;
    init();
  }

  void init() throws IOException {
    createLogDirectories();
    setFormatter(createFormatter());
    open(logDate);
  }

  void createLogDirectories() throws IOException {
    if (!Files.exists(logDirPath)) {
      Files.createDirectories(logDirPath);
    }
  }

  Formatter createFormatter() {
    return new Formatter() {
      @Override
      public String format(LogRecord record) {
        var zdt = ZonedDateTime.ofInstant(record.getInstant(), zoneId);
        var source = getSource(record);
        var throwable = getThrowableString(record);
        var message = formatMessage(record);
        return String.format(
            logFormat,
            zdt,
            source,
            record.getLoggerName(),
            record.getLevel().getLocalizedName(),
            message,
            throwable);
      }
    };
  }

  String getSource(LogRecord record) {
    String source = null;
    if (record.getSourceClassName() != null) {
      source = record.getSourceClassName();
      if (record.getSourceMethodName() != null) {
        source += " " + record.getSourceMethodName();
      }
    } else {
      source = record.getLoggerName();
    }
    return source;
  }

  String getThrowableString(LogRecord record) {
    String throwable = "";
    if (record.getThrown() != null) {
      var sw = new StringWriter();
      var pw = new PrintWriter(sw);
      pw.println();
      record.getThrown().printStackTrace(pw);
      pw.close();
      throwable = sw.toString();
    }
    return throwable;
  }

  @Override
  public synchronized void publish(LogRecord record) {
    changeLogFile(record);
    super.publish(record);
    flush();
  }

  void changeLogFile(LogRecord record) {
    var recordDate = LocalDate.ofInstant(record.getInstant(), zoneId);

    if (!logDate.equals(recordDate)) {
      open(recordDate);
      logDate = recordDate;
    }
  }

  void open(LocalDate fileDate) {
    try {
      setOutputStream(createOutputStream(fileDate));
    } catch (IOException ex) {
      reportError(null, ex, ErrorManager.GENERIC_FAILURE);
    }
  }

  OutputStream createOutputStream(LocalDate fileDate) throws IOException {
    var path = logDirPath.resolve(generateFilename(fileDate));
    return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  String generateFilename(LocalDate date) {
    return fileNameFormat.replaceAll("%d", DateTimeFormatter.ISO_DATE.format(date));
  }

  void logInShutdownHook(Level level, String message) throws IOException {
    var record = new LogRecord(level, message);
    var elem = Thread.currentThread().getStackTrace()[2];
    record.setSourceClassName(elem.getClassName());
    record.setSourceMethodName(elem.getMethodName());
    var log = getFormatter().format(record);
    var zdt = ZonedDateTime.ofInstant(record.getInstant(), zoneId);
    try (var os = createOutputStream(zdt.toLocalDate())) {
      os.write(log.getBytes(StandardCharsets.UTF_8));
    }
  }
}
