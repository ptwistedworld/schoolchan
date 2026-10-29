# SchoolChan Phased Roadmap

## Phase 0 — Stabilize & fix (current)
- [x] Honeypot + IP rate limiting
- [x] Active-thread cap counts sinking threads
- [x] Anonymity audit test expansion
- [x] Rich demo seed data (11 boards, 140+ threads, images, Tagalog/Cebuano)
- [x] Search endpoint + page (subject + post text)
- [x] Staff bulk publish/block
- [x] Audit log viewer + threads admin page
- [x] Fix StaffController missing List/UUID imports (bulk actions)
- [x] Ensure all templates render without 500s (About page route was missing)
- [x] Verify fresh seed + `make test` green
- [x] Student gate: anonymous users cannot act, students cannot reach /staff
- [x] Database backup script (`backup-db.bat`)

## Phase 1 — Backend enhancements
- [x] Real ML wiring (ToxiScan + Tagalog RoBERTa) with lazy load + health (scaffolded; batch endpoint added)
- [x] Batch classify endpoint + calibration script
- [x] Per-stage latency logging in moderation_result (already present)
- [x] NSFW image classifier (OpenNSFW ONNX) behind interface (scaffolded)
- [x] Translation caching by content hash + async fallback (caching done; async attach implemented)
- [x] Retention purge job dry-run + logging
- [x] Escalation package print/PDF view
- [x] Staff management (create/disable moderators)
- [ ] Reputation flag threshold tuning + UI (posts are now flagged, not auto-held)
- [ ] Learned-blacklist candidate approval (backend exists, polish)
- [ ] Similarity boost via embeddings

## Phase 2 — Frontend enhancements (minimal chan style)
- [x] Hover preview for >>quote links (vanilla JS, preview endpoint)
- [x] Mobile responsive pass (phone widths)
- [x] Pagination on board page
- [x] Catalog grid with thread cards + thumbnails
- [x] Spoiler blur animation + reveal
- [x] Pagination on archive page
- [x] Dark mode refinement (manual toggle + CSS variables)
- [x] Empty/loading states
- [x] Keyboard navigation + ARIA (focus styles, aria-labels)

## Phase 3 — Feature onboarding
- [x] Thread pin/lock UI buttons (staff) (threads page)
- [x] Search filters (by board)
- [x] Thread tags/flair (tag column + form)
- [x] Per-session bookmarks (client-side)
- [x] Report analytics dashboard (report count on dashboard)
- [ ] Post edit window (session-bound)
- [ ] Staff notes on threads
- [ ] Search filters by date/language

## Phase 4 — Evaluation & metrics
- [x] Run eval harness on fixture → accuracy/McNemar/latency
- [ ] Load probe (k6/Python) for ISO 25010
- [ ] Threshold sweep script
- [ ] Dataset loader guide + approved datasets
- [ ] Chart generation (PNG)

## Phase 5 — Security & privacy hardening
- [x] OWASP notes + docs/SECURITY.md updated
- [x] Dependency scan target documented
- [x] Automated anonymity audit (AnonymityPageTest)
- [ ] CSP refinement, rate-limit backoff

## Phase 6 — Optimization
- [x] DB indexes migration (V6)
- [x] Caching (translations by content hash)
- [ ] N+1 query elimination
- [ ] Static asset minification
- [ ] Baseline performance numbers recorded

## Iteration cadence
Each phase: implement → test → demo → review → next. Stop-and-report after each phase.

## Latest round (2026-10-22)
- [x] **A picture in a reply shows in the board preview**, at thumbnail size
- [x] Uploads are shrunk on arrival (1600 px) and a small preview copy is kept
- [x] Board page, catalog and endless scroll rebuilt around 3 queries instead of 60
- [x] Staff dashboard: 590 queries → 18; the review queue asks for flagged posts
      directly instead of reading every post in the database
- [x] New indexes: `media(post_id)`, `posts(thread_id, status, board_post_number)`,
      `moderation_results(created_at)`
- [x] The local profile now runs the real migrations and validates the schema;
      a fresh database was built from the migrations and checked
- [x] Nightly media maintenance: shrink what is too big, make missing preview
      copies, remove files no post points at (demo media: 55.7 MB → 2.1 MB)
- [x] The moderation log is trimmed on the retention schedule
- [x] **Fixed: posting from a browser sent an empty message.** The "clear the
      composer on submit" script emptied the textarea before the browser built
      the request body, so every post and reply arrived blank. The value is no
      longer touched on submit, and the server refuses an empty message
      (ADR-016).
- [x] **The staff review queue shows the whole post**: the text, the English
      translation, the board and thread with a link, and any attachment
      (ADR-017).
