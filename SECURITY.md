# Security Policy

## Supported versions

| Version line | Support level |
|---|---|
| `5.x` standard artifacts | Supported major line. Only released versions are eligible for production support |
| `4.x` Legacy artifacts | Maintenance-only. Only released versions are eligible for critical security and compatibility fixes at maintainer discretion |
| `3.x` and earlier | Internal support only. Not part of the public support policy |

Maintenance-only does not guarantee a fix for every finding or a fixed response time. Enterprise users requiring an SLA should establish a separate commercial support agreement.

The project may support one minor line or multiple minor lines within either artifact family. The applicable release announcement or maintained compatibility matrix must identify the exact supported minors and artifact family. A branch name or existing tag alone is not evidence that a line remains supported.

## Reporting a vulnerability

Do not open a public issue for a suspected vulnerability.

Report it privately to `devops@unit.com.vn`. Include:

- affected version, artifact, and commit if known
- deployment context and relevant Java, Spring, Spring Data, Mirage-SQL, database, and JDBC-driver versions
- impact and realistic attack scenario
- reproduction steps or a minimal test case
- proposed mitigation if available
- whether the issue is already public or has a disclosure deadline

Do not include production credentials, customer data, access tokens, or other secrets. Use sanitized evidence.

Maintainers should acknowledge the report, validate impact, agree on disclosure coordination, and identify supported release lines before publishing details. Exact response and remediation timelines depend on severity, reproducibility, affected versions, and available maintainer capacity unless governed by a separate agreement.

## Scope

Security reports may cover repository proxy generation, SQL-resource resolution, query parameter binding, reflection, expression evaluation, transaction integration, dependency exposure attributable to the project, and supported integrations.

Unsafe application SQL templates, database permissions, secret management, network controls, and unsupported dependency overrides may require application-side remediation even when Spring Data Mirage is involved in the execution path.
