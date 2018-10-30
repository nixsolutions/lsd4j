package com.nixsolutions.logging.parameters.loggabletype;

import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.EXCEPTIONAL;
import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.LAZY;
import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.RESOLVED;
import static com.nixsolutions.logging.parameters.loggabletype.LookupResult.LookupType.UNRESOLVED;
import static java.util.Objects.isNull;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import com.nixsolutions.logging.parameters.loggabletype.exception.UnresolvedLookupException;

public class LookupResult
{

  private static class ResultAccessor
  {
    private Function<Object, Map<String, Object>> extractionFunction;
    private Object object;

    private Supplier<Map<String, Object>> extractionSupplier;

    private ResultAccessor(Function<Object, Map<String, Object>> extractionFunction, Object object)
    {
      this.extractionFunction = extractionFunction;
      this.object = object;
    }

    private ResultAccessor(Supplier<Map<String, Object>> extractionSupplier)
    {
      this.extractionSupplier = extractionSupplier;
    }

    public static ResultAccessor from(Function<Object, Map<String, Object>> extractionFunction, Object object)
    {
      return new ResultAccessor(extractionFunction, object);
    }

    public static ResultAccessor from(Supplier<Map<String, Object>> extractionSupplier)
    {
      return new ResultAccessor(extractionSupplier);
    }

    public Map<String, Object> accessResult()
    {
      return isNull(extractionSupplier) ?
          extractionFunction.apply(object) :
          extractionSupplier.get();
    }
  }

  public enum LookupType
  {
    EXCEPTIONAL, RESOLVED, UNRESOLVED, LAZY
  }

  private ResultAccessor resultAccessor;
  private LookupType lookupType;
  private Supplier<LookupResult> wrappedLookupResultSupplier;

  private LookupResult(Supplier<LookupResult> wrappedLookupResultSupplier)
  {
    this.wrappedLookupResultSupplier = wrappedLookupResultSupplier;
    this.lookupType = LAZY;
  }

  private LookupResult(ResultAccessor resultAccessor)
  {
    this(resultAccessor, LAZY);
  }

  private LookupResult(ResultAccessor resultAccessor, LookupType lookupType)
  {
    this.resultAccessor = resultAccessor;
    this.lookupType = lookupType;
  }

  public boolean isCertainLookupType(LookupType lookupType)
  {
    unWrapLazyLookup();
    return this.lookupType.equals(lookupType);
  }

  /**
   * @return
   * @throws UnresolvedLookupException if lookup Is unresolved
   * @throws RuntimeException         for all user defined exceptions
   */
  public Map<String, Object> executeForResult()
  {
    LookupResult finalLookup = unWrapLazyLookup();
    return finalLookup.resultAccessor.accessResult();
  }

  private LookupResult unWrapLazyLookup()
  {
    LookupResult finalLookup = this;
    while (finalLookup.lookupType.equals(LAZY))
    {
      finalLookup = finalLookup.wrappedLookupResultSupplier.get();
    }

    this.lookupType = finalLookup.getLookupType();
    this.resultAccessor = finalLookup.resultAccessor;

    return finalLookup;
  }

  public static LookupResult createResolved(Supplier<Map<String, Object>> extractionSupplier)
  {
    return new LookupResult(ResultAccessor.from(extractionSupplier), RESOLVED);
  }

  public static LookupResult createResolved(Function<Object, Map<String, Object>> extractionFunction, Object object)
  {
    return new LookupResult(ResultAccessor.from(extractionFunction, object), RESOLVED);
  }

  public static LookupResult createUnresolved()
  {
    return new LookupResult(ResultAccessor.from(() ->
    {
      throw new UnresolvedLookupException();
    }), UNRESOLVED);
  }

  public static LookupResult createExceptional(Supplier<Exception> exceptionSupplier)
  {

    return new LookupResult(ResultAccessor.from(() ->
    {
      throw new RuntimeException(exceptionSupplier.get());
    }), EXCEPTIONAL);
  }

  public static LookupResult lazy(Supplier<LookupResult> lookupResultSupplier)
  {
    return new LookupResult(lookupResultSupplier);
  }

  public LookupType getLookupType()
  {
    return lookupType;
  }

  public boolean isResolved()
  {
    return isCertainLookupType(RESOLVED);
  }

  public boolean isExceptional()
  {
    return isCertainLookupType(EXCEPTIONAL);
  }

  public boolean isUnresolved()
  {
    return isCertainLookupType(UNRESOLVED);
  }

  public boolean isLazy()
  {
    return this.getLookupType().equals(LAZY);
  }
}
