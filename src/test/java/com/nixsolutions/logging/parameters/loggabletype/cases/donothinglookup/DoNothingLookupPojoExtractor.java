package com.nixsolutions.logging.parameters.loggabletype.cases.donothinglookup;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Component
public class DoNothingLookupPojoExtractor implements ContextParamExtractor<DoNothingLookupPojo>
{
  @Override
  public Map<String, Object> extractParams(String name, DoNothingLookupPojo parameter)
  {
    return ImmutableMap.of(name, parameter.toString());
  }

  @Override
  public List<Class<?>> getExtractableClasses()
  {
    return ImmutableList.of(DoNothingLookupPojo.class);
  }
}
