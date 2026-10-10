# CRUD Application on Kubernetes with Minikube

This README documents how I built and ran my Spring Boot CRUD application in Docker, deployed it to a local Kubernetes cluster using Minikube, exposed it with a `NodePort` Service, and accessed it through the Minikube node IP and port.

## Project overview

- **Application:** Spring Boot CRUD application
- **Container image:** `crud:1.0`
- **Kubernetes Deployment:** `crud-deployment`
- **Replicas:** 3
- **Container port:** `8080`
- **Kubernetes Service:** `crud-service`
- **Service type:** `NodePort`
- **Minikube node IP in my environment:** `192.168.49.2`
- **NodePort assigned in my environment:** `31345`

> The node IP and NodePort above are the values observed in my environment. They can be different on another machine or after recreating the Service/cluster.

## How the pieces fit together

1. **Docker image** packages the Spring Boot application and its dependencies.
2. **Minikube** runs a local Kubernetes cluster.
3. **Deployment** tells Kubernetes to maintain three replicas of the application.
4. **Pods** run the containers. Each application instance listens on port `8080`.
5. **Service (`NodePort`)** gives the Pods a stable access point and exposes the service on a port of the Kubernetes node.
6. **Browser/client** can reach the app using the Minikube node IP and NodePort, if that address is reachable from the client.

```text
Browser / curl
     |
     | http://192.168.49.2:31345
     v
Minikube node (NodePort 31345)
     |
     v
Kubernetes Service: crud-service (port 8080)
     |
     +------> Pod 1 (container port 8080)
     +------> Pod 2 (container port 8080)
     +------> Pod 3 (container port 8080)
```

## Prerequisites

- Docker
- `kubectl`
- Minikube
- The project’s Docker image built as `crud:1.0`
- The Spring Boot application listening on `8080` inside the container

Check the tools:

```bash
docker --version
kubectl version --client
minikube version
```

## 1. Start Minikube

```bash
minikube start
```

This starts the local Kubernetes cluster. In this environment, the node was named `minikube`.

Check cluster and node status:

```bash
minikube status
kubectl get nodes -o wide
```

The node should show `Ready`.

## 2. Build the Docker image

Run this from the project directory containing the `Dockerfile`:

```bash
docker build -t crud:1.0 .
```

Check that the image exists in the host Docker image list:

```bash
docker images
```

**Important:** An image built by Fedora’s Docker daemon is not automatically available inside Minikube. Minikube uses its own container runtime (in this environment, `containerd`), so load the image into Minikube next.

## 3. Load the image into Minikube

```bash
minikube image load crud:1.0
```

This copies the locally built image into Minikube so the Kubernetes Pods can use it without pulling it from a remote registry.

Verify that the image is available:

```bash
minikube image ls | grep crud
```

The Deployment uses:

```yaml
image: crud:1.0
imagePullPolicy: IfNotPresent
```

`IfNotPresent` tells Kubernetes to use the image already available on the node when it is present.

## 4. Create the Deployment

Save the following as `crud-deployment.yaml`. Use spaces for indentation; YAML indentation must not contain tabs.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: crud-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: crud
  template:
    metadata:
      labels:
        app: crud
    spec:
      containers:
        - name: crud
          image: crud:1.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
```

Apply it:

```bash
kubectl apply -f crud-deployment.yaml
```

What the main fields mean:

- `kind: Deployment`: creates a controller that maintains the desired number of Pods.
- `replicas: 3`: asks Kubernetes to keep three application Pods running.
- `selector.matchLabels` and `template.metadata.labels`: connect the Deployment to its Pods using the label `app: crud`.
- `image: crud:1.0`: selects the application image.
- `containerPort: 8080`: documents the port used by the application inside each container. **It does not by itself publish the port on the Fedora host.**

Check the Deployment and Pods:

```bash
kubectl get deployments
kubectl get pods -o wide
```

Wait until all three Pods show `Running` and `READY 1/1`.

If a Pod does not start, inspect its details and logs:

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

Replace `<pod-name>` with an actual Pod name from `kubectl get pods`.

## 5. Create a NodePort Service

Save this as `crud-service.yaml`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: crud-service
spec:
  type: NodePort
  selector:
    app: crud
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```

Apply it:

```bash
kubectl apply -f crud-service.yaml
```

Check the Service:

```bash
kubectl get service crud-service
kubectl get svc
```

In my environment, the output included:

```text
NAME           TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)
crud-service   NodePort   10.104.1.217    <none>        8080:31345/TCP
```

This means:

- `port: 8080` is the Service port inside the Kubernetes cluster.
- `targetPort: 8080` is the port on each selected Pod that receives traffic.
- `NodePort` exposes the Service through a port on the node. Kubernetes assigned `31345` in this environment.



## 6. Access the application using the Minikube node IP and NodePort

Find the node IP:

```bash
kubectl get nodes -o wide
```

In my environment, the node IP was `192.168.49.2`. Find the assigned NodePort:

```bash
kubectl get svc crud-service
```

The Service showed NodePort `31345`, so the URL was:

```text
http://192.168.49.2:31345
```

Open that URL in a browser or test it with:

```bash
curl -i http://192.168.49.2:31345
```

The exact response depends on the application's routes. A `404` at `/` can mean the server is reachable but the application does not define a handler for `/`; try a CRUD endpoint defined by the project.

### If the node IP is not reachable

Minikube's node IP is often reachable from the host when using the Docker driver on Linux, but it is not guaranteed to be reachable in every Minikube driver/network configuration. If the URL does not connect, run:

```bash
minikube ip
minikube service crud-service
```

`minikube service crud-service` can open the Service URL or print the URL appropriate to the current environment. For local development, another option is:

```bash
kubectl port-forward service/crud-service 8080:8080
```

Then browse to `http://localhost:8080` while that command remains running. Port forwarding is a temporary local access method; the NodePort Service is the Kubernetes exposure method used in this project.

## 7. What “publicly hosted” means here

A `NodePort` makes the application available through the Kubernetes node's network address and NodePort, provided the client can reach that node. The URL `http://192.168.49.2:31345` is a **private/local network address in this setup**, not automatically a public Internet URL.

To make an application available to people outside your computer or home network, additional networking is needed—for example, a suitably configured public server, firewall/router rules, DNS, and usually HTTPS. Do not expose a development Minikube cluster directly to the Internet without considering authentication, TLS, firewall rules, and application security.

## Command sequence summary

For a fresh local run, assuming the Docker image and YAML files are ready:

```bash
minikube start
docker build -t crud:1.0 .
minikube image load crud:1.0
kubectl apply -f crud-deployment.yaml
kubectl get pods
kubectl apply -f crud-service.yaml
kubectl get svc
kubectl get nodes -o wide
```

Then use the Minikube node IP and the NodePort shown by `kubectl get svc crud-service`, for example:

```text
http://192.168.49.2:31345
```
