# Releasing Spring Data Mirage

This repository is a single-module Maven project. The release version and the matching Mirage-SQL dependency line must be verified together.

## Release policy

- `5.x` is the Current standard artifact line. Active development occurs on `develop`, while `main` contains the latest completed standard release.
- Standard and Legacy artifacts are separate release families. Standard maintenance branches use `maintenance/{major}.{minor}.x`. Legacy maintenance branches use `maintenance/legacy/{major}.{minor}.x`.
- A maintenance policy may cover a major line, one or more minor lines, or a short-lived exact patch branch within either artifact family.
- A release fact is established only after the release commit, Git tag, build result, and artifact publication have been verified.
- Never reuse or move a published release tag.

Fixes affecting multiple supported minor lines must be applied and tested on every affected branch. A fix affecting both standard and Legacy artifacts must be adapted and tested separately. Do not merge one maintenance branch wholesale into another.

## Branch version policy

| Branch or ref | Version policy | Example |
|---|---|---|
| `main` | Exact non-SNAPSHOT version of the latest completed standard release | After releasing `5.0.0`, `main` remains `5.0.0` |
| `develop` | Next planned minor or major `-SNAPSHOT` | After releasing `5.0.0`, use `5.1.0-SNAPSHOT` |
| Short-lived `feature/*` | Inherit the version from `develop` | A feature for the next minor uses `5.1.0-SNAPSHOT` |
| Optional `release/{version}` | Exact target release version during stabilization | `release/5.1.0` uses `5.1.0` |
| `maintenance/{major}.x` | Snapshot within the selected standard major | `maintenance/5.x` accepts `5.y.z-SNAPSHOT` |
| `maintenance/{major}.{minor}.x` | Next standard patch `-SNAPSHOT` | `maintenance/5.0.x` uses `5.0.1-SNAPSHOT` after `5.0.0` |
| `maintenance/{major}.{minor}.{patch}` | Exact short-lived standard patch `-SNAPSHOT` | `maintenance/5.0.1` uses `5.0.1-SNAPSHOT` |
| `maintenance/legacy/{major}.x` | Snapshot within the selected Legacy major | `maintenance/legacy/4.x` accepts `4.y.z-SNAPSHOT` |
| `maintenance/legacy/{major}.{minor}.x` | Next Legacy patch `-SNAPSHOT` | `maintenance/legacy/4.0.x` uses `4.0.2-SNAPSHOT` after `4.0.1` |
| `maintenance/legacy/{major}.{minor}.{patch}` | Exact short-lived Legacy patch `-SNAPSHOT` | `maintenance/legacy/4.0.2` uses `4.0.2-SNAPSHOT` |
| Release commit and immutable tag | Exact non-SNAPSHOT release version | Standard `v5.0.0`; Legacy `legacy-v4.0.1` |

After a standard release, keep `main` on the released non-SNAPSHOT version and advance `develop` to the next `-SNAPSHOT`. After a maintenance release, advance the continuing maintenance branch to the next patch `-SNAPSHOT` or delete an exact short-lived patch branch.

## CI release and snapshot policy

The workflow in `.github/workflows/release.yml` delegates deployment to `unit-corp/ci-templates-public/.github/workflows/trigger-maven-central-release.yml@main`.

Snapshot triggers cover:

```text
develop
maintenance/[0-9]+.x
maintenance/[0-9]+.[0-9]+.x
maintenance/[0-9]+.[0-9]+.[0-9]+
maintenance/legacy/[0-9]+.x
maintenance/legacy/[0-9]+.[0-9]+.x
maintenance/legacy/[0-9]+.[0-9]+.[0-9]+
```

`main` is intentionally excluded. The shared script also requires a `-SNAPSHOT` Maven version for branch deployment.

Configure these GitHub Actions Organization or Repository Variables:

- `TAG_PREFIXES`, for example `legacy-v v`
- `MAVEN_SNAPSHOT_URL`
- optional `ALLOWED_BRANCH_PATTERNS`; when configured, the shared script requires a branch to match at least one whitespace-separated regular expression

Repository Variables override Organization Variables with the same name.

## Preconditions

1. Confirm the release branch, artifact family, lifecycle eligibility, and working-tree scope.
2. Confirm the intended version does not already exist as a Git tag or published artifact.
3. Review all release, compatibility, migration, security, and Maven metadata.
4. Confirm the Spring Data Mirage version, Mirage-SQL dependency version, artifact family, and Java packages agree.
5. Confirm JDK, Maven, signing key, runner, and repository credentials in the approved release environment.

## Build and verification

```bash
mvn clean verify
```

Inspect the generated JAR, sources JAR, Javadoc JAR, POM, `META-INF/LICENSE`, `META-INF/NOTICE`, and Git properties. Confirm dependency scopes and ensure no mixed Legacy and standard artifacts are present.

Validate repository scanning, SQL-resource resolution, CRUD, paging, chunking, modifying queries, transactions, auditing, and supported database integrations.

## Version and changelog

Set the exact non-SNAPSHOT release version and the matching Mirage-SQL dependency version. Create the versioned changelog section using only changes verified from source, pull requests, Git history, and test evidence.

Before treating a version as released, verify that its release commit, immutable tag, publication result, and changelog entry agree.

## Tag and post-release verification

Use `v{version}` for standard releases and `legacy-v{version}` for Legacy releases. Create a tag only for the immutable release commit.

After publication:

1. Resolve the artifact from a clean environment.
2. Verify signatures and checksums where provided.
3. Verify the tag points to the reviewed release commit.
4. Confirm documentation links and examples.
5. Announce only the capabilities and compatibility actually verified.

If publication partially fails, do not overwrite an artifact accepted by the repository. Determine which coordinates are immutable and use a new version when required.
