package com.nixsolutions.logging.parameters.loggabletype.cases.multipleannotatedmethods;

import java.util.Map;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class MultipleAnnotatedMethodsPojo implements BasePojo
{

  @LoggableType.extractionMethod
  public Map<String, Object> extract1()
  {
    return ImmutableMap.of();
  }

  @LoggableType.extractionMethod
  public Map<String, Object> extract2()
  {
    return ImmutableMap.of();
  }

}
