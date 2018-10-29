package com.nixsolutions.logging.integration;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import com.nixsolutions.logging.advice.pojo.Pojo;
import com.nixsolutions.logging.annotation.ContextParam;
import com.nixsolutions.logging.annotation.Log;

@Component
public class SampleService
{


  private void timeConsuming()
  {
    try
    {
      Thread.sleep(300);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  //Entry

  @Log
  @Log.entry
  public void method()
  {
  }

  @Log
  @Log.entry
  public void method(@ContextParam String strParam)
  {

  }

  @Log
  @Log.entry
  public void methodWithRenamedParam(@ContextParam("renamedStrParam") String strParam)
  {

  }

  @Log
  @Log.entry
  public void methodWithMultipleParams(@ContextParam String strParam,
                                       @ContextParam Long longParam)
  {

  }

  @Log
  @Log.entry
  public void methodWithComplexParam(@ContextParam Pojo complexParam)
  {

  }

  //Exec time

  @Log
  @Log.exectime
  public void methodWihExecTimeLogging()
  {
    timeConsuming();
  }

  @Log
  @Log.exectime(timeUnit = TimeUnit.MICROSECONDS)
  public void methodWithExectimeLoggingAndOtherTimeUnit()
  {

  }

  @Log
  @Log.exectime(taskName = "newTaskName")
  public void methodWithExectimeLoggingAndOtherTaskName()
  {

  }

  @Log
  @Log.exectime(taskName = "Human readable task name")
  public void methodWithExectimeLoggingAndHumanReadableTaskName()
  {

  }

  //Exit

  @Log
  @Log.exit
  public String methodWithStrReturn()
  {
    return "RETURN_STR";
  }

  @Log
  @Log.exit
  public Pojo methodWithPojoReturn()
  {
    return new Pojo();
  }

  @Log
  @Log.exit
  public void methodWithVoidReturn()
  {
  }

  @Log
  @Log.exit
  public void methodTerminatedWithException()
  {
    throw new RuntimeException();
  }
}

