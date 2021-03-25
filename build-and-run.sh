#!/usr/bin/env bash
docker build -t bootcamp:latest .
docker stop bootcamp || true
docker rm bootcamp || true
docker run --name bootcamp --env SPRING_PROFILES_ACTIVE=dev,docker -p 8080:8080 -v $(pwd)/agreements:/opt/agreements bootcamp:latest
