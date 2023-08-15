# Tutorial Telemetry
This project creates a simple microservice
that has CRUD and queries for 'Tutorial'
data stored in an H2 in-memory database.

Spring Data R2DBC ConnectionFactoryInitializer provides a convenient
way to configure and initialize a connection factory for a reactive database
connection in a Spring application. It will scan schema.sql in the classpath,
execute SQL script to initialize the database when the database is connected.

## API
This service creates a simple API with H2 in-memory database persistence backing.

| Methods |	Urls| Actions |
|---------|-----|---------|
| POST | /api/tutorials | 	create new Tutorial |
| GET |	/api/tutorials | 	retrieve all Tutorials |
| GET |	/api/tutorials/:id | 	retrieve a Tutorial by :id |
| PUT |	/api/tutorials/:id | 	update a Tutorial by :id |
| DELETE |	/api/tutorials/:id | 	delete a Tutorial by :id |
| DELETE |	/api/tutorials | 	delete all Tutorials |
| GET |	/api/tutorials/published | 	find all published Tutorials |
| GET |	/api/tutorials?title=[keyword] | 	find all Tutorials which title contains keyword |

## Resources
* Based on blog: https://www.bezkoder.com/spring-boot-r2dbc-h2/

# Local K8 / Istio Setup
For running everything on your local laptop
so you can experiment with telemetry tools, etc.

## Istio install and config local environment

The docs presume you are using the built-in Kubernetes in Docker Desktop on a Mac.

## Setup Kubernetes and Istio first
IMPORTANT: First, make sure you've installed and did basic setup of Kubernetes and Istio with the istio add-ons per instructions here:
[Setup Local Istio & K8 Environment](../doc/setup-local-k8-istio.md)




## Run spring service in local Docker

### Local build and run in Docker

```
// build local server
$ ./mvnw install
// mvn package

// verify build artifacts
$ ls -l target/*.jar
-rw-r--r--  1 bmcginnis@rccl.com  staff  27556452 Jun  5 16:14 target/tutorial-svc-0.0.1-SNAPSHOT.jar

// Run the jar file to verify
$ java -jar target/*.jar 


---
For local demo only:
$ docker login registry.gitlab.com
$ docker build -t registry.gitlab.com/brianmcginnis/tutorial-service .
$ docker push registry.gitlab.com/brianmcginnis/tutorial-service

// Run it locally in docker in foreground
$ docker run -p 8080:8080 registry.gitlab.com/brianmcginnis/tutorial-service

----
// Build docker image for your repo. Use your docker repo (not mine)
$ docker build -t tutorial-service .
$ docker tag tutorial-service brianmcginnis/tutorial-service:v1

Load image into your docker repo (docker hub in this example)
// login to a docker repo

$ docker login registry.aws-digital.rccl.com
Username: _your_email
Password: _your_pwd

// push your artifact to repo
$ docker push brianmcginnis/tutorial-service  // should upload to docker hub w/ latest tag

// Run it locally in docker in foreground
$ docker run -p 8080:8080 brianmcginnis/tutorial-service:v1

// OR if you used a remote repo like above samples...
$ docker run -p 8080:8080 registry.gitlab.com/brianmcginnis/tutorial-service:v1 

// Send it some traffic to make sure it works to endpoints
$ curl http://localhost:8080/api/tutorials 

// now you can stop the docker run and continues

```

## Initialize Istio/K8 environment


### Create a Namespace and setup kubctl context to use it.

```
// 1. create new namespace 'rcl-experiments'
$ kubectl create namespace rcl-experiments
---
 namespace/rcl-experiments created

// 2. Set current context to use rcl-experiments
$ kubectl config set-context $(kubectl config current-context) \
--namespace rcl-experiments

---
Context "docker-desktop" modified.

// 3. verify namespace is created and current context uses it.
$ kubectl config get-contexts
---
CURRENT   NAME             CLUSTER          AUTHINFO         NAMESPACE
*         docker-desktop   docker-desktop   docker-desktop   rcl-experiments
```

### Setup Istio sidecar injection for rcl-experiements

```
// Configure the namespace for auto-injection. 
$ kubectl label namespace rcl-experiments istio-injection=enabled
---
namespace/rcl-experiments labeled

// Verify the injection label worked. 
// You should see the label istio-injection=enabled
// in the rcl-experiments namespace.
$ kubectl get namespaces --show-labels
---
NAME              STATUS   AGE   LABELS
...
rcl-experiments   Active   28m   istio-injection=enabled,kubernetes.io/metadata.name=rcl-experiments

```

You should now be able to run the experiments.

### Kill ANY old K8 service artifacts deployed
```

// SHow all k8 resources running in the current namespace
$ kubectl get all

// Delete prior deployed artifacts in rcl-experiments
kubectl delete virtualservice,deployment,service,destinationrule,\
serviceaccount,gateway --all
```


### Deploy project K8 artifacts
```
kubectl apply -f k8/gateway.yaml
kubectl apply -f k8/tutorial-svc-deployment.yaml
kubectl apply -f k8/tutorial-svc-virtual-service.yaml

/// Or a shorthand method to apply all in directory
$ kubectl apply -f ./k8
```

