package com.nixsolutions.logging.parameters.loggabletype.cases.annotatedmethodfails;

import java.util.Map;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType
public class AnnotatedMEthodFailsPojo implements BasePojo
{
  public String field1 = "POJO_A10_FIELD_1";

  @JsonIgnore
  @LoggableType.extractionMethod
  public Map<String, Object> getLogParams()
  {
    throw new RuntimeException();
  }
}