- [x] Staff-only route for attachments that are still in quarantine, audited
- [x] The trust-score flag now says what the score is, instead of "low trust score"
- [x] **Publish-then-flag policy** — the board no longer blocks or holds a post on its own.
      Everything is published; the filter, a student, or a moderator points it out to the
      staff queue (ADR-014). The poster is not told — only staff see the flag.
- [x] **Appeals start from a removal** — a student can ask a moderator to look again at a
      post that was removed or hidden for safety.
- [x] **"My posts" and "Bookmarks" hidden until a student signs in** (ADR-015); the URLs
      redirect to the sign-in page instead of showing an empty list.
- [x] Flag columns on `posts` (`flagged`, `flag_reason`, `flag_source`) + migration V13
- [x] Moderator "Flag" action; flags clear when a post is resolved
- [x] Review queue filters: hidden / flagged / reported, with a friendly source label
- [x] Moderation policy settings (admin only, persisted): auto-block off, safety hold on/off
- [x] "Restore the shipped default rules" button, and the rules text now describes the policy
- [x] One-time backfill so posts screened under the old policy carry the new flag
- [x] The seeder repairs a missing demo thread on the next start

## Latest round (2026-10-21)
- [x] Student sign-in gate (staff provisioning + student enrollment requests + role matrix)
- [x] Login throttling — 5 failures locks for 10 min, and the lock blocks the correct password
- [x] Student password change page
- [x] 11 boards and 140+ production-like threads with images, greentext, quotes and Tagalog/Cebuano
- [x] About page + rewritten friendly rules
- [x] Pagination hidden while infinite scroll is on
- [x] Database backup script (`backup-db.bat`)
- [ ] Remaining (see `docs/PROPOSALS.md` section 0): CI, Postgres profile verification, real ML
      weights, a real evaluation dataset, appeals, moderator accountability, and the 4chan
      feature set (OP bump, galleries, quote-by-selection, watch counts).

---

# Board grammar plan — the rest of the 4chan ideas (2026-10-23)

Goal: finish the reader experience without touching moderation policy or anonymity.
Every sprint ends with `gradlew test`, a browser pass at 390 px and 1280 px, and an ADR.
The proposal lives in `docs/PROPOSALS.md` section G.

## Sprint 1 — the conversation graph (done 2026-10-23)
- [x] **G1** backlinks (`→ 149, 151`), a quiet `(You)` on your own posts, and a flash on the
      post you jumped to
- [x] **G1b** land on your own post after creating a thread or replying (`#pN`)
- [x] **G2** reply preview limit (60) + `N replies omitted — show all` (`?all=true`)
- [x] **G2b** following a link to an omitted post reopens the thread with `?all=true#pN`
- [x] **G7** relative timestamps ("12 min ago") with the exact time on hover
- [x] **G7b** `#` permalink and `/boards/{slug}/p/{number}` route
- [x] **G6** lazy images with real dimensions, expand/shrink toggle, ←/→ in the lightbox
- [x] Tests: `QuoteIndexTest`, `ThreadPreviewTest`, `PermalinkTest`, `ReplyPreviewTest`
- [x] ADR-024

## Sprint 2 — reader control (done 2026-10-29)
- [x] **G4** per-browser filters: keyword rules (hide / collapse / highlight) and a hide-this-thread ×
      with "N hidden — show"; strongest match wins, localStorage only
- [x] **G5** catalog sort (bump / newest / replies / images), "has pictures", two-line OP excerpt
- [x] **G8** "Quoted you" tab on `/my-posts` (board-scoped, self-quotes and hidden replies excluded)
- [x] Tests: `CatalogOrderTest`, `QuoteInboxServiceTest`, `CatalogPageTest`, `MyPostsQuotesTest`;
      the localStorage filter engine is covered by the browser pass
- [x] ADR-025

## Sprint 3 — liveness (done 2026-10-29)
- [x] **G3** extract the post markup into `fragments/post.html`
- [x] **G3** `N new posts — show` polling, paused when the tab is hidden or a composer has text
- [x] **G3** `thread.live-poll-seconds` setting and a staff off switch
- [x] Tests: "posts after N", the poll endpoint, no duplicates when shown
- [x] ADR-026

## Sprint 4 — finish line
- [x] Cache headers for `/media` and static assets; image dimensions everywhere
- [x] `aria-live` on the new-posts bar; focus management after posting
- [x] Bump-limit banner ("this thread no longer bumps")
- [x] Empty states: no search results, no bookmarks, no quotes
- [x] Performance re-measure (board / catalog / thread), a11y sweep, full route smoke (key public/staff routes returned 200/302 as intended)
- [x] Docs: README, DEMO, PROPOSALS checkboxes; ADR-027 if needed
- [x] Final full test run (112 tests, 0 failures), coverage summary, release checklist

**Definition of done for the whole plan:** every box ticked, tests green, no console errors,
no 500s on the public + staff route sweep, mobile and desktop screenshots, docs current.