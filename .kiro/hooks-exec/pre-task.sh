#!/usr/bin/env bash
# KiroRails pre-task hook — runs before starting a task.
# Checks: compilation passes, no uncommitted changes.
set -euo pipefail

CONF="${KIRORAILS_CONF:-.kiro/kirorails.conf}"
RED='\033[0;31m'; GREEN='\033[0;32m'; NC='\033[0m'

read_conf() { grep "^$1=" "$CONF" 2>/dev/null | cut -d= -f2- | xargs; }

echo "🛤️  KiroRails pre-task check"
echo "─────────────────────────────"

# Check for uncommitted changes
if [ -n "$(git status --porcelain 2>/dev/null)" ]; then
    echo -e "${RED}✗${NC} Uncommitted changes detected — commit or stash first"
    exit 1
fi
echo -e "${GREEN}✓${NC} Working tree clean"

# Run compile check
CMD=$(read_conf compile)
if [ -n "$CMD" ]; then
    echo -n "  Compiling... "
    if eval "$CMD" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗${NC} Compilation failed — fix before starting task"
        exit 1
    fi
fi

echo "─────────────────────────────"
echo -e "${GREEN}Ready to start task.${NC}"
