# Local Jenkins Setup Guide

## Quick Start with Docker

### 1. Start Jenkins Container

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

> **Windows Docker Desktop**: Enable "Expose daemon on tcp://localhost:2375 without TLS" in Settings → General

### 2. Get Initial Admin Password

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 3. Access Jenkins

Open <http://localhost:8080> and paste the password.

---

## Initial Setup

### Install Suggested Plugins

When prompted, click **"Install suggested plugins"**

### Additional Plugins Required

Go to: `Manage Jenkins` → `Plugins` → `Available plugins`

Install:

- **Pipeline**
- **Git**
- **Docker Pipeline**
- **Pipeline: Build Step** (for `build job:` step)

---

## Configure Environment Variables

Go to: `Manage Jenkins` → `System` → `Global properties`

1. Check **"Environment variables"**
2. Add:

   | Name | Value |
   |------|-------|
   | `DOCKERHUB_USERNAME` | your-dockerhub-username |
   | `DOCKERHUB_PASSWORD` | your-dockerhub-password-or-token |

---

## Install Docker in Jenkins Container

```bash
# Enter Jenkins container as root
docker exec -u root -it jenkins bash

# Install Docker CLI
apt-get update && apt-get install -y docker.io

# Add jenkins user to docker group
usermod -aG docker jenkins

# Exit and restart container
exit
docker restart jenkins
```

---

## Create Pipeline Jobs

### For Each Service (8 total)

1. Click **"New Item"**
2. Enter name: `pilotquiz-eureka-server`
3. Select **"Pipeline"** → OK
4. Scroll to **Pipeline** section:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/251027-Java/HawkinsP3.git`
   - Branch: `*/dev`
   - Script Path: `jenkinsfiles/Jenkinsfile.eureka-server`
5. Click **Save**

Repeat for all services:

| Job Name | Script Path |
|----------|-------------|
| `pilotquiz-eureka-server` | `jenkinsfiles/Jenkinsfile.eureka-server` |
| `pilotquiz-api-gateway` | `jenkinsfiles/Jenkinsfile.api-gateway` |
| `pilotquiz-user-service` | `jenkinsfiles/Jenkinsfile.user-service` |
| `pilotquiz-quiz-service` | `jenkinsfiles/Jenkinsfile.quiz-service` |
| `pilotquiz-progress-service` | `jenkinsfiles/Jenkinsfile.progress-service` |
| `pilotquiz-root-config` | `jenkinsfiles/Jenkinsfile.root-config` |
| `pilotquiz-mfe-react-auth` | `jenkinsfiles/Jenkinsfile.mfe-react-auth` |
| `pilotquiz-mfe-angular-quiz` | `jenkinsfiles/Jenkinsfile.mfe-angular-quiz` |

### Create Orchestrator Job

1. **New Item** → Name: `pilotquiz-orchestrator`
2. **Pipeline** → Script Path: `Jenkinsfile`

---

## Test a Single Pipeline

1. Go to `pilotquiz-eureka-server`
2. Click **"Build Now"**
3. Watch the **Console Output**
4. Verify image appears on DockerHub

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Docker permission denied | Run `docker exec -u root jenkins chmod 666 /var/run/docker.sock` |
| Cannot pull Docker images | Ensure Jenkins container has internet access |
| Stash/unstash errors | Increase workspace size or clean old builds |

## Notes

- **No Maven/Node installation needed** - Pipelines use Docker agents (`maven:3.9-eclipse-temurin-17`, `node:18-alpine`)
- **Docker-in-Docker** - Jenkins runs Docker commands by mounting the host's Docker socket
