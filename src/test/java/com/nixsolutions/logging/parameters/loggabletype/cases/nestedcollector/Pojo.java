package com.nixsolutions.logging.parameters.loggabletype.cases.nestedcollector;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType
public class Pojo implements BasePojo
{
  @LoggableType.property
  public String field1 = "POJO_A3_FIELD_1";

  @LoggableType.property
  public NestedPojo pojoB3 = new NestedPojo();
}
