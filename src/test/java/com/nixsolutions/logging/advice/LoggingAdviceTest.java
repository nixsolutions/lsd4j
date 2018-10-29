package com.nixsolutions.logging.advice;

import static com.nixsolutions.logging.advice.LoggingResultHelper.PARAM_LONG;
import static com.nixsolutions.logging.advice.LoggingResultHelper.PARAM_STR;
import static com.nixsolutions.logging.advice.LoggingResultHelper.supposeThat;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nixsolutions.logging.LogContext;
import com.nixsolutions.logging.LogContextJson;
import com.nixsolutions.logging.advice.pojo.Pojo;
import com.nixsolutions.logging.configuration.LoggingConfiguration;
import com.nixsolutions.logging.integration.SampleService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Configuration
@ContextConfiguration(classes = {LoggingAdviceTest.class, LoggingConfiguration.class})
@ExtendWith(SpringExtension.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LoggingAdviceTest
{
  private ObjectMapper objectMapper = new ObjectMapper();

  private PrintStream stdout = System.out;
  private PrintStream stderr = System.err;

  private ByteArrayOutputStream outStream;

  @Autowired
  SampleService sampleService;

  @BeforeAll
  public static void beforeAll()
  {
    LoggerFactory.getLogger(LoggingAdviceTest.class).debug("{\"status\": \"TEST STARTED\"}");
  }

  @BeforeEach
  public void beforeTest()
  {
    outStream = new ByteArrayOutputStream();

    System.setOut(new PrintStream(outStream));
  }

  @AfterEach
  public void afterTest()
  {
    System.setOut(stdout);
    System.setErr(stderr);
  }

  @ParameterizedTest(name = "Should {0}")
  @MethodSource("argsForTestEntryLogging")
  public void testEntryLogging(String name, Runnable methodRun, String fileName) throws Exception
  {
    //when
    methodRun.run();

    //then
    JsonNode expected = objectMapper
        .readTree(LoggingAdviceTest.class.getResourceAsStream("entry/" + fileName));

    supposeThat()
        .givenSource(outStream)
        .fromCtx()
        .shouldBeEqualTo(expected);
  }

  private Stream<Arguments> argsForTestEntryLogging()
  {
    return Stream.of(
        Arguments.of(
            "not log entry params if not specified",
            (Runnable) () -> sampleService.method(),
            "emptyCtx.json"
        ),
        Arguments.of(
            "log single string param",
            (Runnable) () -> sampleService.method(PARAM_STR),
            "strParam.json"
        ),
        Arguments.of(
            "log renamed string parameter",
            (Runnable) () -> sampleService.methodWithRenamedParam(PARAM_STR),
            "renamedParam.json"
        ),
        Arguments.of(
            "log multiple parameters",
            (Runnable) () -> sampleService.methodWithMultipleParams(PARAM_STR, PARAM_LONG),
            "multipleParams.json"
        ),
        Arguments.of(
            "log complex parameter",
            (Runnable) () -> sampleService.methodWithComplexParam(new Pojo()),
            "complexParam.json"
        )
    );
  }

  @ParameterizedTest(name = "Should {0}")
  @MethodSource("argsForTestExectimeLogging")
  public void testExectimeLogging(String name, Runnable methodRun, String fileName) throws Exception
  {
    //when
    methodRun.run();

    //then
    JsonNode expected = objectMapper
        .readTree(LoggingAdviceTest.class.getResourceAsStream("exectime/" + fileName));

    supposeThat()
        .givenSource(outStream)
        .fromCtx()
        .propertyValueIgnored("timeLoggingContext.duration")
        .shouldBeEqualTo(expected);
  }

  private Stream<Arguments> argsForTestExectimeLogging()
  {
    return Stream.of(
        Arguments.of(
            "log exec time if specified",
            (Runnable) () -> sampleService.methodWihExecTimeLogging(),
            "simpleTimeLogging.json"
        ),
        Arguments.of(
            "log exec time with adjusted time unit",
            (Runnable) () -> sampleService.methodWithExectimeLoggingAndOtherTimeUnit(),
            "changedTimeUnit.json"
        ),
        Arguments.of(
            "log exec time with adjusted task name",
            (Runnable) () -> sampleService.methodWithExectimeLoggingAndOtherTaskName(),
            "adjustedTaskName.json"
        ),
        Arguments.of(
            "log exec time with human readable task name",
            (Runnable) () -> sampleService.methodWithExectimeLoggingAndHumanReadableTaskName(),
            "humanReadableTaskName.json"
        )
    );
  }

  @Test
  public void shouldLogTime() throws Exception
  {
    //when
    sampleService.methodWihExecTimeLogging();

    //then
    long duration = new ObjectMapper()
        .readTree(outStream.toString())
        .get("context")
        .get("ctx")
        .get("timeLoggingContext")
        .get("duration")
        .longValue();


    Assertions.assertTrue(duration >= 300L);
  }

  @ParameterizedTest(name = "Should {0}")
  @MethodSource("argsForTestExitLogging")
  public void testExitLogging(String name, Runnable methodRun, String fileName) throws Exception
  {
    //when
    methodRun.run();

    //then
    JsonNode expected = objectMapper
        .readTree(LoggingAdviceTest.class.getResourceAsStream("exit/" + fileName));

    supposeThat()
        .givenSource(outStream)
        .fromCtx()
        .shouldBeEqualTo(expected);
  }

  private Stream<Arguments> argsForTestExitLogging()
  {
    return Stream.of(
        Arguments.of(
            "log string return param",
            (Runnable) () -> sampleService.methodWithStrReturn(),
            "strReturnParam.json"
        ),
        Arguments.of(
            "log pojo return param",
            (Runnable) () -> sampleService.methodWithPojoReturn(),
            "pojoReturnParam.json"
        ),
        Arguments.of(
            "log void return param",
            (Runnable) () -> sampleService.methodWithVoidReturn(),
            "voidReturn.json"
        )
    );
  }

  @Test
  public void shouldLogExceptionTerminatedMethod() throws Exception
  {
    //when
    try
    {
      sampleService.methodTerminatedWithException();
    }
    catch (Exception e)
    {

    }

    //then
    JsonNode actual = objectMapper.readTree(outStream.toString());

    Assertions.assertTrue(actual.get("exception").asText().length() > 0);
  }

  @Primary
  @Bean
  public LogContext<Long, String> logContextJson()
  {
    return new LogContextJson();
  }
}
