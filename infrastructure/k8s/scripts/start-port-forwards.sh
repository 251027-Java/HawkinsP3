#!/bin/bash
# Port forwarding script for PilotQuiz Kubernetes services
# Uses Ingress Controller for single-point access
# Run on App EC2 to expose services externally

set -e

KUBECONFIG_PATH="/home/ubuntu/.kube/config"

echo "=== Stopping any existing port-forwards ==="
pkill -f "kubectl port-forward" 2>/dev/null || true
sleep 2

echo "=== Starting Ingress Controller port-forward ==="
# Single port-forward to Ingress Controller handles all routing
# This survives pod restarts since it connects to the service, not pod
sudo KUBECONFIG=$KUBECONFIG_PATH kubectl port-forward --address 0.0.0.0 svc/ingress-nginx-controller 80:80 -n ingress-nginx > /dev/null 2>&1 &

sleep 3

echo ""
echo "=== Ingress port-forward active ==="
ps aux | grep "kubectl port-forward" | grep -v grep

echo ""
echo "=== Access URLs (all through Ingress) ==="
EC2_IP=$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4 2>/dev/null || echo "<EC2-PUBLIC-IP>")
echo "Frontend:      http://$EC2_IP/"
echo "React Auth JS: http://$EC2_IP/auth/pilotquiz-react-auth.js"
echo "Angular MFE:   http://$EC2_IP/quiz/main.js"
echo "API Gateway:   http://$EC2_IP/api/"
echo ""
echo "All routes handled by Ingress - survives pod restarts!"
echo "To stop: pkill -f 'kubectl port-forward'"
