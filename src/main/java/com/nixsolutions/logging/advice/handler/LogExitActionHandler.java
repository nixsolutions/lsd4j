package com.nixsolutions.logging.advice.handler;

import static com.nixsolutions.logging.LoggingConstants.RETURNED_RESULT;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.advice.handler.base.LogFlowActionHandler;
import com.nixsolutions.logging.annotation.Log;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.AnnotatedObject;
import com.nixsolutions.logging.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

public class LogExitActionHandler extends LogFlowActionHandler
{

  private AnnotationReflectionLookupUtils reflectionLookupUtils;

  public LogExitActionHandler(PrettyLoggable prettyLoggable, AnnotationReflectionLookupUtils reflectionLookupUtils)
  {
    super(prettyLoggable, reflectionLookupUtils);
  }

  @Override
  public void perform(Map<String, Object> params)
  {
    Method method = (Method) params.get(METHOD_PARAM);
    if (!isApplicable(method, Log.exit.class))
    {
      return;
    }

    Exception exception = (Exception) params.get(EXCEPTION_PARAM);
    if (nonNull(exception))
    {
      prettyLoggable.logError(exception.getMessage(), EMPTY_MAP, exception);
      return;
    }

    Map<String, Object> contextParams = getContextParams(method, params.get(INVOCATION_RESULT_PARAM));
    prettyLoggable.logDebug("< -- " + method.getName() + "()", contextParams);
  }

  private Map<String, Object> getContextParams(Method method, Object invocationResult)
  {
    if (isVoidReturn(method.getReturnType()))
    {
      return Collections.singletonMap(RETURNED_RESULT, "void");
    }

    return getLoggableTypesContextInfo(singletonMap(RETURNED_RESULT,
        AnnotatedObject.createWithAnnotation(invocationResult, LoggableType.class)));
  }

  private boolean isVoidReturn(Class<?> returnType)
  {
    return returnType.equals(void.class);
  }
}
