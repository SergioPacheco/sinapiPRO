#!/usr/bin/env bash
# init.sh — Project environment setup
#
# This script gets the project into a runnable state.
# The Ralph loop and coding agents read this to know how to start the project.
#
# Customize this for your project after running bootstrap.py.

set -e

echo "=== Project Setup ==="

# --- Dependencies ---
# Uncomment and adapt for your stack:
# mvn dependency:resolve          # Java / Maven
# ./gradlew dependencies          # Java / Gradle
# npm install                     # Node.js
# pip install -r requirements.txt # Python

# --- Database ---
# Uncomment if your project needs a database:
# docker compose up -d postgres
# sleep 2
# mvn flyway:migrate              # Run migrations

# --- Build ---
# Uncomment for your stack:
# mvn compile                     # Java
# npm run build                   # Node.js

# --- Verify ---
# Run a quick smoke test to confirm the project is healthy:
# mvn test -pl :core              # Java — run core module tests only
# npm test -- --bail              # Node.js — stop on first failure

echo "=== Setup complete ==="
