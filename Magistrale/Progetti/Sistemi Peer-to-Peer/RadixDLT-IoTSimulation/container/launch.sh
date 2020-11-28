#!/bin/bash

# Run the server
./scripts/start_mongo.sh
wait
./scripts/start_docker.sh
wait
