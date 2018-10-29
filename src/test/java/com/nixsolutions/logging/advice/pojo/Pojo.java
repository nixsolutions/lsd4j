package com.nixsolutions.logging.advice.pojo;

import static com.nixsolutions.logging.advice.LoggingResultHelper.PARAM_LONG;
import static com.nixsolutions.logging.advice.LoggingResultHelper.PARAM_STR;
import com.nixsolutions.logging.annotation.LoggableType;

@LoggableType
public class Pojo
{
  @LoggableType.property
  public String strParam = PARAM_STR;
  @LoggableType.property
  public Long longParam = PARAM_LONG;
  @LoggableType.property
  public EnumType enumParam = EnumType.ENUM_PARAM;
}
