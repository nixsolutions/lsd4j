package com.nixsolutions.logging.parameters.loggabletype.util;

import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.EXCEPTIONAL;
import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.RESOLVED;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.List;
import com.nixsolutions.logging.parameters.loggabletype.LookupResult;

public class LookupUtils
{

  public static LookupResult resultingLookup(LookupResult... lookupOrder)
  {
    LookupResult errorLookup = errorLookup(lookupOrder);
    if (nonNull(errorLookup))
    {
      return errorLookup;
    }

    return firstSpecificLookup(RESOLVED, lookupOrder);
  }

  public static LookupResult errorLookup(LookupResult... lookupOrder)
  {
    return firstSpecificLookup(EXCEPTIONAL, lookupOrder);
  }

  public static LookupResult firstSpecificLookup(LookupResult.LookupType lookupType, LookupResult... lookupOrder)
  {
    if (lookupOrder.length == 1)
    {
      return lookupOrder[0];
    }
    return Arrays.stream(lookupOrder)
        .filter(lookupResult -> lookupResult.isCertainLookupType(lookupType))
        .findFirst()
        .orElse(null);
  }

  public static LookupResult conflictingLookup(LookupResult onConflict, LookupResult... results)
  {
    List<LookupResult> lookupResults = Arrays.stream(results)
        .filter(LookupResult::isResolved)
        .collect(toList());

    if (lookupResults.size() > 1)
    {
      return onConflict;
    }

    return lookupResults.get(0);
  }
}
