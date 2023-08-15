package com.rccl.otel.demos.standalone.common;

/** Names of common headers */
public interface CommonHeaders {

  /**
   * Send by caller signalling the request is a "retry" of a prior request.
   * We require the caller to send the same TraceId as the prior request so
   * we can track it. Note retry policy is often undocumented and callers may not
   * know how often they can retry, how frequently, etc.
   *
   * For a normal request that isn't being retried, the caller should not create
   * this header and send it.
   *
   * The value of the header should be the number of retry attempts being made.
   * For example if a request failed, the sender would send the "retry" header with
   * a value of "1" for the first retry. A value of "2" for second, etc.
   *
   * The service receiving the header should:
   * 1) Log the retry value in relevant log records for this request.
   * 2) Might use the retry header to fail fast if the caller is abusing her retry privileges.
   */
  public static final String HDR_RETRY = "retry";

  /**
   * Signals the retry being made is being done downstream from the service call chain.
   * For example: MobileApp => Alpha-Service => Beta-Service
   * The mobile app encounters a 500 error and wants to retry the request. the headers look like:
   * - MobileApp sends headers: "retry" : "1"
   * - Alpha-Service: receives "retry" from MobileApp. It sends "parent-retry" : "MobieApp".
   * - Beta-Service: receives "parent-retry" : "MobileApp".
   *
   * Beta-Service responds with a 500 error again to Alpha-Service.
   * Alpha-Service then decides to retry this request. It sends: "parent-retry" : "MobileApp" and
   * a header "retry" : "1" for the Alpha's first retry. This cycle repeats as Alpha retries
   * 2 more times. It them gives up and response to mobile app with the payload:
   * {
   * status: 500,
   * payload : {
   *   retry : {
   *     attempts: 3,
   *     retryable: false
   *   }
   * }
   * }
   *
   * The mobile app now knows that AlphaService it called retried their request 3 times and got the
   * same response plus retryable=false, so the MobileApp should not retry this request again now.
   */
  public static final String HDR_PARENT_RETRY = "parent-retry";

}
