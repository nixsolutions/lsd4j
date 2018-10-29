package com.nixsolutions.logging.parameters.loggabletype.cases.caseinheritance;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class ChildPojo extends ParentPojo implements BasePojo
{
  @LoggableType.property
  public String field1 = "POJO_A9_FIELD_1";
}
