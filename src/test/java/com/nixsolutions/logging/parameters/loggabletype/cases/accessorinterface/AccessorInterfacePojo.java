package com.nixsolutions.logging.parameters.loggabletype.cases.accessorinterface;

import java.util.Map;
import com.nixsolutions.logging.annotation.LoggableType;
import com.nixsolutions.logging.parameters.loggabletype.ContextParamsAccessor;
import com.nixsolutions.logging.parameters.loggabletype.cases.BasePojo;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class AccessorInterfacePojo implements BasePojo, ContextParamsAccessor
{
  public String field1 = "POJO_A4_FIELD_1";

  @Override
  public Map<String, Object> extractParams()
  {
    return ImmutableMap.of("field1", field1);
  }
}
