#!/bin/bash

export OTEL_METRICS_EXPORTER=otlp
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:5555
export OTEL_RESOURCE_ATTRIBUTES=service.name=standalone-otel,service.version=v1.0
export INVENTORY_BASE_URL=http://localhost:8080/inventory

echo "====================="
echo "Rebuild & Run stand-alone with auto-instrumented OpenTelemetry sent to localhost Jaeger..."
echo "OTEL_METRICS_EXPORTER :         $OTEL_METRICS_EXPORTER"
echo "OTEL_TRACES_EXPORTER :          $OTEL_TRACES_EXPORTER"
echo "OTEL_EXPORTER_OTLP_ENDPOINT :   $OTEL_EXPORTER_OTLP_ENDPOINT"
echo "OTEL_RESOURCE_ATTRIBUTES :      $OTEL_RESOURCE_ATTRIBUTES"
echo "INVENTORY_BASE_URL :            $INVENTORY_BASE_URL"
echo "====================="
echo ""

mvn clean package -Dmaven.test.skip=true


java -javaagent:./otel-dist/opentelemetry-javaagent.jar \
-jar target/standalone-otel-0.0.1-SNAPSHOT.jar