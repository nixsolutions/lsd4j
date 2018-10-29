package com.nixsolutions.logging.advice.handler;

import java.util.Map;

public interface LogActionHandler
{
  void perform(Map<String, Object> params);
}
