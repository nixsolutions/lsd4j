package com.nixsolutions.logging;

import java.util.Map;
import org.slf4j.Logger;

public interface PrettyLoggable<T>
{
  Logger getCurrentLogger();

  LogContext<T, String> getLogContext();

  default void logDebug(String message, T context)
  {
    getCurrentLogger().debug(getLogContext().get(message, context));
  }

  default void logDebug(String message, Map<String, Object> customContext)
  {
    getCurrentLogger().debug(getLogContext().get(message, customContext));
  }

  default void logDebug(String message, T context, Map<String, Object> customContext)
  {
    getCurrentLogger().debug(getLogContext().get(message, context, customContext));
  }

  default void logError(String message, T context, Exception e)
  {
    getCurrentLogger().error(getLogContext().get(message, context), e);
  }

  default void logError(String message, Map<String, Object> customContext, Exception e)
  {
    getCurrentLogger().error(getLogContext().get(message, customContext), e);
  }

  default void logError(String message, T context, Map<String, Object> customContext, Exception e)
  {
    getCurrentLogger().error(getLogContext().get(message, context, customContext), e);
  }
}
