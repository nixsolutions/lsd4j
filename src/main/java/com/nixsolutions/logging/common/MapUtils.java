package com.nixsolutions.logging.common;

import static org.apache.commons.lang3.StringUtils.defaultString;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides utility methods for Map instances.
 */
public class MapUtils
{
  private static final BinaryOperator DEFAULT_MERGE_FUNCTION = (value1, value2) -> value2;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Returns an immutable empty map if the argument is <code>null</code>,
   * or the argument itself otherwise.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @param map the map, possibly <code>null</code>
   * @return an empty map if the argument is <code>null</code>
   */
  public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map)
  {
    return map == null ? new HashMap() : map;
  }

  /**
   * Returns either the passed in map, or if the map is {@code null},
   * the value of {@code defaultMap}.
   *
   * @param <K>        the key type
   * @param <V>        the value type
   * @param map        the map, possibly <code>null</code>
   * @param defaultMap the returned values if map is {@code null}
   * @return an empty list if the argument is <code>null</code>
   * @since 4.0
   */
  public static <K, V> Map<K, V> defaultIfNull(final Map<K, V> map, final Map<K, V> defaultMap)
  {
    return map == null ? defaultMap : map;
  }

  /**
   * Map values to lower case.
   * Null values will be updated to empty strings
   */
  public static Map<String, String> lowCaseMapValues(Map<String, String> map)
  {
    return map.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            entry -> StringUtils.lowerCase(defaultString(entry.getValue()))));
  }

  /**
   * Method converts array of {@code keys} and {@code values} into an immutable map
   *
   * @param keys
   * @param values
   * @return resulting map
   */
  public static <K, V> Map<K, V> toImmutableMap(K[] keys, V[] values)
  {
    return makeMapImmutable(toMap(keys, values));
  }

  /**
   * Method converts array of {@code keys} and {@code values} into a mutable map
   *
   * @param keys   keys
   * @param values values
   * @return resulting map
   */
  public static <K, V> Map<K, V> toMutableMap(K[] keys, V[] values)
  {
    return toMap(keys, values);
  }

  private static <K, V> Map<K, V> toMap(K[] keys, V[] values)
  {
    Map<K, V> map = new HashMap<>();
    int keysLength = (keys != null) ? keys.length : 0;
    int valuesLength = (values != null) ? values.length : 0;

    if (keysLength != valuesLength)
    {
      throw new IllegalArgumentException("The number of keys doesn't match the number of values.");
    }

    for (int i = 0; i < keysLength; i++)
    {
      map.put(keys[i], values[i]);
    }

    return map;
  }

  private static <K, V> Map<K, V> makeMapImmutable(Map<K, V> map)
  {
    return Collections.unmodifiableMap(new LinkedHashMap<>(map));
  }

  /**
   * Collects maps' keys and values into new map using {@code mergeFunction} to resolve conflicts
   *
   * @param maps
   * @param mergeFunction
   * @return resulting map
   */
  @SafeVarargs
  public static <K, V> Map<K, V> mergeMaps(BinaryOperator<V> mergeFunction, Map<K, V>... maps)
  {
    return Arrays.stream(maps)
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
  }

  /**
   * Collects maps' keys and values into new map using default merge function: {@code (v1, v2) -> v2}
   * which works as default i.e. rewriting values under non-unique keys.
   *
   * @param maps
   * @return resulting map
   */
  @SafeVarargs
  public static <K, V> Map<K, V> mergeMaps(Map<K, V>... maps)
  {
    return mergeMaps(DEFAULT_MERGE_FUNCTION, maps);
  }

  /**
   * Method converts {@code params} context map <String, Object> into JSON String
   *
   * @param params context map
   * @return JSON String
   * */
  public static String convertMapToJson(Map<String, Object> params) throws JsonProcessingException
  {
    return OBJECT_MAPPER.writer().writeValueAsString(params);
  }
}
