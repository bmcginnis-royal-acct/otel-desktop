# Inventory Service

for config files: https://www.giorgosdimtsas.net/blog/collecting-spring-boot-telemetry-data-with-opentelemetry/

A simple service used for remote call telemetry
demo purposes. 

## Initializing with OTEL auto-instrumentation in docker desktop mode

We are using the otel automatic instrumentation. 
The dockerfile includes the lines to configure it:
```
# Download and add OpenTelemetry auto-instrumentation jar. 
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar .

# Use JAVA_TOOL_OPTIONS to include otel java agent at start-up
# this starts the byte code manipulation to inject otel telemetry into our app.
ENV JAVA_TOOL_OPTIONS "-javaagent:./opentelemetry-javaagent.jar"
```
Note: The ../docker-compose.yaml file includes more 
OTEL env variables, snippet shown here:

```
  inventory-svc:
    container_name: inventory-svc
    build:
      context: ./service-inventory
      dockerfile: Dockerfile
    environment:
      - OTEL_SERVICE_NAME=inventory-svc
      - OTEL_METRICS_EXPORTER=otlp
      - OTEL_TRACES_EXPORTER=jaeger
      - OTEL_EXPORTER_JAEGER_ENDPOINT=http://jaeger:14250
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:5555
      - OTEL_RESOURCE_ATTRIBUTES=service.name=inventory-svc,service.version=0.0.1-SNAPSHOT
    ports:
      - "8081:8081"
```


Note: if you started this service manually, instead of in
Docker you will not have otel auto-instrumentation
running. You would need a simple start up script such as this
to make auto instrumentation work:

```
# file: run-microservice.sh

mvn clean package -Dmaven.test.skip=true

export OTEL_TRACES_EXPORTER=jaeger
export OTEL_METRICS_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:5555
export OTEL_RESOURCE_ATTRIBUTES=service.name=inventory-svc,service.version=0.0.1-SNAPSHOT

echo "====================="
echo "Rebuild & Run service with telemetry sent to OTEL Collector and monitoring tools..."
echo "OTEL_METRICS_EXPORTER :         $OTEL_METRICS_EXPORTER"
echo "OTEL_TRACES_EXPORTER :          $OTEL_TRACES_EXPORTER"
echo "OTEL_RESOURCE_ATTRIBUTES :      $OTEL_RESOURCE_ATTRIBUTES"
echo "====================="
echo ""

java -javaagent:./otel-dist/opentelemetry-javaagent.jar -jar target/cart-svc-0.0.1-SNAPSHOT.jar
```