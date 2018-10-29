package com.nixsolutions.logging;

import org.slf4j.Logger;

public class ObjectlessPrettyLoggable implements PrettyLoggable
{
  private Logger logger;
  private LogContext logContext;

  public ObjectlessPrettyLoggable(Logger logger, LogContext logContext)
  {
    this.logger = logger;
    this.logContext = logContext;
  }

  @Override
  public Logger getCurrentLogger()
  {
    return logger;
  }

  @Override
  public LogContext getLogContext()
  {
    return logContext;
  }
}
