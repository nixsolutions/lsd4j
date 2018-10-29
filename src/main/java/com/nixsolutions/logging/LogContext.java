package com.nixsolutions.logging;

import java.util.Map;

/**
 * @param <T> defines a type of a primary context(f.e. for QuoteLogContext it is probably a quoteId:
 *            {@link java.lang.Long})
 * @param <E> defines a result of a context info processed(filtered,added, etc). Example: {@link java.lang.String} to
 *            print in log
 */
public interface LogContext<T, E>
{
  E get(T context);

  E get(String message, T context);

  E get(String message, T context, Map<String, Object> params);

  E get(String message, Map<String, Object> params);

  /**
   * Compresses params into a single field
   *
   * @param contextParams - map of params to shrink
   * @param fieldName     - resulting field name
   * @return resulting map
   * Example: {quoteId: 123, groupId: 122}, "groupQtContext" -> {groupQtContext: [quoteId: 123, groupId: 122]} <br>
   */
  Map<String, Object> shrinkParamsAsField(Map<String, Object> contextParams, String fieldName);
}
