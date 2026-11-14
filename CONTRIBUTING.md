# Contributing

## Scope and lifecycle

Spring Data Mirage `5.x` is the Current standard artifact line for active development. The `4.x` Legacy artifact family is maintenance-only. A contribution must target an existing branch and must not claim behavior that is not present in that branch.

Target `maintenance/{major}.{minor}.x` for standard artifacts and `maintenance/legacy/{major}.{minor}.x` for Legacy artifacts. Never merge a fix into the other artifact family without adapting and testing it against that family's artifactId, Java packages, Mirage-SQL dependencies, and compatibility contract.

The project may maintain one minor line or multiple minor lines within either family. Check [Security Policy](SECURITY.md) and [Compatibility](COMPATIBILITY.md) before opening a change. Use a short-lived `fix/*` or `security/*` branch for an individual change, then apply it to every affected supported line.

## Development setup

Use the JDK and Maven versions required by the target release line. From the repository root:

```bash
mvn clean verify
```

This repository is a single-module Maven project. Do not introduce a new module or dependency without a concrete requirement and compatibility review.

## Change requirements

- Keep changes focused and consistent with the existing package and resource layout.
- Add or update tests for behavior changes and regressions.
- Preserve backward compatibility on a maintenance line unless an approved security fix cannot be implemented compatibly.
- Keep repository interfaces and their SQL resources in corresponding package paths.
- Explain the runtime scope, license, security impact, and necessity of any new dependency.
- Keep code comments, Javadoc, identifiers, exception messages, and log messages in English.
- Describe user-visible changes, compatibility impact, and migration requirements in the pull request.
- Update `COMPATIBILITY.md` or the relevant migration guide when runtime, dependency, API, coordinate, package, or configuration compatibility changes.
- Do not describe an artifact as released or available until publication is verified.

## Testing

Run the full build before submitting a change:

```bash
mvn clean verify
```

Database-specific changes should include verification against the affected database and JDBC driver where feasible. The H2 test suite alone is not evidence of compatibility with every Mirage-SQL dialect.

## Commit and review information

Provide a concise problem statement, implementation rationale, compatibility impact, test evidence, and migration implications. Link the relevant issue when one exists and identify any environment that was not tested.

Do not commit credentials, private repository settings, signing material, generated build output, or customer data.
