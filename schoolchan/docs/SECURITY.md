# SchoolChan Security Notes

## Data minimization

- Anonymous posters receive an HttpOnly `sc_session` cookie; only a SHA-256 hash is used server-side for votes and reports.
- Staff-only `poster_metadata` stores IP and session hash encrypted with AES-GCM (`schoolchan.metadata-key`). Retention job purges rows after `metadata-retention-days`.
- Public HTML does not include the session token, poster IP, or staff password. The escalation text download also omits IP and session; those appear only after an audited reveal.

## Authentication

- Public read/post (when access windows allow writes) requires no login.
- `/staff/**` uses form login, BCrypt password hashes, CSRF on HTML forms; `/staff/api/**` is CSRF-exempt for JSON eval tools (staff-authenticated only).

## Headers

`SecurityHeadersFilter` sets:

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `Referrer-Policy: no-referrer`
- Baseline `Content-Security-Policy`

## Moderation safety

- Seed and test harmful triggers use `TOKEN_*` placeholders only.
- ML sidecar defaults to `MOCK_MODE=true` (`scripts/run-ml.ps1`). `scripts/run-ml-real.ps1` loads ToxiScan and NLLB and falls back to the mock if a weight file fails to load.
- High-confidence auto-block; medium → human review; escalation → restricted queue (no auto-contact of authorities).

## Operations

- Rotate `SCHOOLCHAN_ADMIN_PASSWORD`, `SCHOOLCHAN_METADATA_KEY`, and database credentials before production.
- Run behind HTTPS so session cookies can use the `Secure` flag.
- Dependency scanning: add OWASP dependency-check or `gradle dependencyCheckAnalyze` before deploy.
