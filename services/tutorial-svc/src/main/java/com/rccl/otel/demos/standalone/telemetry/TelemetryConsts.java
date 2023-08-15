package com.rccl.otel.demos.standalone.telemetry;

/**
 * Const values for custom telemetry.
 */
public class TelemetryConsts {

  public static final String METRIC_PREFIX = "tutorial.custom.metric.";

  public static final String NUMBER_OF_TUTORIALS_CREATED = METRIC_PREFIX + "tutorials.created";
  public static final String NUMBER_OF_TUTORIALS_CREATED_DESCRIPTION = "Counts the number of tutorials created.";

}
