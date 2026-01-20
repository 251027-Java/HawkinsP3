# Jenkins Build Agent Setup

## Quick Start

### Prerequisites

- Docker and Docker Compose installed on Build Agent EC2
- Security group allowing port 8080 (Jenkins UI)
- GitHub webhook access (if using webhooks)

### Deploy Jenkins

```bash
# SSH into Build Agent EC2
ssh buildagent

# Clone the repo (or copy the jenkins folder)
cd /opt
git clone https://github.com/251027-Java/HawkinsP3.git pilotquiz
cd pilotquiz/infrastructure/jenkins

# Start Jenkins
docker-compose -f docker-compose.jenkins.yml up -d

# View initial admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Access Jenkins

Open `http://<buildagent-ec2-ip>:8080` in your browser.

---

## Initial Configuration

1. Enter the initial admin password
2. Install suggested plugins
3. Create admin user
4. Configure Jenkins URL

### Required Plugins

- **Pipeline** - Pipeline jobs support
- **Git** - Git SCM integration
- **Docker Pipeline** - Docker build steps
- **Credentials Binding** - Secure credential injection

### DockerHub Credentials

1. Manage Jenkins → Credentials → System → Global credentials
2. Add Credentials:
   - Kind: Username with password
   - Username: `<dockerhub-username>`
   - Password: `<dockerhub-token>`
   - ID: `dockerhub-credentials`

---

## Pipeline Jobs

Create the following pipeline jobs pointing to `Jenkinsfile` in the repo:

| Job Name | Jenkinsfile Path |
|----------|------------------|
| pilotquiz-orchestrator | `Jenkinsfile` |
| pilotquiz-eureka-server | `jenkinsfiles/Jenkinsfile.eureka-server` |
| pilotquiz-api-gateway | `jenkinsfiles/Jenkinsfile.api-gateway` |
| pilotquiz-user-service | `jenkinsfiles/Jenkinsfile.user-service` |
| pilotquiz-quiz-service | `jenkinsfiles/Jenkinsfile.quiz-service` |
| pilotquiz-progress-service | `jenkinsfiles/Jenkinsfile.progress-service` |
| pilotquiz-root-config | `jenkinsfiles/Jenkinsfile.root-config` |
| pilotquiz-mfe-react-auth | `jenkinsfiles/Jenkinsfile.mfe-react-auth` |
| pilotquiz-mfe-angular-quiz | `jenkinsfiles/Jenkinsfile.mfe-angular-quiz` |

---

## Troubleshooting

### Docker permission denied

```bash
# Add jenkins user to docker group
docker exec -u root jenkins usermod -aG docker jenkins
docker restart jenkins
```

### Out of memory

```bash
# Check memory usage
docker stats jenkins

# Increase memory limit in docker-compose.jenkins.yml
```

### View logs

```bash
docker-compose -f docker-compose.jenkins.yml logs -f jenkins
```

---

## Security Groups

| Port | Source | Purpose |
|------|--------|---------|
| 8080 | Your IP | Jenkins UI |
| 50000 | Internal | Agent communication |
| 22 | Your IP | SSH |
