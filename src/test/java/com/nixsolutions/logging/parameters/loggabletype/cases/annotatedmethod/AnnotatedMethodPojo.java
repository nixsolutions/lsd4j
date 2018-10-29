package com.nixsolutions.logging.parameters.loggabletype.cases.annotatedmethod;

import java.util.Map;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class AnnotatedMethodPojo implements BasePojo
{
  public String field1 = "POJO_A5_FIELD_1";

  @JsonIgnore
  @LoggableType.extractionMethod
  public Map<String, Object> getLogParams()
  {
    return ImmutableMap.of("field1", field1);
  }
}
