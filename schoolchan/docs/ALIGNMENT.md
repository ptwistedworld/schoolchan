# Capstone Alignment Check

Mapped against the 7 research objectives in the Master Prompt.

## Objective 1 — Anonymous, account-free board
- ✅ No registration; all posts labeled "Anonymous".
- ✅ Anonymous session cookie (salted hash) for votes/rate limits.
- ⚠️ Anonymity audit test exists but limited; htmx not yet used.

## Objective 2 — Non-ephemeral threads, bump, archive
- ✅ Bump on reply, sage, bump limit → sinking, active cap → archive.
- ✅ Archived threads read-only, browsable.
- ⚠️ Pagination on archive not implemented.

## Objective 3 — Layered moderation pipeline
- ✅ Keyword/blacklist, link screening, ML text (mock/real), image NSFW (mock/real), escalation.
- ✅ Decision engine with thresholds from config.
- ✅ Per-stage + total latency recorded.
- ✅ The board never removes a post on its own: everything is published, and the filter, a
      student, or a moderator flags it for the staff review queue (ADR-014).
- ⚠️ Video frame sampling requires ffmpeg (documented).
- ⚠️ Degraded mode (ML down → publish unflagged, and the dashboard says so) works.

## Objective 4 — Automatic translation
- ✅ Original preserved, translated text stored, toggle in UI.
- ✅ Caching by content hash (added).
- ⚠️ Async fallback (publish then attach) not yet implemented.

## Objective 5 — Spoiler tagging
- ✅ Checkbox + [spoiler] parser, blurred rendering.
- ⚠️ DOM leakage test not yet added.

## Objective 6 — Access windows
- ✅ Windows table, read-only/unavailable modes, server-side enforcement.
- ⚠️ Admin override with expiry not implemented.

## Objective 7 — Rules, escalation, privacy
- ✅ Rules page, escalation queue, audit log, metadata encryption, retention job.
- ✅ Escalation package text export.
- ⚠️ PDF export (print view) not yet.
- ⚠️ Staff management UI not yet.

## Gaps to close (not straying)
1. Implement htmx interactivity (quote hover preview, translation toggle).
2. Add pagination, archive search.
3. Admin override, rules versioning UI, staff management.
4. Async translation fallback.
5. PDF/print escalation package.
6. Evaluation harness run + McNemar cross-check.
7. Security hardening pass (OWASP, headers, dependency scan).

Conclusion: Implementation aligns with the capstone scope. Remaining work is within the documented Parts 9–12. No scope drift detected.