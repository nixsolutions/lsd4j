package com.nixsolutions.logging.parameters.loggabletype.cases.nestedcollector;

import com.nixsolutions.logging.annotation.LoggableType;

@LoggableType
public class NestedPojo
{
  @LoggableType.property
  public String field1 = "POJO_B3_FIELD_1";

  @LoggableType.property
  public EvenMoreNestedPojo pojoC3 = new EvenMoreNestedPojo();
}
