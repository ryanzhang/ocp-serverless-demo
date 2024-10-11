# OpenShift Serverless
For OCP 4 serverless use case

## Use Case 1: How to config knative serving service retention time 
When running some workload which required longer time to proceed, you might want to increase the retention time before knative terminate your workload (aka scale to zero)

* Option 1 (Recommended): Configure KnativeServing CR:
```yaml
apiVersion: operator.knative.dev/v1beta1
kind: KnativeServing
metadata:
  name: knative-serving
  namespace: knative-serving
spec:
  config:
    autoscaler:
      scale-to-zero-pod-retention-period: 200s
```

* Option 2 Tune **scale-to-zero-pod-retention-period**  field directly in the configmap.
knative-serving -> configmap -> config-autoscaller -> data: scale-to-zero-pod-retention-period

Default value for scale-to-zero-pod-retention-period is 0s

```yaml
apiVersion: v1
data:
  scale-to-zero-pod-retention-period: 300s
  ...
kind: ConfigMap
metadata
    name: config-autoscaler
```

How to verify:
 
 To deploy longprocessapp as knative service and configure app configmap longprocessapp-app-config to 200 seconds, by default the retention-period is 0s
 and the knative workload will be terminated in 1-2 minutes

## User Case 2:  How to make knative serving route only internal accessbile 
By default, public route is exposed through embedded components 3scale-kourier

In order to expose the internal route (ie, make it private services), you can do this:
```
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  annotations:
  labels:
    ...
    networking.knative.dev/visibility: cluster-local
  name: longprocessapp
  namespace: ...
spec:
```

Or by oc cli
```bash
oc label ksvc <service_name> networking.knative.dev/visibility=cluster-local
```


## Use Case 3: How to make knative serving only accessible through apigateway
This can be achived by applying networkpolicy properly.

Please note that you need to set networkpolicy to knative-serving-ingress because ksvc route is expose via knative-serving-ingress namespace(it uses kourier to do the ingress route)

The networkpolicy is defined longprocessapp/src/main/openshift folder, please check there;

How to verify:

* Try to curl the ksvc route in openshift-ingress namespace or any other namespace, the expect behaviour is ksvc will not be active since no request should be hit

* Try to curl ksvc route in 3scale-project namespace since it's configured as apigateway-namespace, the expect behaviour is activate and response well.

* Try to configure the ksvc private route in 3scale managment console and access via the 3scale apigateway route from your local environement, the expect behaviour is ksvc activate and response well.


## Use Case 4:  knative Function experience





## Version info:

OCP version: 4.16

OpenShift Serverless Operator version: 1.33.2

kn Version:      v1.12.0

Knative version:

Supported APIs:
* Serving
  - serving.knative.dev/v1 (knative-serving v1.12.0)
* Eventing
  - sources.knative.dev/v1 (knative-eventing v1.12.0)
  - eventing.knative.dev/v1 (knative-eventing v1.12.0)
