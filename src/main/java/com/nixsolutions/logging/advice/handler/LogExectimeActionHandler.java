package com.nixsolutions.logging.advice.handler;

import static com.nixsolutions.logging.LoggingConstants.DURATION;
import static com.nixsolutions.logging.LoggingConstants.TASK_NAME;
import static com.nixsolutions.logging.LoggingConstants.TIME_LOGGING_CONTEXT;
import static com.nixsolutions.logging.LoggingConstants.TIME_UNIT;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.advice.handler.base.AbstractLogActionHandler;
import com.nixsolutions.logging.annotation.Log;
import com.nixsolutions.logging.common.WordUtils;

public class LogExectimeActionHandler extends AbstractLogActionHandler
{
  public LogExectimeActionHandler(PrettyLoggable prettyLoggable)
  {
    super(prettyLoggable);
  }

  @Override
  public void perform(Map<String, Object> params)
  {
    Method method = (Method) params.get(METHOD_PARAM);
    if (!isApplicable(method, Log.exectime.class))
    {
      return;
    }

    Long startTime = (Long) params.get(START_MOMENT_PARAM);
    Long endTime = (Long) params.get(FINISH_MOMENT_PARAM);
    TimeUnit timeUnit = method.getAnnotation(Log.exectime.class).timeUnit();
    String taskName = getTaskNameIfPresentOrMethodName(method);

    Map<String, Object> durationContextInfo = getDurationAsContextInfo(taskName, timeUnit, startTime, endTime);

    prettyLoggable.logDebug("execution finished", durationContextInfo);
  }

  private Map<String, Object> getDurationAsContextInfo(String taskName, TimeUnit timeUnit, long beforeCall, long
      afterCall)
  {
    Map<String, Object> timeLoggingContext = new HashMap<>();

    timeLoggingContext.put(TIME_UNIT, timeUnit.name());
    timeLoggingContext.put(TASK_NAME, taskName);
    timeLoggingContext.put(DURATION, getDesiredDurationFromNanoseconds(beforeCall, afterCall, timeUnit));

    return singletonMap(TIME_LOGGING_CONTEXT, timeLoggingContext);
  }

  private String getTaskNameIfPresentOrMethodName(Method method)
  {
    Log.exectime annotation = method.getAnnotation(Log.exectime.class);

    if (isNotEmpty(annotation.taskName()))
    {
      return WordUtils.toCamelCase(annotation.taskName(), SPACE);
    }
    return method.getName();
  }

  private long getDesiredDurationFromNanoseconds(long start, long end, TimeUnit timeUnit)
  {
    return timeUnit.convert(end - start, NANOSECONDS);
  }
}
