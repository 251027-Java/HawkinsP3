# PilotQuiz Deployment Walkthrough

## Current Infrastructure Status

| Component | Status | IP/Endpoint |
|-----------|--------|-------------|
| Jenkins EC2 (buildagent) | ✅ Running | 3.101.105.230 |
| ELK EC2 (elk) | ✅ Running | 3.101.73.3 |
| App EC2 (app) | ✅ Running | 54.219.206.229 |
| RDS PostgreSQL | ✅ Running | (needs databases) |
| S3 Logs Bucket | ✅ Running | pilotquiz-elk-logs |

---

## Deployment Steps

### Phase 1: Complete RDS Setup

SSH to any EC2 and run:

```bash
# Connect to RDS (use your endpoint)
psql -h your-rds-endpoint.region.rds.amazonaws.com -U pilotquiz -d postgres

# Create databases
CREATE DATABASE userdb;
CREATE DATABASE quizdb;
CREATE DATABASE progressdb;

# Verify
\l
\q
```

---

### Phase 2: Jenkins-GitHub Build Gate Integration

#### Step 2.1: Install Jenkins Plugins

On Jenkins (<http://3.101.105.230:8080>):

1. **Manage Jenkins → Plugins → Available**
2. Install:
   - GitHub Integration Plugin
   - GitHub Branch Source Plugin
   - Pipeline: GitHub Notify

3. Restart Jenkins

#### Step 2.2: Create GitHub Personal Access Token

1. GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens (classic)
2. Generate new token with scopes:
   - ✅ `repo`
   - ✅ `admin:repo_hook`
3. **Save the token!**

#### Step 2.3: Add Token to Jenkins

1. **Manage Jenkins → Credentials → System → Global**
2. Add Credentials:
   - Kind: **Secret text**
   - Secret: (paste token)
   - ID: `github-token`

#### Step 2.4: Configure GitHub Server in Jenkins

1. **Manage Jenkins → System → GitHub Servers**
2. Add GitHub Server:
   - Name: `GitHub`
   - API URL: `https://api.github.com`
   - Credentials: `github-token`
3. Test connection → Save

#### Step 2.5: Create GitHub Webhook

1. GitHub repo → Settings → Webhooks → Add webhook
2. Configure:
   - Payload URL: `http://3.101.105.230:8080/github-webhook/`
   - Content type: `application/json`
   - Events: Push, Pull requests
3. Save

#### Step 2.6: Test Integration

1. Push a small change to `dev` branch
2. Verify Jenkins build triggers
3. Check GitHub commit shows status

---

### Phase 3: Deploy ELK Stack

SSH to ELK EC2:

```bash
ssh elk

# Clone repo
cd /opt
sudo git clone https://github.com/251027-Java/HawkinsP3.git pilotquiz
cd pilotquiz/infrastructure/elk

# Set vm.max_map_count for Elasticsearch
sudo sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf

# Create .env file
sudo cp .env.example .env
sudo nano .env
# Add your AWS credentials OR leave empty if using EC2 instance role

# Start ELK
sudo docker-compose -f docker-compose.elk.yml up -d

# Wait for healthy (2-3 minutes)
sudo docker-compose -f docker-compose.elk.yml logs -f elasticsearch

# Setup S3 repository
chmod +x scripts/*.sh
./scripts/setup-s3-repo.sh

# Setup ILM policy
./scripts/setup-ilm.sh
```

**Verify**: Open <http://3.101.73.3:5601> (Kibana)

---

### Phase 4: Deploy Application to Minikube

SSH to App EC2:

```bash
ssh app

# Install Minikube (if not installed)
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube

# Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install kubectl /usr/local/bin/kubectl

# Start Minikube
minikube start --driver=docker --memory=6144 --cpus=2

# Enable ingress
minikube addons enable ingress

# Clone repo
cd /opt
sudo git clone https://github.com/251027-Java/HawkinsP3.git pilotquiz
cd pilotquiz/infrastructure/k8s

# Create namespaces
kubectl apply -f namespaces.yaml

# Create secrets (edit first!)
cp secrets.yaml.example secrets.yaml
nano secrets.yaml
# Update with RDS endpoint and credentials
kubectl apply -f secrets.yaml

# Deploy in order
kubectl apply -f infra/
kubectl wait --for=condition=ready pod -l app=kafka -n pilotquiz-infra --timeout=180s

kubectl apply -f backend/eureka-server.yaml
kubectl wait --for=condition=ready pod -l app=eureka-server -n pilotquiz-backend --timeout=180s

kubectl apply -f backend/
kubectl apply -f frontend/
kubectl apply -f ingress.yaml

# Update Filebeat with ELK IP
nano monitoring/filebeat-daemonset.yaml
# Set LOGSTASH_HOST to 3.101.73.3
kubectl apply -f monitoring/
```

**Verify**:

```bash
kubectl get pods -A | grep pilotquiz
minikube ip
# Access http://<minikube-ip>/
```

---

### Phase 5: Expose Minikube to Internet

```bash
# Option 1: Port forward (temporary)
kubectl port-forward --address 0.0.0.0 svc/root-config 80:9000 -n pilotquiz-frontend

# Option 2: Use minikube tunnel (recommended)
minikube tunnel
```

---

### Phase 6: Verify Full Stack

| Check | Command/URL |
|-------|-------------|
| Pods running | `kubectl get pods -A` |
| Eureka dashboard | Port forward 8761-8761 |
| Frontend | <http://app-ec2-ip> |
| Kibana | <http://3.101.73.3:5601> |
| Logs in Kibana | Search `pilotquiz-logs-*` |

---

## Security Group Checklist

### Jenkins EC2

- 8080: Your IP (Jenkins UI)
- 22: Your IP (SSH)

### ELK EC2

- 5601: Your IP (Kibana)
- 5044: App EC2 SG (Logstash)
- 22: Your IP (SSH)

### App EC2

- 80/443: 0.0.0.0/0 (Public access)
- 22: Your IP (SSH)

### RDS

- 5432: App EC2 SG

---

## Quick Reference

```bash
# SSH shortcuts
ssh buildagent   # Jenkins
ssh elk          # ELK
ssh app          # App/Minikube

# Check pod status
kubectl get pods -A | grep pilotquiz

# View logs
kubectl logs -f deployment/user-service -n pilotquiz-backend

# Restart deployment
kubectl rollout restart deployment/user-service -n pilotquiz-backend
```
