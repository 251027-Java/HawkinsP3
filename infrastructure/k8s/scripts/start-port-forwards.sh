#!/bin/bash
# Port forwarding script for PilotQuiz Kubernetes services
# Run on App EC2 to expose services externally

set -e

KUBECONFIG_PATH="/home/ubuntu/.kube/config"

echo "=== Stopping any existing port-forwards ==="
pkill -f "kubectl port-forward" 2>/dev/null || true
sleep 2

echo "=== Starting port forwards ==="

# Frontend services
echo "Starting root-config on port 80..."
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/root-config 80:80 -n pilotquiz-frontend > /dev/null 2>&1 &

echo "Starting mfe-react-auth on port 8080..."
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/mfe-react-auth 8080:8080 -n pilotquiz-frontend > /dev/null 2>&1 &

echo "Starting mfe-angular-quiz on port 8081..."
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/mfe-angular-quiz 8081:8081 -n pilotquiz-frontend > /dev/null 2>&1 &

# Backend API Gateway
echo "Starting api-gateway on port 8888..."
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/api-gateway 8888:8888 -n pilotquiz-backend > /dev/null 2>&1 &

sleep 3

echo ""
echo "=== Port forwards active ==="
ps aux | grep "kubectl port-forward" | grep -v grep

echo ""
echo "=== Access URLs ==="
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "<EC2-PUBLIC-IP>")
echo "Frontend:    http://$EC2_IP/"
echo "React Auth:  http://$EC2_IP:8080/"
echo "Angular MFE: http://$EC2_IP:8081/"
echo "API Gateway: http://$EC2_IP:8888/"
echo ""
echo "To stop all port-forwards: pkill -f 'kubectl port-forward'"
