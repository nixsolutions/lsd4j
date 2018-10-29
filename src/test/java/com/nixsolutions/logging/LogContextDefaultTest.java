package com.nixsolutions.logging;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogContextDefaultTest
{
  private static final Logger LOG = LoggerFactory.getLogger(LogContextDefaultTest.class);
  private static final String MESSAGE = "message";
  private static final Long QUOTE_ID = 47777L;
  private static final String QUOTE_ID_STRING = "47777";
  private static final String QUOTE_NAME_KEY = "quoteName";
  private static final String QUOTE_NAME_VALUE = "UberQuote";
  private static final String QUOTE_STATUS_KEY = "quoteStatus";
  private static final String QUOTE_STATUS_VALUE = "Jumping";

  private LogContext<Long, String> logContextDefault = new LogContextDefault();

  private static final String CONTEXT_PREFIX = "ctx:{";
  private static final String CONTEXT_SUFFIX = "}";

  @ParameterizedTest(name = "Should {0}.")
  @MethodSource("argumentsForShouldAddInfoAndGetMessage")
  void shouldAddInfoAndGetMessage(
      String testCaseName,
      String message,
      Map<String, Object> quoteInfo,
      Long key,
      String expectedResult)
  {
    //given

    //when
    String actual = logContextDefault.get(message, key, quoteInfo);

    //then
    Assertions.assertEquals(expectedResult, actual);
  }

  private Stream<Arguments> argumentsForShouldAddInfoAndGetMessage()
  {
    return Stream.of(
        Arguments.of(
            "return formatted message when quoteInfo map is empty",
            MESSAGE,
            new HashMap<>(),
            QUOTE_ID,
            createMessageWithContextPattern(MESSAGE, format("key={0}", QUOTE_ID_STRING))
        ),
        Arguments.of(
            "return formatted message when quoteInfo map is null",
            MESSAGE,
            null,
            QUOTE_ID,
            createMessageWithContextPattern(MESSAGE, format("key={0}", QUOTE_ID_STRING))
        ),
        Arguments.of(
            "return formatted message when quoteInfo map is immutable and prefilled with some values",
            MESSAGE,
            createMap(QUOTE_NAME_KEY, QUOTE_NAME_VALUE, QUOTE_STATUS_KEY, QUOTE_STATUS_VALUE),
            QUOTE_ID,
            createMessageWithContextPattern(MESSAGE,
                format("key={0}, {1}={2}, {3}={4}", QUOTE_ID_STRING, QUOTE_NAME_KEY, QUOTE_NAME_VALUE,
                    QUOTE_STATUS_KEY, QUOTE_STATUS_VALUE))
        ),
        Arguments.of(
            "return formatted message with proper order when quoteInfo prefilled with values. Info by key first then " +
                "additional values",
            MESSAGE,
            createMap(QUOTE_NAME_KEY, QUOTE_NAME_VALUE, QUOTE_STATUS_KEY, QUOTE_STATUS_VALUE),
            QUOTE_ID,
            createMessageWithContextPattern(MESSAGE,
                format("key={0}, {1}={2}, {3}={4}", QUOTE_ID_STRING, QUOTE_NAME_KEY, QUOTE_NAME_VALUE,
                    QUOTE_STATUS_KEY, QUOTE_STATUS_VALUE))
        ),
        Arguments.of(
            "return formatted message when key is null",
            MESSAGE,
            Collections.singletonMap(QUOTE_NAME_KEY, QUOTE_NAME_VALUE),
            null,
            createMessageWithContextPattern(MESSAGE, format("{0}={1}", QUOTE_NAME_KEY, QUOTE_NAME_VALUE))
        ),
        Arguments.of(
            "return formatted message is null",
            null,
            new HashMap<>(),
            QUOTE_ID,
            createMessageWithContextPattern(EMPTY, format("key={0}", QUOTE_ID_STRING))
        ),
        Arguments.of(
            "return formatted message is blank",
            " ",
            new HashMap<>(),
            QUOTE_ID,
            createMessageWithContextPattern(EMPTY, format("key={0}", QUOTE_ID_STRING))
        )
    );
  }

  private Map<String, String> createMap(String... args)
  {
    Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < args.length; i = i + 2)
    {
      map.put(args[i], args[i + 1]);
    }
    Map<String, String> nestedMap = new LinkedHashMap<>();
    nestedMap.put("something", "good");
    nestedMap.put("nothing", "bad");
//    map.put("nes", "");
    return map;
  }

  private String createMessageWithContextPattern(String message, String params)
  {
    String contextPrefix = StringUtils.isNotBlank(message) ? ". " + CONTEXT_PREFIX : CONTEXT_PREFIX;

    return StringUtils.join(message, contextPrefix, params, CONTEXT_SUFFIX);
  }

  private static class ClassWithCustomToString
  {
    @Override
    public String toString()
    {
      return "customToString";
    }
  }

  @Test
  void testStream()
  {
    Optional<Dummy> first = Stream.of(new Dummy(false),
        new Dummy(true),
        new Dummy(false),
        new Dummy(false),
        new Dummy(true),
        new Dummy(false)).filter(Dummy::isResolved).findFirst();

  }

  @Test
  void testInterfaceCheck()
  {
    DummyBase dummyBase = new DummyBase();
    DummyDerived dummyDerived = new DummyDerived();

    assertTrue(dummyBase instanceof Serializable);
    assertTrue(dummyDerived instanceof Serializable);
  }

  private static class DummyBase implements Serializable
  {}

  private static class DummyDerived extends DummyBase
  {}

  private static class Dummy
  {
    private static int counter_gl =0;
    boolean resolved;
    int counter;

    public Dummy(boolean resolved)
    {
      counter_gl++;
      counter = counter_gl;
      this.resolved = resolved;
    }

    public boolean isResolved()
    {
      LOG.error("Resolving..."+ counter);
      return resolved;
    }
  }

  private static class DummyClass
  {
    private String name;
    private Long value;

    DummyClass(String name, Long value)
    {
      this.name = name;
      this.value = value;
    }

    public String getName()
    {
      return name;
    }

    public Long getValue()
    {
      return value;
    }

    @Override
    public String toString()
    {
      return "dummyClassToString";
    }
  }
}