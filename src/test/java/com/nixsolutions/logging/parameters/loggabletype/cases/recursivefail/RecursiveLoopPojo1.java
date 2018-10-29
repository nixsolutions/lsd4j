package com.nixsolutions.logging.parameters.loggabletype.cases.recursivefail;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType
public class RecursiveLoopPojo1 implements BasePojo
{
  public RecursiveLoopPojo1()
  {
    pojoB15 = new RecursiveLoopPojo2();
    pojoB15.pojoA15 = this;
  }

  @LoggableType.property
  public RecursiveLoopPojo2 pojoB15;
}
