package com.xygcoder.opensource.exception;

public class SimpleHttpException extends Exception {
  protected ExceptionCode code;
  protected String message;

  public SimpleHttpException(ExceptionCode code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public SimpleHttpException(ExceptionCode code) {
    this.code = code;
    message = "";
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public enum ExceptionCode {
    INITIALIZE_ERROR,
    NO_HANDLER_FOUND
  }

  public void appendMessage(String message) {
    if (this.message.isEmpty()) {
      setMessage(message);
    } else {
      setMessage(String.format("%s; %s", this.message, message));
    }
  }

  public boolean isSet() {
    return !this.message.isEmpty();
  }
}
