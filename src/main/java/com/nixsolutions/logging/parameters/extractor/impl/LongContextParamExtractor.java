package com.nixsolutions.logging.parameters.extractor.impl;

import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;

@Component
public class LongContextParamExtractor implements ContextParamExtractor<Long>
{
  @Override
  public Map<String, Object> extractParams(String name, Long parameterValue)
  {
    return unmodifiableMap(singletonMap(name, String.valueOf(parameterValue)));
  }

  @Override
  public List<Class<?>> getExtractableClasses()
  {
    return unmodifiableList(Arrays.asList(Long.class, long.class));
  }
}
