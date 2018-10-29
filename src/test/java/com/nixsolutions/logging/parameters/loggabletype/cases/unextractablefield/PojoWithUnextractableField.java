package com.nixsolutions.logging.parameters.loggabletype.cases.unextractablefield;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithUnextractableField implements BasePojo
{
  @LoggableType.property
  public UnextractablePojo foreignPojo = new UnextractablePojo();
}
