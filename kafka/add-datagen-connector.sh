#!/bin/sh

# When connect cluster container is up it takes a while for the REST API to go live
while :
do
    curl --head --fail http://connect:8083/
    RESULT=$?
    if [ $RESULT -eq 0 ]; then
        break
    fi
    printf '...\n'
    sleep 3
done

# Call the REST endpoint with provided config to create a new connector
curl -X POST -H "Content-Type: application/json" --data "@/etc/connector.config" http://connect:8083/connectors