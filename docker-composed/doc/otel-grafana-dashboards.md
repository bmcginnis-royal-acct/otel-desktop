# OTEL Grafana dasbhoards

Notes and info on dashboards for OTEL desktop. We will use tutorial-svc
as a starting place. 

# Incoming HTTP Requests

## Request Rates

Examples of request rates (and in some cases error rates).

Metric: findall tutorials (any status)
```
rate(default_http_server_requests_count{exported_job="tutorial-svc",method="GET",uri="/api/tutorials"}[$__rate_interval])
``````

Metric: findOne tuturial (any status)
```
rate(default_http_server_requests_count{exported_job="tutorial-svc",method="GET",uri="/api/tutorials/{id}"}[$__rate_interval])
```

Metric: POST tutorial (server-side errors)
```
rate(default_http_server_requests_count{exported_job="tutorial-svc",uri="/api/tutorials",method="POST",outcome="SERVER_ERROR"}[$__rate_interval])
```

Metric: POST tutorial (client-side errors 4xx)
```
rate(default_http_server_requests_count{exported_job="tutorial-svc",uri="/api/tutorials",method="POST",outcome="CLIENT_ERROR"}[$__rate_interval])
```

Metric: POST tutorial (sucess 2xx)
```
rate(default_http_server_requests_count{exported_job="tutorial-svc",uri="/api/tutorials",method="POST",outcome="SUCCESS"}[$__rate_interval])
```

## Response Latency

```
// 95th percentile - GET all tutorials
histogram_quantile(0.95, sum(rate(default_http_server_duration_bucket{exported_job="tutorial-svc",http_method="GET",http_route="/api/tutorials"}[$__rate_interval])) by (le))

// 50th percentile - GET all tutorials
histogram_quantile(0.5, sum(rate(default_http_server_duration_bucket{exported_job="tutorial-svc",http_method="GET",http_route="/api/tutorials"}[$__rate_interval])) by (le))
```

## Uptime

todo


# Auto-Generated Metrics of interest

OTEL's documentation still kind of sucks, so below is a reverse-engineered description of the metrics we have available
to us in Prometheus/Grafana we will commonly use. The best otel documentation of them can be found here: https://github.com/open-telemetry/semantic-conventions/blob/main/docs/http/http-metrics.md

| prometheus metric base name | suffixes | otel metric name | metric type | description | documentation |
|---|---|---|---|---|--|
| default_http_server_requests_ | bucket, count, max, sum | http.server.requests | ?? | Appears to increment for each request handled by microservice | <link> |
| default_http_server_request_size_ | bucket, count, sum | http.server.request.size | Histogram | unknown | todo |
| default_http_server_response_size | bucket, count, sum | http.server.response.size | Histogram | unknown | todo | 
| default_application_ready_time | none |   application.ready.time | ? | unknown | todo |
|default_http_server_active_requests | http.server.active.requests | ? | unknown | unknown | todo |
| default_application_started_time | none |   application.ready.time | ? | unknown | todo |
| default_r2dbc_pool | acquired, allocated, pool, etc. | r2dbc.pool | ? | monitors an R2DB connection pool | todo |


XXX  TODO Add

1. default_http_server_duration
1. default_http_client_duration_
1. default_http_client_response_
1. default_jvm
1. default_process 
1. default_process_uptime?
