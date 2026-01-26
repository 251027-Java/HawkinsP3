#!/bin/bash
# Setup Index Lifecycle Management (ILM) Policy
# 3 days on disk, then delete (Basic license compatible)

ES_HOST=${ES_HOST:-localhost:9200}

echo "=== Setting up ILM Policy ==="

# Wait for Elasticsearch to be ready
echo "Waiting for Elasticsearch..."
until curl -s "$ES_HOST/_cluster/health" | grep -q '"status":"green"\|"status":"yellow"'; do
    sleep 5
done
echo "Elasticsearch is ready!"

# Clean up existing indices and aliases (in case of re-run)
echo "Cleaning up existing indices..."
curl -X DELETE "$ES_HOST/pilotquiz-logs-*" 2>/dev/null || true
echo ""

# Create ILM policy (Basic license - no searchable_snapshot)
echo "Creating ILM policy..."
curl -X PUT "$ES_HOST/_ilm/policy/pilotquiz-ilm-policy" -H "Content-Type: application/json" -d '
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "0ms",
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_primary_shard_size": "10gb"
          },
          "set_priority": {
            "priority": 100
          }
        }
      },
      "warm": {
        "min_age": "1d",
        "actions": {
          "set_priority": {
            "priority": 50
          },
          "readonly": {}
        }
      },
      "delete": {
        "min_age": "3d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
'

echo ""

# Create index template
echo "Creating index template..."
curl -X PUT "$ES_HOST/_index_template/pilotquiz-logs-template" -H "Content-Type: application/json" -d '
{
  "index_patterns": ["pilotquiz-logs-*"],
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "index.lifecycle.name": "pilotquiz-ilm-policy",
      "index.lifecycle.rollover_alias": "pilotquiz-logs"
    }
  }
}
'

echo ""

# Create initial index with alias
echo "Creating initial index..."
curl -X PUT "$ES_HOST/pilotquiz-logs-000001" -H "Content-Type: application/json" -d '
{
  "aliases": {
    "pilotquiz-logs": {
      "is_write_index": true
    }
  }
}
'

echo ""
echo "=== ILM Policy Setup Complete ==="
echo ""
echo "Policy Summary (Basic License):"
echo "  - Hot:    0-1 day  (active, searchable)"
echo "  - Warm:   1-3 days (read-only)"
echo "  - Delete: 3+ days  (removed from disk)"
echo ""
echo "Note: For S3 snapshots, set up a scheduled snapshot policy manually"
echo "or use a cron job to take regular snapshots before deletion."
