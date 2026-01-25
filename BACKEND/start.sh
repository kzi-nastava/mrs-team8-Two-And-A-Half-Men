#!/bin/bash
set -e

echo "Starting database..."
./db.sh start

echo "Loading environment variables..."
export $(grep -v '^#' .env | xargs)

echo "Starting Spring Boot app..."
java -jar target/BACKEND-0.0.1-SNAPSHOT.jar
