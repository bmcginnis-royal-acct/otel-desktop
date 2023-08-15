# Local K8 / Otel configuration

This configuration installs:
* Jaeger in K8 
* Otel collector in k8
* A sample app in k8

We also install the following operators
* Jaeger Operator
* Otel operator - 
* A Cert manager for k8 (for otel operator)

## OTEL K8 Operator


It manages (1) otel collector inside k8 and (2) auto-instrumentation of workloads
using OTEL's libraries. 

# Getting started
Make sure all old k8 app is deleted and all Istio operating 
monitoring tools are not deployed. 

### Install Istio 
Follow directions to install istio (without monitoring tools)
XXX TODO LINK HERE 

### Delete any Istio installed monitoring tools
```
// Check if istio installed monitoring tools are running.
// you will see jaeger, prometheus, etc. if they are. 
$ kubectl get pods -n istio-system

// cd to your Istio directory
$ pushd ~/Brian/devtools/istio-1.18.0 

// delete the monitoring tool artifacts
$ kubectl delete -f ./samples/addons

// Verify the delete worked
$ kubectl get pods -n istio-system   
```



### Delete this app if it's running
```
// check if app is running
$ kubectl get pods

// if it is, delete all the artifacts
$ cd _project_dir_
$ kubectl delete -f ./k8

// verify all is gone
$ kubectl get pods
```

# Install solution

## Install cert manager

Cert Manager: https://cert-manager.io/docs/installation/
As a side effect, this creates a namespace called "cert-manager"
```
$ kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.0/cert-manager.yaml
```

## Deploy the Jaeger Operator
Documentation: https://www.jaegertracing.io/docs/1.47/operator/
Github Jaeger Operator: https://github.com/jaegertracing/jaeger-operator

Install the "latest" version, 1.46  was just latest at time of this writing.
As a side-effect, we create a namespace "observability" to run it on.
```
// Create observability namespace
$ kubectl create namespace observability

// Create the jaeger operator
$ kubectl create -f https://github.com/jaegertracing/jaeger-operator/releases/download/v1.46.0/jaeger-operator.yaml -n observability

// verify operator installed. After a few min, it should look like below.
$ kubectl get deployment jaeger-operator -n observability  
NAME              READY   UP-TO-DATE   AVAILABLE   AGE
jaeger-operator   1/1     1            1           81s
```

Deploy the Jaeger "All in one" strategy with k8 name "simplest"
```
$ kubectl apply -f ./k8/otel/jaeger-simple-all-in-one.yaml 
jaeger.jaegertracing.io/simplest created
```

## Deploy Otel Operator

This is located at: https://github.com/open-telemetry/opentelemetry-operator

```
$ kubectl apply -f https://github.com/open-telemetry/opentelemetry-operator/releases/latest/download/opentelemetry-operator.yaml
```

## Deploy the OTEL collector

```
$ kubectl apply -f ./k8/otel/otel-collector.yaml

// verify the install with
$ kubectl get all
```

## Deploy the OTEL Java Auto-instrumentation CRD for Otel
The auto-instrumentation configuration points the library towards the previously deployed OTel collector 
service "otel-collector" on port 4317 and references 
the image locations for each language type.

```
$ kubectl apply -f ./k8/otel/otel-auto-inst-crd.yaml
```

## Start Jaeger
In a separate terminal, start a port forward for Jaeger UI
```
$ kubectl port-forward svc/simplest-query 16686:16686
```
Make sure it's running by hitting this url with browser:
http://localhost:16686/search


## Deploy the app

We added some key annotations to our Deployment files for our 
services. 

Note the "annotations" section of the yaml file and the sidecar
injection lines:

```
  // App deployment yaml file
  kind: Deployment
  ... Key app deployment section ...
  template:
    metadata:
      labels:
        app: tutorial-svc
        version: v1
      annotations:
        sidecar.opentelemetry.io/inject: "true"
        instrumentation.opentelemetry.io/inject-java: "true"
    spec:
  ... blah, blah, blah
```
In the annotation shown above, we have indicated that we are using the sidecar 
method to spin up an init container when the app's tutorial-svc pod(s) are deployed and we want to inject 
the Java auto-instrumentation code into the tutorial-svc container.

To see this in action, run 
```kubectl describe pod _podname_here_```
you should see injection with the java jar file and otel stuff. 

```
kubectl apply -f ./k8/otel/gateway.yaml
kubectl apply -f ./k8/otel/tutorial-svc-deployment.yaml
kubectl apply -f ./k8/otel/tutorial-svc-virtual-service.yaml
```

### Verify the install


Check pods running
```
$ kubectl get pods
```


Send traffic to service with CURL...
TODO xref traffic sending here

```
// Tail the logs of tutorial-svc 
$ kubectl logs -f -l app=tutorial-svc
```
Hit jaeger with the browser, verify traces are working. 

# Prometheus

## Install
Install one time:

```
$ kubectl apply -n observability -f ./k8/otel/prometheus-cluster.yaml

```

# Resources

* https://techblog.cisco.com/blog/getting-started-with-opentelemetry
* https://medium.com/@tathagatapaul7/opentelemetry-in-kubernetes-deploying-your-collector-and-metrics-backend-b8ec86ac4a43