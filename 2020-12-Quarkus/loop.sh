#!/usr/bin/env bash
while true; do sleep 1; curl -X POST -H "Content-Type: text/plain" -d "Understanding Quarkus" http://localhost:8702/api/books; echo; done
