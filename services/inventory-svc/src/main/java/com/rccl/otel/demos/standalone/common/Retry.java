package com.rccl.otel.demos.standalone.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Retry {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Builder.Default
  public Boolean retryable = true;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Builder.Default
  public Integer attempts = 1;
}