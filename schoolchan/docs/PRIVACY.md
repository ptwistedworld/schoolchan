# Privacy (RA 10173 design notes)

> Design-level measures only — not a substitute for the school's legal review.

## What we store

| Data | Purpose | Retention |
| --- | --- | --- |
| Post text (original + optional translation) | Board content, moderation | Permanent while thread archived |
| Media files (after screening) | Board content | Permanent unless blocked (then deleted; hash kept) |
| Vote/report session hashes | De-duplication, reputation | With related records; reputation decay |
| Encrypted IP + session metadata | Abuse investigation / escalation only | Default 90 days; longer if open escalation |
| Staff accounts | Moderator/admin auth | Until disabled |
| Audit log | Accountability | Append-only, long retention |

## What we do not do

- No student registration or profiles
- No public display of IP, session ID, or poster identifiers
- No automatic contact of authorities (human escalation package only)
- No logging of post body or IPs at INFO for ordinary operations

## User-facing disclosure

Key points appear on `/rules`: acceptable use, automated + human moderation, escalation pathway (post + IP may be forwarded for genuine safety risks), data retention, and moderator conduct.
