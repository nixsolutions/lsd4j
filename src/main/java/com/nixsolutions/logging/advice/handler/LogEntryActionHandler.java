package com.nixsolutions.logging.advice.handler;

import java.lang.reflect.Method;
import java.util.Map;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.advice.handler.base.LogFlowActionHandler;
import com.nixsolutions.logging.annotation.Log;
import com.nixsolutions.logging.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

public class LogEntryActionHandler extends LogFlowActionHandler
{
  public LogEntryActionHandler(PrettyLoggable prettyLoggable,
                               AnnotationReflectionLookupUtils reflectionLookupUtils)
  {
    super(prettyLoggable, reflectionLookupUtils);
  }

  @Override
  public void perform(Map<String, Object> params)
  {
    Method method = (Method) params.get(METHOD_PARAM);
    if (!isApplicable(method, Log.entry.class))
    {
      return;
    }

    Object[] args = (Object[]) params.get(METHOD_ARGS_PARAM);

    prettyLoggable.logDebug(method.getName() + "() -- >", getAdditionalContextInfo(method.getParameters(), args));
  }
}
