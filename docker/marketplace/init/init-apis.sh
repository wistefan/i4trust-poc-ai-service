#!/bin/bash

# Wait for MySQL
MAX_RETRIES=60
INTERVAL=5

# Start TMForum APIs
echo "Starting TMForum APIs"
/entrypoint.sh
