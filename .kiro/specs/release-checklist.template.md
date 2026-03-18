# Release Checklist — [version]

## Pre-release

- [ ] All specs for this release are verified (verifier passed)
- [ ] All feedback loops pass on main branch (`mvn test`, `npm test`, etc.)
- [ ] No open critical or high-priority bugs
- [ ] CHANGELOG_AI.md reviewed and cleaned up for human readability
- [ ] DECISIONS.md reviewed — no unresolved architectural questions
- [ ] RISKS.md reviewed — all high risks mitigated or accepted with justification
- [ ] Database migrations tested against production-like data volume
- [ ] Rollback plan documented for each migration
- [ ] Security review: no secrets in code, dependencies scanned, auth tested
- [ ] API documentation updated (OpenAPI spec, README, etc.)

## Release

- [ ] Version bumped in build config (pom.xml, package.json, pyproject.toml)
- [ ] Release branch or tag created
- [ ] CI/CD pipeline passes on release branch
- [ ] Artifact built and published (Docker image, JAR, package)
- [ ] Database migrations applied to staging
- [ ] Smoke test passed on staging environment
- [ ] Production deployment executed
- [ ] Smoke test passed on production

## Post-release

- [ ] Monitoring dashboards checked (error rates, latency, resource usage)
- [ ] Completed specs archived to `specs/archive/`
- [ ] STATE.md updated with release summary
- [ ] Team notified of release
- [ ] Release notes published (GitHub release, internal docs)

## Rollback criteria

If any of these occur within 1 hour of deployment:
- Error rate increases by >5% compared to pre-release baseline
- P95 latency increases by >50%
- Any data integrity issue detected
- Critical functionality broken

**Rollback procedure:**
1. Deploy previous artifact version
2. Run rollback migration scripts (if applicable)
3. Verify rollback with smoke tests
4. Document what went wrong in RISKS.md
