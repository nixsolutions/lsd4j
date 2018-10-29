package com.nixsolutions.logging;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class LogContextDefault implements LogContext<Long, String> {
  private static final String KEY_VALUE_DELIMITER = "=";
  private static final String KEY_VALUE_PAIRS_DELIMITER = ", ";
  private static final String MESSAGE_DELIMITER = "ctx:";
  private static final String CONTEXT_KEY = "key";
  private static final String PARAMS_PREFIX = "{";
  private static final String PARAMS_SUFFIX = "}";
  private static final String COMPOSITE_FIELD_PREFIX = "[";
  private static final String COMPOSITE_FIELD_SUFFIX = "]";

  @Override
  public String get(Long context) {
    return get(StringUtils.EMPTY, context);
  }

  @Override
  public String get(String message, Long context) {
    return get(message, context, Collections.emptyMap());
  }

  @Override
  public String get(String message, Long context, Map<String, Object> params) {
    String contextPrefix = StringUtils.isNotBlank(message) ? ". " + MESSAGE_DELIMITER : MESSAGE_DELIMITER;
    String info = getContextInfoStream(params, context)
        .collect(Collectors.joining(KEY_VALUE_PAIRS_DELIMITER, PARAMS_PREFIX, PARAMS_SUFFIX));

    return String.join(contextPrefix, StringUtils.defaultIfBlank(message, StringUtils.EMPTY), info);
  }

  @Override
  public String get(String message, Map<String, Object> params) {
    return get(message, null, params);
  }

  @Override
  public Map<String, Object> shrinkParamsAsField(Map<String, Object> contextParams, String fieldName) {
    String fieldValue = contextParams.entrySet().stream()
        .map(entry -> String.join(KEY_VALUE_DELIMITER, entry.getKey(), String.valueOf(entry.getValue())))
        .collect(Collectors.joining(KEY_VALUE_PAIRS_DELIMITER, COMPOSITE_FIELD_PREFIX, COMPOSITE_FIELD_SUFFIX));
    Map<String, Object> nestedMap = new LinkedHashMap<>();
    nestedMap.put(fieldName, fieldValue);
    return Collections.unmodifiableMap(nestedMap);
  }

  private Stream<String> getContextInfoStream(Map<String, Object> params, Long context) {
    Map<String, Object> newQuoteInfo = addInfo(params, context);
    return newQuoteInfo.entrySet()
        .stream()
        .map(entry -> String.join(KEY_VALUE_DELIMITER, entry.getKey(), String.valueOf(entry.getValue())));
  }

  private Map<String, Object> addInfo(Map<String, Object> quoteInfo, Long context) {
    Map<String, Object> infoMap = asMap(context);
    Map<String, Object> newInfo = Optional.ofNullable(quoteInfo)
        .map(LinkedHashMap::new)
        .orElseGet(LinkedHashMap::new);

    infoMap.putAll(newInfo);
    return infoMap;
  }

  private Map<String, Object> asMap(Long context) {
    Map<String, Object> info = new LinkedHashMap<>();

    if (context == null || context == 0L) {
      return info;
    }

    info.put(CONTEXT_KEY, String.valueOf(context));
    return info;
  }

}