# tavall-logging

A Java logging helper with leveled messages, exception output, and styled text.

The single-module library exposes Tavall's Log methods and LogText/LogColors helpers for formatted application output.

## Why tavall-logging

- Small static entry points for success, info, warning, error, and critical messages.
- Supports plain strings and styled LogText values.
- Includes an exception logging entry point.

## Features

- Log.success / info / warn / error / critical
- Log.exception(Throwable)
- LogText builder entry point and color/style helpers

## Quick Start

Add the published artifact to a Gradle project:

```kotlin
dependencies {
    implementation("org.tavall:tavall-logging:<version>")
}
```

Use the exact published version and repository access configured for your project. See the links below for API and contribution details.

## Project Structure

This repository is a single Java library module (Module Type: LIBRARY; Runtime: None).

## Documentation

| Document | Purpose |
| --- | --- |
| [Contributing](CONTRIBUTING.md) | Contribution and development notes. |
| [Repository Git Workflow](docs/quality/GIT_WORKFLOW.md) | Applicable repository guidance. |

## Requirements / Compatibility

Java 25.

## Building From Source

```bash
./gradlew check
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Source headers refer to the TJVD License and LICENSE.TXT, but no tracked LICENSE.TXT appears in the current repository tree. Confirm the applicable license with the repository owner before reuse.

## Documentation Update State

<details>
<summary>Documentation Update State</summary>

### Current Locations

| Surface | Sync State | Location | Last Updated | Evidence |
| --- | --- | --- | --- | --- |
| GitHub | PRIMARY | TavallStudios/tavall-logging/README.md | 2026-09-27 12:29 PM PDT | Migration PR. |
| Notion | NOT_APPLICABLE | — | 2026-09-27 12:29 PM PDT | README files are not synchronized as Notion twins. |

### Update History

| Timestamp | Surface | Event | Location | Previous Location | Evidence | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-09-27 12:29 PM PDT | GitHub | UPDATED | TavallStudios/tavall-logging/README.md | Same path | Migration PR. | Reworked the public README to describe the current project, module boundary, usage, and documentation. |

</details>
