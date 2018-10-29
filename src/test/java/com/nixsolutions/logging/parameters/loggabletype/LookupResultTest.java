package com.nixsolutions.logging.parameters.loggabletype;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.nixsolutions.logging.parameters.loggabletype.exception.UnresolvedLookupException;
import com.google.common.collect.ImmutableMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LookupResultTest
{
  private static final Object OBJECT = new Object();

  private static final Map<String, Object> RESULT = ImmutableMap.of(
      "key1", ImmutableMap.of("key1_1", "key1_val1"),
      "key2", ImmutableMap.of("key2_1", "key2_val1"));

  @ParameterizedTest(name = "Should {0}")
  @MethodSource("argsForShouldBeCorrectType")
  public void shouldBeCorrectType(String caseName, LookupResult lookupResult,
                                  LookupResult.LookupType lookupTypeExpected)
  {
    Assertions.assertEquals(lookupTypeExpected, lookupResult.getLookupType());

  }

  private Stream<Arguments> argsForShouldBeCorrectType()
  {
    return Stream.of(
        Arguments.of(
            "type be RESOLVED",
            createLookupResult(LookupResult.LookupType.RESOLVED),
            LookupResult.LookupType.RESOLVED
        ),
        Arguments.of(
            "type be UNRESOLVED",
            createLookupResult(LookupResult.LookupType.UNRESOLVED),
            LookupResult.LookupType.UNRESOLVED
        ),
        Arguments.of(
            "type be EXCEPTIONAL",
            createLookupResult(LookupResult.LookupType.EXCEPTIONAL),
            LookupResult.LookupType.EXCEPTIONAL
        ),
        Arguments.of(
            "type be LAZY",
            createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED)),
            LookupResult.LookupType.LAZY
        )
    );
  }

  @ParameterizedTest(name = "Should be {0} after unwrapping lazy")
  @MethodSource("argsForShouldBeCorrectTypeOfLazyAfterUnwrap")
  public void shouldBeCorrectTypeOfLazyAfterUnwrap(String caseName, LookupResult lookupResult,
                                                   LookupResult.LookupType lookupTypeExpected)
  {
    Assertions.assertEquals(lookupResult.getLookupType(), LookupResult.LookupType.LAZY);
    Assertions.assertTrue(lookupResult.isCertainLookupType(lookupTypeExpected));
    Assertions.assertNotEquals(lookupResult.getLookupType(), LookupResult.LookupType.LAZY);
  }

  private Stream<Arguments> argsForShouldBeCorrectTypeOfLazyAfterUnwrap()
  {
    return Stream.of(
        Arguments.of(
            "type be RESOLVED",
            createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED)),
            LookupResult.LookupType.RESOLVED
        ),
        Arguments.of(
            "type be UNRESOLVED",
            createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.UNRESOLVED)),
            LookupResult.LookupType.UNRESOLVED
        ),
        Arguments.of(
            "type be EXCEPTIONAL",
            createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.EXCEPTIONAL)),
            LookupResult.LookupType.EXCEPTIONAL
        )
    );
  }

  @ParameterizedTest(name = "Should throw {0}")
  @MethodSource("argsForShouldThrowExExceptionalAndUnresolvedLookup")
  public void shouldThrowExExceptionalAndUnresolvedLookup(Class<? extends Exception> expectedException, LookupResult
      lookupResult)
  {
    Executable executable = lookupResult::executeForResult;
    Assertions.assertThrows(expectedException, executable);
  }

  private Stream<Arguments> argsForShouldThrowExExceptionalAndUnresolvedLookup()
  {
    return Stream.of(
        Arguments.of(
            RuntimeException.class,
            createLookupResult(LookupResult.LookupType.EXCEPTIONAL)
        ),
        Arguments.of(
            UnresolvedLookupException.class,
            createLookupResult(LookupResult.LookupType.UNRESOLVED)
        )
    );
  }

  @ParameterizedTest(name = "Should {0}")
  @MethodSource("argsForShouldProduceCorrectResult")
  public void shouldProduceCorrectResult(String name, Map<String, Object> expectedResult, LookupResult lookupResult)
  {
    Assertions.assertEquals(expectedResult, lookupResult.executeForResult());
  }

  private Stream<Arguments> argsForShouldProduceCorrectResult()
  {
    return Stream.of(
        Arguments.of(
            "return correct result for resolved lookup",
            RESULT,
            createLookupResult(LookupResult.LookupType.RESOLVED)
        ),
        Arguments.of(
            "return correct result for lazy resolved lookup",
            RESULT,
            createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED))
        )
    );
  }

  private LookupResult createLazyLookupWrapping(LookupResult lookupResultWrapped)
  {
    return LookupResult.lazy(() -> lookupResultWrapped);
  }

  private LookupResult createLookupResult(LookupResult.LookupType lookupType)
  {
    if (lookupType.equals(LookupResult.LookupType.RESOLVED))
    {
      return LookupResult.createResolved(() -> RESULT);
    }
    else if (lookupType.equals(LookupResult.LookupType.UNRESOLVED))
    {
      return LookupResult.createUnresolved();
    }
    else
    {
      return LookupResult.createExceptional(RuntimeException::new);
    }
  }
}