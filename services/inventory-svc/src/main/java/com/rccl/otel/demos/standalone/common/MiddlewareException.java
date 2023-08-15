package com.rccl.otel.demos.standalone.common;



public class MiddlewareException extends RuntimeException {

  private Integer httpStatus;
  private String errorCode;

  public MiddlewareException() {
    super(null, null);
  }

  public MiddlewareException(String errorMessage) {
    super(errorMessage);
  }

  public MiddlewareException(Throwable throwable) {
    super(throwable);
  }

  public MiddlewareException(String errorMessage, Throwable throwable) {
    super(errorMessage, throwable);
  }

  public MiddlewareException(Integer httpStatus, String errorCode, String errorMessage, Throwable throwable) {
    super(errorMessage, throwable);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }

  /**
   * Get the error code specific to a {@link MiddlewareException}.
   * <p>
   * This method is meant to be overridden.
   *
   * @return {@code String}
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Get the HTTP status represented by this {@link MiddlewareException}.
   * <p>
   * This method is meant to be overridden.
   *
   * @return {@code Integer}
   */
  public Integer getHttpStatus() {
    return httpStatus;
  }

  /**
   * Get the more information link specific to a {@link MiddlewareException}.
   * <p>
   * This method is meant to be overridden.
   *
   * @return {@code String}
   */
  public String getMoreInfo() {
    return null;
  }

  /**
   * Get the user message specific to a {@link MiddlewareException}.
   * <p>
   * This method is meant to be overridden.
   *
   * @return {@code String}
   */
  public String getUserMessage() {
    return null;
  }

  public MiddlewareError toError() {
    return MiddlewareError.builder()
        .errorCode(this.getErrorCode())
        .message(this.getMessage())
//        .moreInfo(this.getMoreInfo())
//        .userMessage(this.getUserMessage())
        .build();
  }
}
