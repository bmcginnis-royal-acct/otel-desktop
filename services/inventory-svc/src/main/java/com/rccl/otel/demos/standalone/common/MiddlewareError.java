package com.rccl.otel.demos.standalone.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

//public class MiddlewareError  {}
/**
 * A Middleware standard error format that provides the structure for developers to design and throw detailed
 * application errors.
 * <p>
 * Most properties are optional, so not setting them will exclude them from serialization. It is recommended to
 * review the error in question to ensure we can provide back as much information as possible.
 * <p>
 * Please review the documentation for each property within the class.
 *
 * @author unascribed
 * @see <a href="https://royal-digital.atlassian.net/wiki/spaces/MID/pages/974948/API+Design+Best+Practices#APIDesignBestPractices-#9HandleErrorswithHTTPstatuscodes">
 * API Design Best Practices for Handling Errors</a>
 */
@Data
//@Builder
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MiddlewareError  {

//  private static final long serialVersionUID = 1L;

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * A detailed error message intended for the developer consuming the service that aims to
   * assist with understanding what went wrong and how to fix it.
   */
//  private String developerMessage;

  /**
   * A simple, non-technical message briefly describing the error that can be shown back to a user.
   */
//  private String userMessage;

  /**
   * A technical message briefly describing what went wrong.
   */
//  private String internalMessage;

  /**
   * A formal title for the error encountered.
   */
//  private String errorTitle;

  /**
   * A service-specific domain error code.
   *
   * @see <a href="https://confluence.rccl.com/display/MID/Error+Codes">
   * https://confluence.rccl.com/display/MID/Error+Codes</a>
   */
  private String errorCode;

  private String message;

  /**
   * A URL leading to additional information about the specific error code.
   */
//  private String moreInfo;

  /**
   * A list of errors containing all input values that failed validation.
   */
  /** BJM Hack: Compiler blows up on this annotation
   * @Singular("validationError")
   *
   * Use it like this:
   * // Single validation error
   * MiddlewareError.builder().validationErrors(List.of(err1))
   * or like this:
   * MiddlewareError.builder().validationErrors(List.of(err1, err2, err3))
   *
   **/
  private List<MiddlewareValidationError> validationErrors;

  /**
   * A unique identifier for this exception message instance.
   */
  @Builder.Default
  private UUID id = UUID.randomUUID();

  /**
   * The instance in time that this exception message was created, in epoch milliseconds.
   */
  @Builder.Default
  private Long time = System.currentTimeMillis();

  /**
   * Adds a {@link MiddlewareValidationError} to the list of validation errors associated with this error.
   *
   * @param validationError The MiddlewareValidationError to add.
   */
  public void addValidationError(MiddlewareValidationError validationError) {
    this.getValidationErrors().add(validationError);
  }

  @Override
  public String toString() {
    try {
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (IOException ioe) {
      // This'll never happen.
      return super.toString();
    }
  }
}
