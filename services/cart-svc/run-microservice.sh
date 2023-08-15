mvn clean package -Dmaven.test.skip=true

export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:5555

# Important: if you provide resource attributes here, it will override any resource attrs read from your code.
#export OTEL_RESOURCE_ATTRIBUTES=service.name=cart-svc,service.version=0.0.1-SNAPSHOT,deployment.environment="dev"


echo "====================="
echo "Rebuild & Run service with telemetry sent to OTEL Collector and monitoring tools..."
echo "OTEL_METRICS_EXPORTER :         $OTEL_METRICS_EXPORTER"
echo "OTEL_TRACES_EXPORTER :          $OTEL_TRACES_EXPORTER"
echo "OTEL_RESOURCE_ATTRIBUTES :      $OTEL_RESOURCE_ATTRIBUTES"
echo "====================="
echo ""

java -javaagent:./otel-dist/opentelemetry-javaagent.jar -jar target/cart-svc-0.0.1-SNAPSHOT.jar
