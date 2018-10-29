package com.nixsolutions.logging.advice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.advice.handler.LogActionHandlerFactory;
import com.nixsolutions.logging.advice.handler.base.AbstractLogActionHandler;

@Aspect
@Component
public class LoggingAdvice
{
  @Autowired
  private LogActionHandlerFactory logActionHandlerFactory;

  //TODO check if advised class has it's own implementation of PrettyLoggable
  @Around("@annotation(com.nixsolutions.logging.annotation.Log)")
  public Object loggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
  {
    final Object invocationResult;
    final Method method = getMethod(proceedingJoinPoint);
    final Object originalObject = proceedingJoinPoint.getTarget();
    final Object[] args = proceedingJoinPoint.getArgs();
    final Logger logger = getLoggerForObject(originalObject);

    long beforeCall = System.nanoTime();
    try
    {
      logActionHandlerFactory.createEntryHandler(logger).perform(
          createParamsForEntryLogging(method, args));
      invocationResult = proceedingJoinPoint.proceed(args);
      logActionHandlerFactory.createExectimeHandler(logger).perform(
          createParamsForExectimeLogging(method, beforeCall, System.nanoTime()));
      logActionHandlerFactory.createExitHandler(logger).perform(createParamsForExitLogging(method, null,
          invocationResult));
    }
    catch (Exception exception)
    {
      logActionHandlerFactory.createExectimeHandler(logger).perform(
          createParamsForExectimeLogging(method, beforeCall, System.nanoTime()));
      logActionHandlerFactory.createExitHandler(logger).perform(createParamsForExitLogging(method, exception, null));
      throw exception;
    }

    return invocationResult;
  }

  private Map<String, Object> createParamsForEntryLogging(Method method, Object[] methodArgs)
  {
    HashMap<String, Object> parameters = new HashMap<>();

    parameters.put(AbstractLogActionHandler.METHOD_PARAM, method);
    parameters.put(AbstractLogActionHandler.METHOD_ARGS_PARAM, methodArgs);

    return parameters;
  }

  private Map<String, Object> createParamsForExectimeLogging(Method method, long beforeCall, long afterCall)
  {
    HashMap<String, Object> parameters = new HashMap<>();

    parameters.put(AbstractLogActionHandler.METHOD_PARAM, method);
    parameters.put(AbstractLogActionHandler.START_MOMENT_PARAM, beforeCall);
    parameters.put(AbstractLogActionHandler.FINISH_MOMENT_PARAM, afterCall);

    return parameters;
  }

  private Map<String, Object> createParamsForExitLogging(Method method, Exception exception, Object invocationResult)
  {
    HashMap<String, Object> parameters = new HashMap<>();

    parameters.put(AbstractLogActionHandler.METHOD_PARAM, method);
    parameters.putIfAbsent(AbstractLogActionHandler.EXCEPTION_PARAM, exception);
    parameters.put(AbstractLogActionHandler.INVOCATION_RESULT_PARAM, invocationResult);

    return parameters;
  }

  //TODO Logger cache?
  private Logger getLoggerForObject(Object originalObject)
  {
    return LoggerFactory.getLogger(originalObject.getClass());
  }

  private Method getMethod(ProceedingJoinPoint joinPoint)
  {
    return ((MethodSignature) joinPoint.getSignature()).getMethod();
  }
}
