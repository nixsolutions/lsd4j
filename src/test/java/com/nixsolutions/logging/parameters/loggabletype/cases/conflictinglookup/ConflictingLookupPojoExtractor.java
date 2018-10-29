package com.nixsolutions.logging.parameters.loggabletype.cases.conflictinglookup;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.parameters.extractor.ContextParamExtractor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Component
public class ConflictingLookupPojoExtractor implements ContextParamExtractor<ConflictingLookupPojo>
{
  @Override
  public Map<String, Object> extractParams(String name, ConflictingLookupPojo parameter)
  {
    return ImmutableMap.of("field1", parameter.field1);
  }

  @Override
  public List<Class<?>> getExtractableClasses()
  {
    return ImmutableList.of(ConflictingLookupPojo.class);
  }
}
