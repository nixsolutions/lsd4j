package com.nixsolutions.logging.parameters.loggabletype.cases.conflictinglookup;

import static com.nixsolutions.logging.parameters.loggabletype.ExtractionResolutionStrategy.RAISE_EX_ON_CONFLICT;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType(resolutionStrategy = RAISE_EX_ON_CONFLICT)
public class ConflictingLookupPojo implements BasePojo
{
  @LoggableType.property
  public String field1 = "POJO_A12_FIELD_1";
}
