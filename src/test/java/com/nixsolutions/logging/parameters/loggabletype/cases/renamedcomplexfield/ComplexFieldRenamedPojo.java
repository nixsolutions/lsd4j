package com.nixsolutions.logging.parameters.loggabletype.cases.renamedcomplexfield;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonProperty;

@LoggableType
public class ComplexFieldRenamedPojo implements BasePojo
{
  @JsonProperty("pojob16")
  @LoggableType.property(name = "pojob16")
  public ComplexPojo field1 = new ComplexPojo();
}
