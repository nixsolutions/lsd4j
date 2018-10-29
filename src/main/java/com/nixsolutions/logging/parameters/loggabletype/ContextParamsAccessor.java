package com.nixsolutions.logging.parameters.loggabletype;

import java.util.Map;

public interface ContextParamsAccessor
{
  Map<String, Object> extractParams();
}
