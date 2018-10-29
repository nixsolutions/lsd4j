package com.nixsolutions.logging.parameters.extractor.impl;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;

@Component
public class StringContextParamExtractor implements ContextParamExtractor<String>
{
  @Override
  public Map<String, Object> extractParams(String name, String parameter)
  {
    return unmodifiableMap(singletonMap(name, parameter));
  }

  @Override
  public List<Class<?>> getExtractableClasses()
  {
    return unmodifiableList(singletonList(String.class));
  }
}
