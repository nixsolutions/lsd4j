package com.nixsolutions.logging.parameters.loggabletype.cases.caseinheritance;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class ParentPojo extends GrandParentPojo implements BasePojo
{
  @LoggableType.property
  public String field1b2 = "POJO_A9_BASE_2_FIELD_1b2";
}
