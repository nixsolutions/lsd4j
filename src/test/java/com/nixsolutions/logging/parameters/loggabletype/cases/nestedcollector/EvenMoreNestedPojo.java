package com.nixsolutions.logging.parameters.loggabletype.cases.nestedcollector;

import com.nixsolutions.logging.annotation.LoggableType;

@LoggableType
public class EvenMoreNestedPojo
{
  @LoggableType.property
  public String field1 = "POJO_C3_FIELD_1";
}
