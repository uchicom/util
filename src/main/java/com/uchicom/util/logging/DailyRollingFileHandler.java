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
import java.util.Objects;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * 日付ごとにログファイルを切り替えるハンドラ.
 *
 * <p>logging.propertiesから引数なしで生成する場合は、以下のプロパティを参照する。
 *
 * <ul>
 *   <li>{@code <クラス名>.logDir} (必須)
 *   <li>{@code <クラス名>.logFormat} (省略時: {@code %1$tF %1$tT [%4$s] %2$s %5$s%6$s%n})
 *   <li>{@code <クラス名>.fileNameFormat} (省略時: {@code app-%d.log})
 *   <li>{@code <クラス名>.zoneId} (省略時: システムのタイムゾーン)
 *   <li>{@code <クラス名>.level} (省略時: INFO)
 * </ul>
 */
public class DailyRollingFileHandler extends StreamHandler {

  private static final String DEFAULT_LOG_FORMAT = "%1$tF %1$tT [%4$s] %2$s %5$s%6$s%n";
  private static final String DEFAULT_FILE_NAME_FORMAT = "app-%d.log";

  private final String fileNameFormat;
  private final String logFormat;
  private final Path logDirPath;
  private LocalDate logDate;
  private final ZoneId zoneId;

  /** logging.propertiesの設定値を使用して生成する. */
  public DailyRollingFileHandler() throws IOException {
    this(LogManager.getLogManager());
  }

  private DailyRollingFileHandler(LogManager manager) throws IOException {
    this(
        required(manager, "logDir"),
        property(manager, "logFormat", DEFAULT_LOG_FORMAT),
        property(manager, "fileNameFormat", DEFAULT_FILE_NAME_FORMAT),
        ZoneId.of(property(manager, "zoneId", ZoneId.systemDefault().getId())));
    setLevel(Level.parse(property(manager, "level", Level.INFO.getName())));
  }

  private static String property(LogManager manager, String name, String defaultValue) {
    var value = manager.getProperty(DailyRollingFileHandler.class.getName() + "." + name);
    return value == null || value.isBlank() ? defaultValue : value.trim();
  }

  private static String required(LogManager manager, String name) {
    var value = property(manager, name, null);
    if (value == null) {
      throw new IllegalArgumentException(
          DailyRollingFileHandler.class.getName() + "." + name + " is not configured");
    }
    return value;
  }

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
            Objects.toString(record.getLoggerName(), ""),
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

    if (!logDate.equals(recordDate) && open(recordDate)) {
      logDate = recordDate;
    }
  }

  boolean open(LocalDate fileDate) {
    try {
      setOutputStream(createOutputStream(fileDate));
      return true;
    } catch (IOException ex) {
      reportError(null, ex, ErrorManager.GENERIC_FAILURE);
      return false;
    }
  }

  OutputStream createOutputStream(LocalDate fileDate) throws IOException {
    var path = logDirPath.resolve(generateFilename(fileDate));
    return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  String generateFilename(LocalDate date) {
    return fileNameFormat.replace("%d", DateTimeFormatter.ISO_DATE.format(date));
  }

  public void logInShutdownHook(Level level, String message) throws IOException {
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
