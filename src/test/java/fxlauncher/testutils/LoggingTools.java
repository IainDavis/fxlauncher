package fxlauncher.testutils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggingTools {

  public static Logger initTestLog(Logger log) {
    log.setUseParentHandlers(false);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(
        new Formatter() {

          @Override
          public String format(LogRecord record) {
            return String.format("\t[TEST LOG] %s: %s\n", record.getLevel(), record.getMessage());
          }
        });
    log.addHandler(handler);
    return log;
  }
}
