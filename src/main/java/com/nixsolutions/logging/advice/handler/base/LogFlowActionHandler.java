package com.nixsolutions.logging.advice.handler.base;

import static com.nixsolutions.logging.LoggingConstants.SINGLE_PROPERTY;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import com.nixsolutions.logging.PrettyLoggable;
import com.nixsolutions.logging.annotation.ContextParam;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.common.MapUtils;
import com.nixsolutions.logging.parameters.loggabletype.AnnotatedObject;
import com.nixsolutions.logging.parameters.loggabletype.LookupResult;
import com.nixsolutions.logging.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

public abstract class LogFlowActionHandler extends AbstractLogActionHandler
{
  protected AnnotationReflectionLookupUtils reflectionLookupUtils;

  public LogFlowActionHandler(PrettyLoggable prettyLoggable, AnnotationReflectionLookupUtils reflectionLookupUtils)
  {
    super(prettyLoggable);
    this.reflectionLookupUtils = reflectionLookupUtils;
  }

  protected Map<String, Object> getAdditionalContextInfo(Parameter[] methodParameters, Object[] methodArguments)
  {
    Map<String, AnnotatedObject<LoggableType>> nameAnnotatedObjectMap = MapUtils
        .toImmutableMap(methodParameters, methodArguments).entrySet().stream()
        .filter(entry -> entry.getKey().isAnnotationPresent(ContextParam.class))
        .map(this::toNameAnnotatedObject)
        .collect(toMap(Pair::getKey, Pair::getValue));

    return getLoggableTypesContextInfo(nameAnnotatedObjectMap);
  }

  protected Map<String, Object> getLoggableTypesContextInfo(
      Map<String, AnnotatedObject<LoggableType>> contextParamAnnotatedObjectMap)
  {
    return contextParamAnnotatedObjectMap.entrySet().stream()
        .map(this::toNamedLookup)
        .filter(this::isResolved)
        .map(this::toNameResult)
        .collect(toMap(Pair::getKey, Pair::getValue));
  }

  private Pair<String, LookupResult> toNamedLookup(Entry<String, AnnotatedObject<LoggableType>> entry)
  {
    return Pair.of(entry.getKey(), reflectionLookupUtils.strategyLookupForRootObj(entry.getValue()));
  }

  private boolean isResolved(Pair<String, LookupResult> entry)
  {
    return entry.getValue().isResolved();
  }

  private Pair<String, Object> toNameResult(Pair<String, LookupResult> entry)
  {
    Map<String, Object> contextParams = entry.getRight().executeForResult();
    if (isSinglePropertyContextParams(contextParams))
    {
      return Pair.of(entry.getLeft(), contextParams.get(SINGLE_PROPERTY));
    }

    return Pair.of(entry.getLeft(), contextParams);
  }

  private boolean isSinglePropertyContextParams(Map<String, Object> contextParams)
  {
    return contextParams.size() == 1 && nonNull(contextParams.get(SINGLE_PROPERTY));
  }

  private Pair<String, AnnotatedObject<LoggableType>> toNameAnnotatedObject(Entry<Parameter, Object>
                                                                                parameterObjectEntry)
  {
    ContextParam annotation = parameterObjectEntry.getKey().getAnnotation(ContextParam.class);
    String paramName = parameterObjectEntry.getKey().getName();
    if (isNotBlank(annotation.value()))
    {
      paramName = annotation.value();
    }

    return Pair.of(paramName,
        AnnotatedObject.createWithAnnotation(parameterObjectEntry.getValue(), LoggableType.class));
  }
}
