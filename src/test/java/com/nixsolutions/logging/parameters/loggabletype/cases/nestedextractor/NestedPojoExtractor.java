package com.nixsolutions.logging.parameters.loggabletype.cases.nestedextractor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;
import com.google.common.collect.ImmutableMap;

@Component
public class NestedPojoExtractor implements ContextParamExtractor<NestedPojo>
{
  @Override
  public Map<String, Object> extractParams(String name, NestedPojo parameter)
  {
    if (name.isEmpty())
    {
      return ImmutableMap.of(
          "field1", parameter.field1);
    }
    else
      return ImmutableMap.of();
  }

  @Override
  public List<Class<?>> getExtractableClasses()
  {
    return Arrays.asList(NestedPojo.class);
  }
}
