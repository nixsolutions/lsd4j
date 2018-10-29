package com.nixsolutions.logging.parameters.loggabletype.cases.recursivefail;

import com.nixsolutions.logging.annotation.LoggableType;

@LoggableType
public class RecursiveLoopPojo2
{
  @LoggableType.property
  public RecursiveLoopPojo1 pojoA15;
}
