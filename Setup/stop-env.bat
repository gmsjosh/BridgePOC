@echo off
pushd %~dp0
echo Stopping Docker Containers
docker-compose down
echo Removing Volumes
docker volume prune -f