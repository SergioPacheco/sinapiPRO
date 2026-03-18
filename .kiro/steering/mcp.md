---
description: MCP server configuration — how to connect external tools to SpecRail workflows
inclusion: manual
---

# MCP Integration Steering

SpecRail subagents tell Kiro what to do. MCP servers give Kiro the tools to do it. This file documents how to connect MCP servers to your SpecRail workflow.

## What MCP adds to SpecRail

Without MCP, Kiro can read files, run commands, and write code. With MCP, Kiro can also:
- Query your database directly (verify migrations, check data)
- Trigger CI/CD pipelines (run tests remotely, deploy)
- Browse your running application (end-to-end verification)
- Read external documentation (API specs, Confluence pages)
- Interact with issue trackers (Jira, Linear, GitHub Issues)

## Recommended MCP servers by workflow

### Database verification (postgres pack)
```json
{
  "mcpServers": {
    "postgres": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-postgres", "postgresql://user:pass@localhost:5432/devdb"]
    }
  }
}
```
Use with: bugfix-investigator (query data to reproduce), verifier (check migration applied), planner (understand schema)

### Browser testing (all packs)
```json
{
  "mcpServers": {
    "puppeteer": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-puppeteer"]
    }
  }
}
```
Use with: verifier (end-to-end feature verification), bugfix-investigator (reproduce UI bugs)

### Filesystem search (brownfield projects)
```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/path/to/project"]
    }
  }
}
```
Use with: codebase-mapper (deep analysis of large codebases)

### GitHub integration
```json
{
  "mcpServers": {
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": { "GITHUB_PERSONAL_ACCESS_TOKEN": "${GITHUB_TOKEN}" }
    }
  }
}
```
Use with: team mode (PR creation, issue tracking), report-generator (link to PRs)

## Configuration

MCP servers are configured in your Kiro project settings. Place the config in `.kiro/settings/mcp.json` or your global Kiro config.

## Rules for MCP + SpecRail

- MCP servers provide data and actions — subagents decide what to do with them
- Never let MCP access production databases from development workflows
- Use read-only database connections for verification and investigation
- Browser MCP is for testing, not for scraping or automation outside the project
- Log all MCP interactions that modify external state (DB writes, deployments)
- If an MCP server is unavailable, the subagent should degrade gracefully (skip that verification step, note it in the report)

## How subagents reference MCP

Subagents don't need to know MCP internals. They describe what they need:

```markdown
## Workflow
1. Query the database to check if the migration was applied
2. Browse to /api/v1/users and verify the response schema
3. Check the CI pipeline status for the latest commit
```

If the corresponding MCP server is available, Kiro uses it. If not, the subagent falls back to CLI commands or skips that step with a note.
