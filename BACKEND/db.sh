#!/usr/bin/env bash
set -e

# =====================
# Configuration
# =====================

# PostgreSQL
POSTGRES_CONTAINER="team8_postgres"
POSTGRES_IMAGE="postgres:16"
POSTGRES_VOLUME="team8_pgdata"
DB_NAME="team8"
DB_USER="team8"
DB_PASS="team8"
POSTGRES_PORT=5432

# pgAdmin
PGADMIN_CONTAINER="team8_pgadmin"
PGADMIN_IMAGE="dpage/pgadmin4:latest"
PGADMIN_VOLUME="team8_pgadmin_data"
PGADMIN_EMAIL="team8@team8.com"
PGADMIN_PASS="team8"
PGADMIN_PORT=5050

# Redis
REDIS_CONTAINER="team8_redis"
REDIS_IMAGE="redis:7"
REDIS_VOLUME="team8_redis_data"
REDIS_PORT=6379

# Docker network
NETWORK="team8_net"

# =====================
# Helpers
# =====================

ensure_docker() {
    if ! systemctl is-active --quiet docker; then
        echo "Starting Docker daemon..."
        sudo systemctl start docker
    fi
}

ensure_network() {
    if ! docker network ls --format '{{.Name}}' | grep -q "^$NETWORK$"; then
        echo "Creating Docker network: $NETWORK"
        docker network create "$NETWORK"
    fi
}

ensure_volume() {
    local volume=$1
    if ! docker volume ls --format '{{.Name}}' | grep -q "^$volume$"; then
        echo "Creating volume: $volume"
        docker volume create "$volume"
    fi
}

container_running() {
    docker ps --format '{{.Names}}' | grep -q "^$1$"
}

container_exists() {
    docker ps -a --format '{{.Names}}' | grep -q "^$1$"
}

# =====================
# Services
# =====================

start_postgres() {
    if container_running "$POSTGRES_CONTAINER"; then
        echo "PostgreSQL already running"
        return
    fi

    if container_exists "$POSTGRES_CONTAINER"; then
        echo "Starting existing PostgreSQL container..."
        docker start "$POSTGRES_CONTAINER"
        return
    fi

    echo "Creating and starting PostgreSQL..."
    docker run -d \
        --name "$POSTGRES_CONTAINER" \
        --network "$NETWORK" \
        -e POSTGRES_DB="$DB_NAME" \
        -e POSTGRES_USER="$DB_USER" \
        -e POSTGRES_PASSWORD="$DB_PASS" \
        -p "$POSTGRES_PORT:5432" \
        -v "$POSTGRES_VOLUME:/var/lib/postgresql/data" \
        "$POSTGRES_IMAGE"
}

start_pgadmin() {
    if container_running "$PGADMIN_CONTAINER"; then
        echo "pgAdmin already running"
        return
    fi

    if container_exists "$PGADMIN_CONTAINER"; then
        echo "Starting existing pgAdmin container..."
        docker start "$PGADMIN_CONTAINER"
        return
    fi

    echo "Creating and starting pgAdmin..."
    docker run -d \
        --name "$PGADMIN_CONTAINER" \
        --network "$NETWORK" \
        -e PGADMIN_DEFAULT_EMAIL="$PGADMIN_EMAIL" \
        -e PGADMIN_DEFAULT_PASSWORD="$PGADMIN_PASS" \
        -p "$PGADMIN_PORT:80" \
        -v "$PGADMIN_VOLUME:/var/lib/pgadmin" \
        "$PGADMIN_IMAGE"
}

start_redis() {
    if container_running "$REDIS_CONTAINER"; then
        echo "Redis already running"
        return
    fi

    if container_exists "$REDIS_CONTAINER"; then
        echo "Starting existing Redis container..."
        docker start "$REDIS_CONTAINER"
        return
    fi

    echo "Creating and starting Redis..."
    docker run -d \
        --name "$REDIS_CONTAINER" \
        --network "$NETWORK" \
        -p "$REDIS_PORT:6379" \
        -v "$REDIS_VOLUME:/data" \
        "$REDIS_IMAGE" \
        redis-server --appendonly yes
}

# =====================
# CLI commands
# =====================

case "$1" in
    start)
        ensure_docker
        ensure_network
        ensure_volume "$POSTGRES_VOLUME"
        ensure_volume "$PGADMIN_VOLUME"
        ensure_volume "$REDIS_VOLUME"
        start_postgres
        start_pgadmin
        start_redis
        echo "Team8 stack is ready 🚀"
        ;;

    stop)
        echo "Stopping containers..."
        docker stop "$POSTGRES_CONTAINER" "$PGADMIN_CONTAINER" "$REDIS_CONTAINER" 2>/dev/null || true
        ;;

    status)
        docker ps -a --filter "name=team8"
        ;;

    admin)
        xdg-open "http://localhost:$PGADMIN_PORT" >/dev/null 2>&1
        ;;

    cli)
        psql -h localhost -p "$POSTGRES_PORT" -U "$DB_USER" -d "$DB_NAME"
        ;;

    redis)
        docker exec -it "$REDIS_CONTAINER" redis-cli
        ;;

    *)
        echo "Usage: team8 {start|stop|status|admin|cli|redis}"
        exit 1
        ;;
esac

