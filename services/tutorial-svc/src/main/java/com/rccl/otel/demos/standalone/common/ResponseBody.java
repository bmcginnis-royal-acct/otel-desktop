package com.rccl.otel.demos.standalone.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseBody<T> {

  @Builder.Default
  private int status = 200;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Builder.Default
  private Retry retry = null;

//  @Singular("error")
  // Use like this: ResponseBody.builder().errors(List.of(err1))
  // or ResponseBody.builder().errors(List.of(err1, err2, err3))
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MiddlewareError> errors;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private T payload;

  public ResponseBody(T payload) {
    this(200, null, payload);
  }

  public ResponseBody(List<MiddlewareError> errors, T payload) {
    this(200, errors, payload);
  }

  public ResponseBody(int status, List<MiddlewareError> errors, T payload) {
    this.status = status;
    this.errors = errors == null ? new ArrayList<>() : errors;
    this.payload = payload;
  }
}
