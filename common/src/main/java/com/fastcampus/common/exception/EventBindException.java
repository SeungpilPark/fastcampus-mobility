package com.fastcampus.common.exception;

public class EventBindException extends RuntimeException {


  public EventBindException(String message) {
    super(message);
  }

  public EventBindException(String message, Throwable cause) {
    super(message, cause);
  }
}
