package com.nixsolutions.logging.parameters.loggabletype.cases.donothinglookup;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.DO_NOTHING)
public class DoNothingLookupPojo implements BasePojo
{
  @JsonIgnore
  @LoggableType.property
  public String field1 = "POJO_A17_FIELD_1";
}
