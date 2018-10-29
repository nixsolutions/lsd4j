package com.nixsolutions.logging.advice.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.LogContext;
import com.nixsolutions.logging.ObjectlessPrettyLoggable;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractorFactory;
import com.nixsolutions.logging.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

@Component
public class LogActionHandlerFactory
{
  private AnnotationReflectionLookupUtils reflectionLookupUtils;
  private ContextParamExtractorFactory contextParamExtractorFactory;
  private LogContext<Long, String> logContext;

  @Autowired
  public LogActionHandlerFactory(AnnotationReflectionLookupUtils reflectionLookupUtils,
                                 ContextParamExtractorFactory contextParamExtractorFactory,
                                 LogContext<Long, String> logContext)
  {
    this.reflectionLookupUtils = reflectionLookupUtils;
    this.contextParamExtractorFactory = contextParamExtractorFactory;
    this.logContext = logContext;
  }

  public LogActionHandler createExectimeHandler(Logger logger)
  {
    PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
    return new LogExectimeActionHandler(prettyLoggable);
  }

  public LogActionHandler createEntryHandler(Logger logger)
  {
    PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
    return new LogEntryActionHandler(prettyLoggable, reflectionLookupUtils);
  }

  public LogActionHandler createExitHandler(Logger logger)
  {
    PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
    return new LogExitActionHandler(prettyLoggable, reflectionLookupUtils);
  }
}
