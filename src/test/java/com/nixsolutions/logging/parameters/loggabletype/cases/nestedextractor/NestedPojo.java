package com.nixsolutions.logging.parameters.loggabletype.cases.nestedextractor;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.EXTRACTOR_FIRST)
public class NestedPojo
{
  @LoggableType.property
  public String field1 = "POJO_B2_FIELD_1";
}
