package io.sunshower.zephyr.log;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import elemental.json.impl.JsonUtil;
import io.sunshower.zephyr.ui.components.Terminal;
import lombok.val;

public class LogbackRemoteAppender extends AppenderBase<ILoggingEvent>
    implements Appender<ILoggingEvent> {

  public static final String PATTERN =
      "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(gyre-worker){magenta} %clr(---){faint} %clr([user-pool]){faint} %clr(:){faint} %m%n%wEx";
  private final Terminal terminal;
  private final PatternLayoutEncoder encoder;

  //
  public LogbackRemoteAppender(Terminal terminal, Context context) {
    this.terminal = terminal;
    this.setContext(context);
    this.encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern(PATTERN);
  }

  public void start() {
    encoder.start();
    super.start();
  }

  public void stop() {
    super.stop();
    encoder.stop();
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    val message = JsonUtil.escapeControlChars(new String(encoder.encode(eventObject)));
    terminal.write(message);
  }
}
