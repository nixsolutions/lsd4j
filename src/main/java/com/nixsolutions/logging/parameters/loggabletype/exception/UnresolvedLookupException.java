package com.nixsolutions.logging.parameters.loggabletype.exception;

public class UnresolvedLookupException extends RuntimeException
{
  public UnresolvedLookupException()
  {
  }

  public UnresolvedLookupException(String message)
  {
    super(message);
  }
}
