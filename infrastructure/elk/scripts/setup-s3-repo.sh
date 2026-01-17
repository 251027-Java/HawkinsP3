#!/bin/bash
# Setup S3 Repository for Elasticsearch Snapshots
# Run this after ELK stack is up and running

ES_HOST=${ES_HOST:-localhost:9200}
S3_BUCKET=${S3_BUCKET:-pilotquiz-elk-logs}
AWS_REGION=${AWS_REGION:-us-west-1}

echo "=== Setting up S3 Snapshot Repository ==="

# Wait for Elasticsearch to be ready
echo "Waiting for Elasticsearch..."
until curl -s "$ES_HOST/_cluster/health" | grep -q '"status":"green"\|"status":"yellow"'; do
    sleep 5
done
echo "Elasticsearch is ready!"

# Register S3 repository
echo "Registering S3 repository..."
curl -X PUT "$ES_HOST/_snapshot/s3_repository" -H "Content-Type: application/json" -d "
{
  \"type\": \"s3\",
  \"settings\": {
    \"bucket\": \"$S3_BUCKET\",
    \"region\": \"$AWS_REGION\",
    \"base_path\": \"elasticsearch-snapshots\"
  }
}
"

echo ""
echo "Verifying repository..."
curl -X POST "$ES_HOST/_snapshot/s3_repository/_verify"

echo ""
echo "=== S3 Repository Setup Complete ==="
