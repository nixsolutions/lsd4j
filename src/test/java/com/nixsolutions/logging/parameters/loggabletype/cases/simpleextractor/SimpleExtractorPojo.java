package com.nixsolutions.logging.parameters.loggabletype.cases.simpleextractor;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.EXTRACTOR_FIRST)
public class SimpleExtractorPojo implements BasePojo
{
  public String field1 = "POJO_A1_FIELD_1";
}
