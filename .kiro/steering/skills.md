---
description: Custom Skills — reusable patterns that auto-inject into agent context
inclusion: always
---

# Custom Skills System

## What are skills?

Skills follow the open [Agent Skills](https://agentskills.io) standard. They are folders containing a `SKILL.md` file with instructions, scripts, and templates that Kiro activates when relevant to your task.

## Skill locations

| Scope | Path | Priority | Shared with |
|-------|------|----------|-------------|
| Project | `.kiro/skills/` | Higher (overrides user) | Team (version-controlled) |
| User | `~/.kiro/skills/` | Lower (fallback) | All your projects |

## Skill format

```
my-skill/
├── SKILL.md           # Required — instructions and metadata
├── scripts/           # Optional — executable code
├── references/        # Optional — documentation
└── assets/            # Optional — templates
```

### SKILL.md frontmatter

```yaml
---
name: my-skill
description: When and why to use this skill. Kiro matches this against your requests.
---
```

- `name` must match the folder name (lowercase, hyphens, max 64 chars)
- `description` tells Kiro when to activate (max 1024 chars) — be specific with keywords

## How activation works

Kiro uses progressive disclosure:
1. At startup, loads only name + description of each skill
2. When your request matches a description, loads the full SKILL.md
3. Scripts and references load only as needed

## Managing skills

```bash
kirorails skill list                    # list all skills (project + user)
kirorails skill add "name"              # create a new skill from template
kirorails skill search "keyword"        # find skills matching a keyword
```

Skills also appear as slash commands — type `/` in Kiro chat to see them.
