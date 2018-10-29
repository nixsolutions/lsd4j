package com.nixsolutions.logging.parameters.loggabletype.cases.renamedfield;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonProperty;

@LoggableType
public class RenamedFieldPojo implements BasePojo
{
  @JsonProperty("field1Renamed")
  @LoggableType.property(name = "field1Renamed")
  public String field1 = "POJO_A8_FIELD_1_RENAMED";
}
