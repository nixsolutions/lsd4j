package com.nixsolutions.logging.parameters.loggabletype.cases.notannotatedfields;

import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType
public class NotAnnotatedFieldsPojo implements BasePojo
{
  @JsonIgnore
  public String field1 = "field1";

  @JsonIgnore
  public String field2 = "field2";
}
