#!/usr/bin/env bash
# KiroRails post-task hook — runs after completing a task.
# Checks: compile, test, lint. All must pass before commit.
set -euo pipefail

CONF="${KIRORAILS_CONF:-.kiro/kirorails.conf}"
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
FAILED=0

read_conf() { grep "^$1=" "$CONF" 2>/dev/null | cut -d= -f2- | xargs; }

run_check() {
    local label="$1" cmd="$2"
    if [ -z "$cmd" ]; then return 0; fi
    echo -n "  $label... "
    if eval "$cmd" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC}"
    else
        echo -e "${RED}✗ FAILED${NC}"
        FAILED=$((FAILED + 1))
    fi
}

echo "🛤️  KiroRails post-task verification"
echo "─────────────────────────────────────"

run_check "Compile" "$(read_conf compile)"
run_check "Tests  " "$(read_conf test)"
run_check "Lint   " "$(read_conf lint)"
run_check "Security" "$(read_conf security)"

echo "─────────────────────────────────────"
if [ "$FAILED" -gt 0 ]; then
    echo -e "${RED}✗ $FAILED check(s) failed — do NOT commit.${NC}"
    exit 1
else
    echo -e "${GREEN}✓ All checks passed — safe to commit.${NC}"
fi
