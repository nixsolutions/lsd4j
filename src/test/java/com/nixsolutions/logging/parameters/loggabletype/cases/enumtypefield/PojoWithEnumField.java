package com.nixsolutions.logging.parameters.loggabletype.cases.enumtypefield;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithEnumField implements BasePojo
{
  @LoggableType.property
  public EnumType foreignPojo = EnumType.ALPHA;
}
