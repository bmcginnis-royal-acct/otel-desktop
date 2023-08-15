package com.rccl.otel.demos.standalone.config;


import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Reads configuration info, per runtime environment.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "environment")
public class EnvironmentConfig implements InitializingBean {

  public Boolean logInvalidRequests;

  // Otel resource attributes
  public String serviceName;
  public String serviceVersion;
  public String deploymentEnvironment;

  public String inventoryBaseUrl;

  @Value("${server.port}")
  private int serverPort;

  private Resource otelResource;

  // Not needed if OTEL_ environment vars were set for auto-instrumentation.
  public void afterPropertiesSet() {
    // TODO: Set a flag for auto-instrumentation
    final boolean is_auto_instrumented = true;
    if (!is_auto_instrumented) {
      otelResource = Resource.getDefault().merge(
          Resource.create(Attributes.of(
              ResourceAttributes.SERVICE_NAME, getServiceName(),
              ResourceAttributes.SERVICE_VERSION, getServiceVersion(),
              ResourceAttributes.DEPLOYMENT_ENVIRONMENT, getDeploymentEnvironment()))
      );
      log.info("Otel Resource is: {}", otelResource);
    }


    log.info("\n\n\nRunning service: {}, on port:{} ...\n\n\n", serviceName, serverPort);
  }


}
