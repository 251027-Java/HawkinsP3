# Kubernetes Deployment Guide

## Prerequisites

- Minikube installed on EC2
- kubectl configured
- AWS RDS PostgreSQL instance created
- ELK EC2 running

## Quick Start

```bash
# Start Minikube
minikube start --driver=docker --memory=8192 --cpus=4

# Enable ingress addon
minikube addons enable ingress

# Create namespaces
kubectl apply -f namespaces.yaml

# Create secrets (edit with your RDS credentials first!)
cp secrets.yaml.example secrets.yaml
# Edit secrets.yaml with your actual values
kubectl apply -f secrets.yaml

# Deploy infrastructure
kubectl apply -f infra/

# Wait for Kafka to be ready
kubectl wait --for=condition=ready pod -l app=kafka -n pilotquiz-infra --timeout=120s

# Deploy backend (in order)
kubectl apply -f backend/eureka-server.yaml
kubectl wait --for=condition=ready pod -l app=eureka-server -n pilotquiz-backend --timeout=120s

kubectl apply -f backend/api-gateway.yaml
kubectl apply -f backend/user-service.yaml
kubectl apply -f backend/quiz-service.yaml
kubectl apply -f backend/progress-service.yaml

# Deploy frontend
kubectl apply -f frontend/

# Deploy ingress
kubectl apply -f ingress.yaml

# Deploy Filebeat (update LOGSTASH_HOST first!)
# Edit monitoring/filebeat-daemonset.yaml with ELK EC2 IP
kubectl apply -f monitoring/
```

## Access Application

```bash
# Get Minikube IP
minikube ip

# Access the app
# Frontend: http://<minikube-ip>/
# API: http://<minikube-ip>/api/
```

## Verify Deployment

```bash
# Check all pods
kubectl get pods -A | grep pilotquiz

# Check services
kubectl get svc -A | grep pilotquiz

# Check Eureka registrations
kubectl port-forward svc/eureka-server 8761:8761 -n pilotquiz-backend
# Open http://localhost:8761
```

## Troubleshooting

```bash
# View pod logs
kubectl logs -f deployment/user-service -n pilotquiz-backend

# Describe pod for events
kubectl describe pod -l app=user-service -n pilotquiz-backend

# Check RDS connectivity
kubectl exec -it deployment/user-service -n pilotquiz-backend -- nc -zv your-rds-endpoint 5432
```

## AWS RDS Setup

1. Create RDS PostgreSQL instance (db.t3.small)
2. Create databases: `userdb`, `quizdb`, `progressdb`
3. Update `secrets.yaml` with RDS endpoint
4. Ensure EC2 security group allows RDS access

## Resource Summary

| Component | Pods | Memory |
|-----------|------|--------|
| Kafka | 1 | 512Mi |
| Zookeeper | 1 | 256Mi |
| Eureka | 1 | 384Mi |
| Gateway | 1 | 384Mi |
| Backend (x3) | 3 | 1536Mi |
| Frontend (x3) | 3 | 192Mi |
| Filebeat | 1 | 128Mi |
| **Total** | 11 | ~3.4GB |
