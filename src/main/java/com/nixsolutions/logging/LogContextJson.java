package com.nixsolutions.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import com.nixsolutions.logging.common.MapUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

public class LogContextJson implements LogContext<Long, String>
{
  private static final String CONTEXT_ID_KEY = "key";
  private static final String CONTEXT_KEY = "ctx";
  private static final String MESSAGE_KEY = "message";

  @Override
  public String get(Long context)
  {
    return get(StringUtils.EMPTY, context);
  }

  @Override
  public String get(String message, Long context)
  {
    return get(message, context, Collections.emptyMap());
  }

  @Override
  public String get(String message, Long context, Map<String, Object> params)
  {
    Map<String, Object> logInfo = asMap(message);
    Map<String, Object> contextInfo = getContextInfo(params, context);
    logInfo.put(CONTEXT_KEY, contextInfo);

    return createJsonLogMessage(logInfo);
  }

  @Override
  public String get(String message, Map<String, Object> params)
  {
    return get(message, null, params);
  }

  @Override
  public Map<String, Object> shrinkParamsAsField(
      Map<String, Object> contextParams,
      String fieldName)
  {
    String fieldValue;
    try
    {
      fieldValue = MapUtils.convertMapToJson(contextParams);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException("Can't convert context map to json.");
    }

    Map<String, String> nestedMap = new LinkedHashMap<>();
    nestedMap.put(fieldName, fieldValue);

    return Collections.unmodifiableMap(nestedMap);
  }

  private Map<String, Object> getContextInfo(Map<String, Object> params, Long context)
  {
    return addInfo(params, context);
  }

  private Map<String, Object> addInfo(Map<String, Object> quoteInfo, Long context)
  {
    Map<String, Object> infoMap = asMap(context);
    Map<String, Object> newInfo = Optional.ofNullable(quoteInfo)
        .map(LinkedHashMap::new)
        .orElseGet(LinkedHashMap::new);

    infoMap.putAll(newInfo);
    return infoMap;
  }

  private String createJsonLogMessage(Map<String, Object> logMap)
  {
    try
    {
      return MapUtils.convertMapToJson(logMap);
    }
    catch (JsonProcessingException e)
    {
      throw new RuntimeException("Can't convert context map to json.");
    }
  }

  private Map<String, Object> asMap(Long context)
  {
    Map<String, Object> info = new LinkedHashMap<>();

    if (context == null || context == 0L)
    {
      return info;
    }

    info.put(CONTEXT_ID_KEY, String.valueOf(context));
    return info;
  }

  private Map<String, Object> asMap(String message)
  {
    Map<String, Object> messageMap = new LinkedHashMap<>();

    if (StringUtils.isBlank(message))
    {
      return messageMap;
    }

    messageMap.put(MESSAGE_KEY, message);
    return messageMap;
  }
}
