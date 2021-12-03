#! /bin/bash

# Wait parameters
MAX_RETRIES=60
INTERVAL=10

# Wait for Mongo
# --> No mongo client in container
#while ! mongo --host bae-mongo.docker --eval "printjson(db.serverStatus())";
#do
#    echo "Waiting for MongoDB"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
#done;

# Wait for TMForum APIs
while ! wget -q "http://bae-apis.docker:8080/DSProductInventory";
do
    echo "Waiting for TMForum APIs (Inventory)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSProductCatalog";
do
    echo "Waiting for TMForum APIs (Catalog)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSProductOrdering";
do
    echo "Waiting for TMForum APIs (Ordering)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSPartyManagement";
do
    echo "Waiting for TMForum APIs (Party)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSBillingManagement";
do
    echo "Waiting for TMForum APIs (Billing)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSCustomerManagement";
do
    echo "Waiting for TMForum APIs (Customer)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;
while ! wget -q "http://bae-apis.docker:8080/DSUsageManagement";
do
    echo "Waiting for TMForum APIs (Usage)"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
done;

# Wait for RSS 
#while ! wget -q "http://bae-rss.docker:8080/DSRevenueSharing";
#do
#    echo "Waiting for RSS"; ((i++)) && ((i==${MAX_RETRIES})) && break; sleep ${INTERVAL};
#done; 

# TODO:
# Prepare plugins at:
# - /business-ecosystem-charging-backend/src/plugins
# - /business-ecosystem-charging-backend/src/wstore/asset_manager/resource_plugins/plugins
# + DB?

# Start Charging Backend
echo "Starting Charging Backend"
/entrypoint.sh

