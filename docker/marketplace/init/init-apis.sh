#!/bin/bash

# Wait for MySQL
MAX_RETRIES=60
INTERVAL=5
while ! mysqladmin ping -hlocalhost -P 8152 --silent;
do
    echo "Waiting for MySQL"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;

# Start TMForum APIs
echo "Starting TMForum APIs"
/entrypoint.sh
