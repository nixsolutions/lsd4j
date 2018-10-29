package com.nixsolutions.logging.advice.handler.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.advice.handler.LogActionHandler;
import com.nixsolutions.logging.annotation.Log;

public abstract class AbstractLogActionHandler implements LogActionHandler
{
  public static final String EXCEPTION_PARAM = "exceptionThrown";
  public static final String START_MOMENT_PARAM = "startMoment";
  public static final String FINISH_MOMENT_PARAM = "finishMoment";
  public static final String INVOCATION_RESULT_PARAM = "invocationResult";
  public static final String METHOD_ARGS_PARAM = "methodArgs";
  public static final String METHOD_PARAM = "method";

  protected PrettyLoggable prettyLoggable;

  protected AbstractLogActionHandler(PrettyLoggable prettyLoggable)
  {
    this.prettyLoggable = prettyLoggable;
  }

  protected boolean isApplicable(Method method, Class<? extends Annotation> desiredAnnotation)
  {
    return method.isAnnotationPresent(Log.class)
        && method.isAnnotationPresent(desiredAnnotation);
  }
}
