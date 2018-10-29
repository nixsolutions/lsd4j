package com.nixsolutions.logging.parameters.loggabletype.cases.repeatedfieldnames;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class RepeatedFieldnamesPojo extends RepatedFieldsParentPojo implements BasePojo
{
  @LoggableType.property
  public String field1 = "POJO_A11_FIELD_1";
}
