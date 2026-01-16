# ELK Stack Deployment Guide

## Quick Start

### Prerequisites

- Docker and Docker Compose installed on ELK EC2
- S3 bucket created (see [AWS Setup](#aws-setup))
- Security group allowing ports: 5601 (Kibana), 5044 (Logstash)

### Deploy ELK Stack

```bash
# SSH into ELK EC2
ssh elk-ec2

# Clone the repo (or copy the elk folder)
cd /opt
git clone https://github.com/251027-Java/HawkinsP3.git pilotquiz
cd pilotquiz/infrastructure/elk

# Copy and edit environment variables
cp .env.example .env
nano .env  # Add your AWS credentials

# Start ELK stack
docker-compose -f docker-compose.elk.yml up -d

# Wait for Elasticsearch to be healthy (1-2 minutes)
docker-compose -f docker-compose.elk.yml logs -f elasticsearch

# Setup S3 repository
chmod +x scripts/*.sh
./scripts/setup-s3-repo.sh

# Setup ILM policy
./scripts/setup-ilm.sh
```

### Access Kibana

Open `http://<elk-ec2-ip>:5601` in your browser.

---

## AWS Setup

### 1. Create S3 Bucket

```bash
aws s3 mb s3://pilotquiz-elk-logs --region us-east-1
```

Or via AWS Console:

1. S3 → Create bucket
2. Name: `pilotquiz-elk-logs`
3. Block all public access: ✅ Enabled
4. Create bucket

### 2. Create IAM Policy

Create policy `PilotQuizELKS3Access`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket", "s3:GetBucketLocation"],
      "Resource": "arn:aws:s3:::pilotquiz-elk-logs"
    },
    {
      "Effect": "Allow",
      "Action": ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
      "Resource": "arn:aws:s3:::pilotquiz-elk-logs/*"
    }
  ]
}
```

### 3. Attach to EC2

**Option A: IAM User** (for testing)

- Create IAM user with the policy
- Add access keys to `.env`

**Option B: EC2 Instance Role** (recommended)

- Create IAM role with the policy
- Attach to ELK EC2 instance
- Leave AWS credentials empty in `.env`

---

## Log Retention Policy

| Phase | Duration | Location |
|-------|----------|----------|
| Hot | 0-1 day | Elasticsearch (active) |
| Warm | 1-3 days | Elasticsearch (read-only) |
| Cold | 3-30 days | S3 snapshot |
| Delete | 30+ days | Deleted |

---

## Security Groups

### ELK EC2 Security Group

| Port | Source | Purpose |
|------|--------|---------|
| 5601 | Your IP | Kibana UI |
| 5044 | App EC2 SG | Filebeat → Logstash |
| 22 | Your IP | SSH |

---

## Troubleshooting

### Elasticsearch won't start

```bash
# Check logs
docker-compose -f docker-compose.elk.yml logs elasticsearch

# Common fix: increase vm.max_map_count
sudo sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
```

### S3 repository not working

```bash
# Verify S3 access
aws s3 ls s3://pilotquiz-elk-logs

# Check repository status
curl localhost:9200/_snapshot/s3_repository
```

### No logs appearing

```bash
# Check Logstash is receiving logs
docker-compose -f docker-compose.elk.yml logs logstash

# Verify index exists
curl localhost:9200/_cat/indices
```
