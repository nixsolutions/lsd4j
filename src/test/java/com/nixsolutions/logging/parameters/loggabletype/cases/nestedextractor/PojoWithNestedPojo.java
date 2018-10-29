package com.nixsolutions.logging.parameters.loggabletype.cases.nestedextractor;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithNestedPojo implements BasePojo
{
  @LoggableType.property
  public String field1 = "POJO_A2_FIELD_1";

  @LoggableType.property
  public NestedPojo pojoB2 = new NestedPojo();
}