### Verify istio config is ok
```
$ istioctl analyze

```

### Check logs


First thing, we should view the service pods and logs to see if this worked.
```
$ kubectl get pods
NAME                            READY   STATUS    RESTARTS   AGE
tutorial-svc-6886fc6fc7-l5xzs   2/2     Running   0          13m
```

The status looks good, so let's view the logs. Open up a terminal or two and tail the service's logs. Note that we included label "app" in our deployments to make this easier.

```
// Tail the logs of tutorial-svc 
$ kubectl logs -f -l app=tutorial-svc
```

### Verify service is accessible and works

// Note: using port 80 to send traffic through our Istio gateway.
```
curl  --location 'http://localhost:80/api/tutorials' \
--header 'Content-Type: application/json' \
--verbose \
--data '{
    "title": "To write a great American novel.",
    "description": "An American classic book."
}'


curl --location 'http://localhost:80/api/tutorials'

Check the service's logs for problems.
```

### Launch monitoring tools you can see in browser
```
$ istioctl dashboard prometheus
$ istioctl dashboard grafana
$ kubectl -n istio-system port-forward deploy/kiali 20001
```



# Metrics
Launch prometheus and enter some queries:
```
$ istioctl dashboard prometheus
```
In the Prometheus search field enter the value
```
istio_requests_total{destination_workload_namespace="rcl-experiments", destination_app="tutorial-svc", destination_version="v1"}
```

# Run a load test

Included under the /k6 directory is a simple test script for generating a load upon your cart services. This is a key component to our experiment because we can't really see much with Prometheus/Grafana or Kiali without traffic to observe.

Our script simply sends one request every 2-3 seconds for 10 minutes. You can kill the k6 test runner with a ctrl-c in the terminal or restart it at any time.


Open a new terminal and run k6 within it:

```
$ k6 run ./k6/simple.js
```

If it works, you'll see k6 pooping out metrics and such as it runs. 

# Verify monitoring tools are running in you K8
Make sure monitoring tools are running.  Run the command below and you should see prometheus, jaeger and grafana are running in a pod. NOTE: We started these already via our foundation setup process. ```$ kubectl apply -f ./samples/addons```

```
$ kubectl get pods --namespace istio-system
---
NAME                                    READY   STATUS    RESTARTS      AGE
...
grafana-5f98b97b64-hgh4c                1/1     Running   0             16h
prometheus-67599c8d5c-tcjpd             2/2     Running   0             16h
jaeger-76cd7c7566-8d92t                 1/1     Running   1 (25h ago)   35d
...

```
# Startup Kiali to view the call graph

Kiali is a powerful tool, it can give you a good systemic observability into your Istio cluster. We won't cover it in detail here, but we want you to start it up and get used to using it. For now, we just want to use it to see the graph of our architecture that it discovered by itself. Kiali is a good tool for getting a high level picture of your system.

Open a new terminal and start kiali to view our system:
```
// port forward to Run kiali or...
$ kubectl -n istio-system port-forward deploy/kiali 20001

// Run istioctl to run kiali
$ istioctl dashboard kiali
---
http://localhost:20001/kiali

// Open your web browser to http://localhost:20001
```
Be sure to select the rcl-experiments namespace and click on the "Graph" option in the sidebar. You should see the system similar to shown below w You'll need to run using k6 to generate live traffic to see it show data or it displays an "Empty Graph" message.

Although our little system doesn't demo this capabiliity well, the Graph feature is useful for exploring microservice data flow and dependencies. If there are issues, the line drawn between diagram nodes will be drawn in red to make it more obvious where the problems are. 

# Start up grafana dashboard

Out of the box, Istio knows how to collect RED metrics for our services and more.
Now we have some traffic, let's fire up grafana and see what it has for us.



Now, startup grafana dashboard.

```
$ istioctl dashboard grafana
```
Make sure you start-up prometheus as described above and
you can see some traffic metrics in the prometheus browser, otherwise
Grafana will not show anything. 

If this worked, you should see grafana launched in your default web browser with the url, http://localhost:3000

Next, click on the dashboard named "Istio Workload Dashboard".

On the top of this dashboard, using the namespace drop-down we select which namespace to look into, which is "rcl-experiements" in this case.

Then we can select which of our services to look into with the "workload" drop-down.

Let's investigate each of our service dashboards to understand what Istio has given us already.
As stated earlier the traffic.

Navigate to the 'Browse' section of Grafana. 
Select the "Istio" folder and open the "Istio Workload"
dashboard. You should be able to click around and load our
service to view your traffic you sent to it from earlier.

# Tracing

Istio collects a lot of telemetry when it passes requests around the service mesh. This includes tracing instrumentation so we can build a distributed trace from the service traffic automatically.

Let's see what Istio gives us out of the box.

Let's fire up a Jaeger dashboard in a new terminal and see what Istio collected:

```
// starts jaeger dashboard
$ istioctl dashboard jaeger
```

This will launch the Jaeger dashboard in your default browser with the url: http://localhost:16686

Let's select "tutorial-svc.rcl-experiments" in the left panel and click on 'Find Traces' to view some traces.

-eod


