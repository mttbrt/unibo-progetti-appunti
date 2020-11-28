#!/bin/bash

# Reset Docker containers and Mongodb buses collection
./scripts/stop_mongo.sh
wait
./scripts/stop_docker.sh
wait
docker rm -f $(docker ps -a -q)
wait
docker volume rm $(docker volume ls -q)
wait
./scripts/start_mongo.sh
wait
./scripts/start_docker.sh
wait
mongo radflix --eval "db.buses.drop();"
wait
mongo radflix --eval "db.purchases.drop();"
wait
mongo radflix --eval "db.accessrequests.drop();"
wait
