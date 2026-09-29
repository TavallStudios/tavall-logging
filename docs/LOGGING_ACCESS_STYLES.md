# Tavall Logging Access Styles

## Purpose

This guide ranks how consumer code writes operational log output with `tavall-logging`, and says what each entry point is for.

`Log` is a static, stateless entry point. It is a focused utility in the tavall-docs sense (`NAMESPACES_VARIABLES_AND_OOP.md`, `Util`): consumers call it directly and do not register, wrap, or inject it.

The core rule:

> Log once, at the boundary that decides what a failure means, with a message that names what failed and what the code does next.

---

## The Contract

| Entry point | Output | Use |
| --- | --- | --- |
| `Log.info(String \| LogText)` | `[INFO]`, white | Lifecycle milestones and one-time configuration facts. |
| `Log.success(String \| LogText)` | `[SUCCESS]`, green | Completion of a startup or long-running step. |
| `Log.warn(String \| LogText)` | `[WARN]`, yellow | Expected degradation handled by a fallback (fail-closed state, local mode, unavailable integration). |
| `Log.error(String \| LogText)` | `[ERROR]`, red | A failure the current operation could not handle. |
| `Log.critical(String \| LogText)` | `[CRITICAL]`, red background | The process or a subsystem cannot continue. |
| `Log.exception(Throwable)` | `[ERROR]` lines: exception, first application frame, cause chain, up to 8 application frames | Unexpected exceptions where the stack is needed to diagnose. |
| `Log.text()` / `LogText.create()` / `LogText.of(...)` | A styled message builder | Multi-part messages with `LogColor` segments. |

Behavior consumers must know:

- Output is **asynchronous**: messages are queued and printed to standard output by a daemon thread (`LogThread`). Lines still queued when the JVM halts can be lost, so logging is not an audit or durability mechanism.
- `%NAME%` placeholders (`%RED%`, `%BOLD%`, `%RESET%`, ...) in a message are replaced with ANSI codes; unknown placeholders are left as written.
- `Log.exception` treats frames from `org.tavall.` (and `com.tjxnjoobie`) packages as application frames; when none exist it prints the first five frames.

---

## Production Ranking Summary

| Rank | Style | Production use |
| --- | --- | --- |
| 1 | One `Log.warn`/`Log.error` line at the boundary that turns a failure into a typed fallback | Default for handled failures |
| 2 | `Log.exception(t)` at the boundary for unexpected failures | When the stack is needed |
| 3 | `Log.info`/`Log.success` for lifecycle milestones | Startup, mode selection, shutdown |
| 4 | `LogText` styled messages | Multi-part console output |
| — | Logging at every layer, logging secrets, wrapper loggers, logging as audit | Not allowed |

---

# Style 1: Log at the Fallback Boundary

## Production Rank

First.

## Shape

```java
public AdminAccountsMetaData loadAdminAccountsMetaData(AdminAccountsLookupRequest lookup) {
    try {
        ...
        return new AdminAccountsMetaDataBuilder().buildAdminAccountsMetaData(...);
    } catch (RuntimeException exception) {
        Log.warn("Account-link authority failed while loading Account Tools: " + exception.getMessage());
        return AdminAccountsMetaData.unavailable(UNAVAILABLE);
    }
}
```

## Why It Wins

- The class that chooses the fallback is the one that knows the consequence, so its message can say both what failed and what happens next.
- The typed fallback (`unavailable`, `AUTHORITY_UNAVAILABLE`, an empty result) carries the outcome to callers; the log line is for operators, not control flow.
- `warn` marks a handled, expected degradation; `error` marks an unhandled one.

## Rules

- Log once. Lower layers throw or return typed results; they do not also log the same failure.
- Include the exception message, not the full stack, when the failure mode is known.
- Never log credentials, tokens, session ids, or personal data.

---

# Style 2: `Log.exception` for Unexpected Failures

## Production Rank

Second.

```java
} catch (RuntimeException exception) {
    Log.exception(exception);
    if (requirePostgres) {
        throw new IllegalStateException("Postgres storage is required but could not be initialized.", exception);
    }
    return Optional.empty();
}
```

Use it where the cause is not known in advance and the application frames and cause chain are needed. Do not follow it with a second `Log.error` repeating the same failure.

---

# Style 3: Lifecycle Milestones

## Production Rank

Third.

`Log.info` states one-time facts (selected mode, bound port, configured provider); `Log.success` marks a completed startup step. A mode that weakens guarantees (for example a seeded local directory instead of durable storage) is a `warn`, not an `info`:

```java
Log.warn("Tavall Web organization authority is using the seeded local directory (local provider mode).");
```

---

# Style 4: Styled Messages

## Production Rank

Fourth; console presentation only.

```java
Log.info(Log.text()
        .append(LogColor.BOLD, "Cloud")
        .append(" operations available: ")
        .append(LogColor.GREEN, String.valueOf(count)));
```

Prefer plain strings for anything that may be parsed or searched.

---

# Anti-Patterns

| Anti-pattern | Why it is rejected | Use instead |
| --- | --- | --- |
| Logging the same failure at every layer | Duplicated, misleading output | Log at the fallback boundary, Style 1 |
| A `*Logger`/`*LogManager` wrapper or DI-registered logger | Forwarding wrapper around a static utility (tavall-docs `CLASSES.md`) | Call `Log` directly |
| Logging instead of returning a typed failure | Callers cannot react to a log line | Typed result or exception, plus one log line |
| Relying on log output for audit or durable records | Output is asynchronous and can be lost at halt | The owning audit store |
| Logging secrets or personal data | Standard output is widely collected | Log identifiers that are safe to expose |

---

# Test Coverage Needed

Tests assert the typed fallback (result, status, or exception), not log text. Log output is not a test contract.
