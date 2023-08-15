package com.rccl.otel.demos.standalone.config;


import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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

  public String serviceName;

  public String inventoryBaseUrl;

  private Resource otelResouce;

  public void afterPropertiesSet() {
    otelResouce = Resource.getDefault().merge(
        Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, getServiceName()))
    );
    log.info("Otel Resource is: {}", otelResouce);
  }


}
