# SchoolChan — Proposals: Fix, Improve, Innovate

Grouped by effort. **S** = small (hours) · **M** = medium (1–2 days) · **L** = large (week+).

---

## Policy change (2026-10-22) — publish, then flag

The school decided that **the board must never remove a post on its own**. Everything a
student writes is published; if the filter, another student, or a moderator thinks it
breaks the rules, it is flagged and waits in the staff review queue, where a person
decides. See [ADR-014](DECISIONS.md) for the full decision.

What this changes in the codebase, and what it opens up:

| # | Item | Size | Notes |
|---|---|---|---|
| P1 | **Flagged posts need a triage order** | S | The queue now mixes filter flags, student reports and moderator flags. Sort by severity (max stage score) or by flag source. |
| P2 | **A student who is reported a lot** | M | Reports no longer hide a post, so a determined harasser can stay up longer. Track report counts per session and escalate them to staff. |
| P3 | **"Leave it up" should say why** | S | When staff clear a flag, record the reason on the post so the history shows what was decided. |
| P4 | **Poster-facing appeals need a queue** | S | Appeals now start from a removal. Staff should see the appeal next to the post it belongs to, not only on the appeals page. |
| P5 | **Escalation visibility** | S | `hide-escalations` is a setting; the school should confirm which way they want it. It is on (hidden) today. |
| P6 | **A student is never told about a flag** | — | Decided: the flag is a staff-side thing. A published post that says "a moderator is looking at this" feels pending, and it lets a writer probe which rule they are near. The writer finds out when a moderator acts. |

---

## 0. Still missing — audited 2026-10-21

### Real gaps that need code (not just polish)

| # | Gap | Size | Notes |
|---|---|---|---|
| 0.1 | **Login rate limiting is configured but not implemented** | S | `students.max-failed-attempts` / `lockout-minutes` exist in config but nothing counts failures. A student ID can be brute-forced. **Highest priority.** |
| 0.2 | **Students cannot change their own password** | S | `must-change-password` is set on provisioning but there is no change-password page for students |
| 0.3 | **No "forgot password" / staff-assisted reset flow for students** | M | Staff can reset, but the student has to ask |
| 0.4 | **Postgres profile has never been run** | M | Everything is verified on H2. `docker compose up` + the dev profile is untested, so Flyway migrations are only proven on H2 |
| 0.5 | **No CI** | M | A GitHub Actions workflow running `make test` would protect every change |
| 0.6 | **No Dockerfile for the backend** | S | Only the sidecar has one |
| 0.7 | **Real ML models are never downloaded or verified** | L | ToxiScan / NLLB / OpenNSFW are configured but the mock is what actually runs. The paper needs a run with the real weights |
| 0.8 | **Evaluation only uses a 4-line placeholder fixture** | M | Chapter 4 needs a real labelled dataset (hate speech, profanity) and an accuracy-per-language table |
| 0.9 | **No structured JSON logging** | S | Log lines are plain text; the paper's "record the reason and scores" story is stronger with structured logs |
| 0.10 | **No appeal path for a held post** | M | A student whose post is held has no way to ask a moderator to look again |
| 0.11 | **Moderator accountability view** | M | The audit log exists but there is no per-moderator summary for the "moderators are themselves reviewable" requirement |
| 0.12 | **No per-board moderation rules** | M | One keyword list for every board; a school may want stricter rules in some places |

### Feature gaps worth doing

| # | Gap | Size | Notes |
|---|---|---|---|
| 0.13 | **No "OP" post / thread bump for the thread owner** | M | A 4chan staple: the original poster can bump their own thread |
| 0.14 | **No author delete** | S | Students can edit for 10 minutes but not delete |
| 0.15 | **No image gallery per thread** | M | 4chan's thread-level image view |
| 0.16 | **No quote-by-selection** | S | Select text, click quote, it becomes `>>123` |
| 0.17 | **No "watch thread" with an unread counter** | M | Bookmarks exist but there is no per-thread unread badge |
| 0.18 | **No school-specific report reasons** | S | Bullying, cheating, harassment of staff, unsafe content |
| 0.19 | **No moderator actions from the thread page** | S | Lock/pin/block live on `/staff/threads` and `/staff/review`, not next to the post |
| 0.20 | **Search filters are thin** | S | Board filter exists; add date range, "has image", "has replies" |
| 0.21 | **No "who is online" / active-now count** | S | Cheap 4chan feel, useful on a school board |
| 0.22 | **No upload preview before submitting** | S | Pick a file, see it, then post |
| 0.23 | **Image moderation is mock-only** | L | Needs the real NSFW model plus a phash blocklist demo |
| 0.24 | **No mobile bottom-sheet composer** | S | The reply box is inline; a sheet is nicer on a phone |
| 0.25 | **No automated accessibility audit** | M | Needed for the ISO 25010 usability evidence |

### Ideas we could innovate on

| # | Idea | Size | Value |
|---|---|---|---|
| 0.26 | **"First time here?" guided post** — a one-off helper panel with formatting tips | S | Onboarding for new students |
| 0.27 | **Automatic thread title from the first post** | S | Less typing, tidier board |
| 0.28 | **Duplicate detection** — warn a poster they are about to repeat a thread | M | Reduces accidental spam, useful with the report system |
| 0.29 | **Trusted-poster tier by community reports** — anonymous, capped, no identity | L | The Slashdot model done safely; the paper's originality |
| 0.30 | **Per-language moderation confidence display** for staff | M | Shows where the model is weak (Cebuano) — honest and paper-friendly |
| 0.31 | **Weekly board digest** — what got read, voted and removed | M | Gives students a sense of a living board |
| 0.32 | **Offline read-only cache of the boards you visit** | L | PWA, works on weak school Wi-Fi |
| 0.33 | **Escalation "what happens next" card for staff** | S | Makes the safety pathway less scary to run |
| 0.34 | **One-click "I saw this" for staff** on escalations | S | Lets the school track that a real case was looked at |

### Recommended order (highest value first)

1. ~~**0.1** login rate limiting~~ — **done**
2. ~~**0.2 / 0.3** student password change and reset codes~~ — **done**
3. ~~**0.5** CI~~ — **done** (add a real deployment target when the school has one)
4. ~~**0.4** Postgres profile verification~~ — the CI job runs it; run `make verify-postgres` locally with Docker
5. ~~**0.8** real evaluation dataset~~ — **done** (312 rows, real models, p = 7.45e-9)
6. ~~**0.7 / 0.23** real ML models~~ — **done** (ToxiScan + OpenNSFW2 verified)
7. ~~**0.10 / 0.11** appeals and moderator accountability~~ — **done**
8. ~~**0.13 / 0.15 / 0.16** the 4chan feature set~~ — **done**
9. ~~**0.29** the trusted-poster tier~~ — **done**
10. ~~**0.32** offline PWA~~ — **done**
11. **Load the NLLB translation model** and re-run the evaluation so Filipino and Cebuano text is screened through its translation. This is the last open item for the paper's claims.
12. **Per-board escalation policy** — which categories escalate in which board.
13. **Automatic image thumbnails** so the catalog loads faster.
14. **A school-managed announcement board** with staff posting and expiry.

---

## A. Fix / Bugs (correctness first)

| # | Item | Size | Why |
|---|---|---|---|
| A1 | **Media moderation when sidecar is down** — an image post is held forever with `ML_UNAVAILABLE` and the poster sees nothing | S | Held posts should show a "queued for review" state to the poster and a staff shortcut |
| A2 | **Board list images not on page 1** — newest threads sort first, so seeded images land on page 2 | S | Sort by bump but show a "recent" tab |
| A3 | ~~Search misses thread subjects~~ **FIXED** — search now covers subject + post text | Done | — |
| A4 | **Post number gaps** — held/blocked posts consume numbers, so `>>107` may not exist | M | Allocate numbers only on publish, or show a tombstone |
| A5 | **Vote/report double-submit** | S | Disable button after submit; already partly handled |
| A6 | **Image limit not surfaced** — user hits the limit and only sees a generic error | S | Show "3/150 images used" in the composer |
| A7 | **Session cookie rotation** — bookmarks/session lost on cookie expiry | S | Longer-lived session, explicit reset button |

### Already fixed this round
- Image upload crashed with `NoClassDefFoundError: org/reactivestreams/Publisher` → added the `reactive-streams` dependency.
- Image moderation always failed with HTTP 422 → the JDK HTTP client tried an **h2c upgrade** the sidecar could not parse, so the multipart body arrived empty. Fixed by forcing HTTP/1.1 and sending an explicit filename.
- Vote/report gave no feedback → notices now shown ("Vote counted.", "Report sent to the moderation team.").
- Redirects with an **absolute** URL produced a mangled path → all redirects are now relative and sanitised.
- Reply box was at the top of the thread → moved to the bottom (4chan style) with a "Skip to reply" link.
- Theme button did nothing on pages without `board.js` → the script is now loaded on every page.
- "Show English" was invisible → now a compact `EN` button, only on posts that really have a translation.
- Bookmarks were client-side and lost on page change → now server-side, tied to the anonymous session.
- **A4** post-number gaps → held/blocked posts now render as a **tombstone** so `>>N` always resolves.
- **A1** posters could not see their held post → new **/my-posts** page (session-scoped).
- **D5** multilingual normalisation → new normaliser folds leetspeak, spaced letters, repeated letters, accents, and Cyrillic/Greek homoglyphs, per line and per token, with Tagalog/Cebuano starter lists in `data/blacklists/`.
- One-click launcher: `start-schoolchan.bat` (close the window to stop) and `stop-schoolchan.bat`.
- **A2 / 0.3** student password change, plus one-time reset codes that staff issue and the student uses.
- **0.1** login rate limiting was configured but never implemented → 5 failures locks for 10 minutes, and the lock refuses the correct password too.
- **0.7 / 0.23** the real models now **run**: ToxiScan (Apache-2.0) and OpenNSFW2 through `onnxruntime`, no PyTorch. Verified scores. The old ADR-003 model id did not exist and was corrected.
- **0.8** the evaluation fixture grew to 312 labelled rows across three languages, the harness runs the **real pipeline**, and the result shows a significant McNemar improvement (p = 7.45e-9) with a per-language table.
- **0.5** CI workflow (backend, sidecar, and a PostgreSQL migration job).
- **0.6** backend Dockerfile.
- **0.9** structured logging (JSON profile) and a fixed logback pattern.
- **0.10** appeals: a student can ask for a held post to be looked at again; staff approve from `/staff/appeals`.
- **0.11** moderator accountability view: every staff action grouped by person.
- **0.12** per-board moderation rules, managed at `/staff/board-rules`.
- **0.13** the OP can bump their own thread.
- **0.14** the author can delete their own post.
- **0.15** per-thread image gallery.
- **0.16** quote-by-selection.
- **0.17** watch a thread and see an unread count.
- **0.18** school-specific report reasons.
- **0.20** search filters: board, date, has-images, minimum replies.
- **0.21** "N here right now" on the home page.
- **0.22** upload preview before posting.
- **0.24** the composer becomes a bottom sheet on a phone.
- **0.25** an automated accessibility audit test (labels, alt text, language, leaks).
- **0.26** a first-time-poster guide.
- **0.27** automatic thread titles.
- **0.28** duplicate-post warning.
- **0.29** anonymous reporter tiers, capped so no session dominates.
- **0.30** per-language moderation confidence for staff.
- **0.31** a weekly board digest.
- **0.32** offline PWA with a service worker and an offline page.
- **0.33** "what happens next" on each escalation.
- **0.34** "I have seen this case" for staff.
- Database backup script (`backup-db.bat`) so a bad run cannot lose your data.
- **A1** the poster can see their own pending posts on `/my-posts`, with the reason spelled out: *"the automatic filter could not be reached, so every new post waits for a person. It is not because of what you wrote."*
- **Author delete** → 15 minute window, and the tombstone says *"deleted by the person who wrote it"*, never *"removed by moderation"*.
- **Staff view** → the review queue has an **"Every screened post"** view, so a post the filter flagged but published is never invisible.
- **Staff dashboard** warns when the moderation service is unreachable, which is the usual reason everything is queued.
- **Access windows can be deleted** from the settings page.
- The appeal box is a real textarea now, not a one-line input.

---

## B. Improve (4chan feel + polish)

| # | Item | Size | Notes |
|---|---|---|---|
| B1 | ~~**Inline reply per post**~~ **DONE** — a "Reply" button under each post opens a small box prefilled with `>>No.` | Done | Biggest 4chan-feel win |
| B2 | ~~**Post hover preview** in the board list~~ **DONE** — hover "preview" for 250 ms | Done | — |
| B3 | ~~**Keyboard shortcuts**~~ **DONE** — `j`/`k` next/prev, `r` reply, `b` board, `t` top, `e` bottom | Done | — |
| B4 | ~~**"Back to top" / "bottom" floating button**~~ **DONE** | Done | — |
| B5 | ~~**Sticky OP** while scrolling~~ **DONE** | Done | — |
| B6 | **Reply count + last reply time in the board list** | Done | — |
| B7 | ~~**Image grid mode**~~ **DONE** — catalog cards now show a thumbnail | Done | — |
| B8 | ~~**Spoiler polish**~~ **DONE** — "Reveal all spoilers" toggle + smooth blur transition | Done | — |
| B9 | ~~**Dark/light theme**~~ **DONE** — round `☀ / ☾` toggle button, soft non-beige light palette | Done | — |
| B10 | ~~**Mobile thread view**~~ **DONE** — sticky "↓ Reply to this thread" bar on phone widths | Done | — |
| B11 | ~~**Post edit window**~~ **DONE** — author-only, 10 minutes, re-screened after edit | Done | — |
| B12 | ~~**Draft autosave**~~ **DONE** — per-composer localStorage draft with a restore note | Done | — |
| B13 | ~~**Quoting toolbar**~~ **DONE** — "quote last" / "greentext" buttons insert at the cursor | Done | — |
| B14 | ~~**Read markers**~~ **DONE** — unread posts get an accent border until you scroll past them | Done | — |
| B15 | ~~**Endless scroll**~~ **DONE** — toggleable on the board list, remembered per browser | Done | — |
| B16 | ~~**Logo**~~ **DONE** — blocky "S" mark (also reads as three stacked posts) + favicon | Done | — |

---

## C. Staff & Moderation

| # | Item | Size | Notes |
|---|---|---|---|
| C1 | **Bulk review actions** (approve/reject selected) | Done | — |
| C2 | **Review queue filters** — by board, by reason, by stage score | M | — |
| C3 | ~~**Explainability card**~~ **DONE** — per-stage score, latency and evidence in the review queue | Done | Strong for the defense paper |
| C4 | **Escalation package PDF** (print view) | Done | — |
| C5 | ~~**Staff notes** on a thread~~ **DONE** — internal notes, staff-only, added inline from the thread page | Done | — |
| C6 | ~~**Mod queue age indicator**~~ **DONE** — oldest first, with a colour-coded "⏱ 2h 13m" age badge | Done | — |
| C7 | ~~**Audit log export (CSV)**~~ **DONE** — `/staff/audit.csv` | Done | — |
| C8 | ~~**Reputation dashboard**~~ **DONE** — anonymised reliability stats on the staff dashboard | Done | Paper requirement |
| C9 | ~~**Blacklist management UI**~~ **DONE** — add / disable / delete, CSV import & export, and a **test-a-string** tool that shows the normalisation and every match | Done | Master Prompt requirement |
| C10 | ~~**Similarity tuning**~~ **DONE** — adjustable threshold in staff settings + a list of confirmed training examples | Done | — |
| C11 | ~~**Reports appear in the review queue**~~ **DONE** — reported posts are queued with a report badge | Done | — |
| C12 | ~~**Per-stage latency dashboard**~~ **DONE** — mean / p95 / max per stage on the staff dashboard | Done | ISO 25010 evidence |
| C13 | ~~**Review queue filters**~~ **DONE** — All / Held only / Reported only + reason filter | Done | — |
| C14 | ~~**Staff view on the public board**~~ **DONE** — staff ribbon + inline block / publish / note per post | Done | — |
| C15 | ~~**Escalation rehearsal mode**~~ **DONE** — dry-run toggle: banner, synthetic placeholder, and a package export with no real post data | Done | Safe demo of the safety pathway |

---

## D. Innovation (paper differentiators)

| # | Item | Size | Value |
|---|---|---|---|
| D1 | **Explainable moderation card** for every decision (C3) | M | Core "hybrid pipeline" evidence for Chapter 4 |
| D2 | **Per-stage latency dashboard** — live p50/p95 per stage, rendered as a simple bar chart | M | Direct ISO 25010 performance-efficiency evidence |
| D3 | **Reputation-weighted, capped trust** shown as anonymous tiers (no identities) | M | Slashdot-style paper requirement, done safely |
| D4 | **Escalation rehearsal mode** — staff can run the workflow on synthetic data with a visible "DRY RUN" banner | M | Defensible, safe demo of the safety pathway |
| D5 | **Multilingual profanity normalisation** (Tagalog/Cebuano confusables + spaced letters) | L | Genuine research contribution for the paper |
| D6 | **Adaptive thresholds by time-of-day / board load** | L | Novel, needs careful false-positive analysis |
| D7 | **FAQ clustering** — detect repeated questions and surface a pinned answer | L | Cool + useful |
| D8 | **Privacy dashboard for students** — "what we store about you, and for how long" | M | RA 10173 transparency |
| D9 | **Offline read-only PWA** — cache recent threads | L | Nice-to-have |
| D10 | **Accessibility CI badge** — automated a11y checks in the test run | M | ISO 25010 usability evidence |

---

## E. Recommended next sprint (ordered)

1. ~~**B1** inline reply~~ — **done**
2. ~~**B3 + B4** keyboard shortcuts and back-to-top~~ — **done**
3. ~~**A1** "queued for review" state for the poster~~ — **done** (`/my-posts`)
4. ~~**C3/D1** explainability card~~ — **done**
5. ~~**D2** latency dashboard~~ — **done**
6. ~~**A4** post-number gaps~~ — **done** (tombstones)
7. ~~**D5** multilingual normalisation~~ — **done**
8. ~~**B10** mobile sticky reply bar~~ — **done**
9. ~~**C2** review queue filters~~ — **done**
10. ~~**C5** staff notes~~ — **done**
11. ~~**B11** post edit window~~ — **done**
12. ~~**B14** read markers~~ — **done**
13. ~~**B15** endless scroll~~ — **done**
14. ~~**B16** logo + favicon~~ — **done** (schoolhouse mark)
15. ~~**C6** queue age indicator~~ — **done**
16. ~~**C9** blacklist management + test-a-string~~ — **done**
17. ~~**C10** similarity tuning~~ — **done**
18. ~~**D4** escalation rehearsal mode~~ — **done**
19. ~~**B8** spoiler polish~~ — **done**
20. **NSFW / media blur toggle** — done (client-side "Images: blurred")
21. **Student sign-in (optional gate)** — done: staff provisioning, student enrollment requests, role matrix, demo accounts
22. Remaining: moderation reason taxonomy, bulk audit-log export, per-language accuracy export to CSV, and the "paper" figure scripts.

---

## F. Quick wins (under an hour each)

- [x] Show image count in the composer (`3 / 150 images used`)
- [x] "Show English" button label → compact `EN`
- [x] Board list: show last reply time
- [x] Thread nav: post count, jump-to-reply link
- [x] Bookmarks, My posts, and Search linked in the header
- [x] Endless-scroll toggle on the board list
- [x] Staff ribbon + inline mod controls on public pages
- [x] `robots.txt` disallowing `/staff`
- [x] Media blur toggle (blur all images until clicked)
- [x] Three themes: light, dim (eye-friendly night), dark
- [ ] Add `aria-live` to the notice bar
- [ ] Add `/api/health` JSON for the sidecar, link it from the staff dashboard
- [ ] Seed 3 "pinned" demo threads for the catalog view

---

## G. 4chan grammar we have not taken yet (proposed 2026-10-23)

4chan's advantage is not its culture, it is six interaction ideas: **every post has
a stable number**, **quotes form a visible graph**, **long threads are compressed**,
**the reader controls what they see**, **the board feels live**, and **pictures are
cheap to open**. We already have numbers, quote links, hover previews, inline
replies, watch/unread counts, catalog, gallery, sage, bump limits, spoilers and
author delete. These are the pieces still missing.

| # | Idea | What the reader gets | Size |
|---|---|---|---|
| G1 | **Backlinks + `(You)` + landing highlight** | A post says "→ 149, 151" meaning those replies quoted it; your own posts carry a quiet `(You)`; following `>>N` flashes the target | M |
| G2 | **Reply preview limit** | OP plus the newest 60 replies, then "37 older replies omitted — show all". Long threads load instantly and still open fully | M |
| G3 | **Live thread** | "3 new posts — show" bar while you read. No auto-jump, no interruption of a half-written reply | L |
| G4 | **Reader filters + hide thread** | Per-browser keyword rules (hide / collapse / highlight) and a "hide this thread" × on board cards, with "N hidden — show" | M |
| G5 | **Catalog as a real index** | Sort by bump / newest / replies / images, "has pictures" filter, and two lines of OP text under each card | M |
| G6 | **Image reading controls** | Expand all / shrink all, remembered per browser; lazy loading with real dimensions; ←/→ in the lightbox | S |
| G7 | **Relative time + short links** | "12 min ago" with the exact time on hover; `/boards/general/p/149` opens the right thread at the right post | S |
| G8 | **"Quoted you" inbox** | A tab on `/my-posts`: every reply that quoted something you wrote, newest first (session-scoped, no account) | M |

**Status (2026-10-29):** G1, G2, G3, G4, G5, G6, G7 and G8 are built (Sprints 1–3,
ADR-024, ADR-025 and ADR-026). The finishing pass is Sprint 4 — see the sprint plan
at the end of `docs/ROADMAP.md`.

### Why each one is worth it

- **G1 (backlinks)** is the single biggest remaining 4chan feel. A conversation on
  an imageboard is a graph, not a list; right now a reader sees `>>14` but the
  quoted post never learns it was answered. It also gives the staff queue useful
  context ("this post is the root of a pile-on"). `myPostIds` already exists for
  the author tools, so `(You)` is nearly free.
- **G2 (omission)** is both the 4chan signature and the honest performance fix for
  300-post threads. The board already suggests 157 threads; long ones will come.
  Server-rendered `?all=1` keeps it working with JS off and keeps `#pN` anchors
  valid.
- **G3 (live)** turns a static page into a board. Polling a small fragment every
  20–30 s (paused in a hidden tab and while a composer has text) is enough; no
  WebSockets, no new infrastructure, and the "show" button means nothing moves
  under the reader.
- **G4 (filters)** is 4chan's most under-rated feature and is a *reader* tool, not
  a moderation tool: no server state, no report, no effect on what others see.
  That keeps it outside the moderation pipeline while giving students control
  over what they read.
- **G5 (catalog)** is how 4chan is actually browsed at speed; ours is a plain grid.
  Sorting and a "has images" filter are query parameters over data we already batch.
- **G6/G7** are the small things that make the board feel finished: pictures that
  do not jump the page, a lightbox you can walk through, times in human words,
  links short enough for a group chat.
- **G8** closes the loop opened by G1 without inventing accounts: your browser
  already knows which posts are yours.

### Scope and guardrails

- All of this sits inside Parts 0–12 (reader experience, no new moderation
  semantics). Nothing here changes what is published, held, blocked or escalated.
- Nothing here creates an identity. Backlinks and `(You)` are computed from the
  session cookie we already use; filters live in `localStorage`; the "quoted you"
  list is derived from `myPostIds`, never stored as a profile.
- Every list/limit stays configurable (`thread.reply-preview-limit`,
  `thread.live-poll-seconds`) and every new page keeps a server-rendered,
  JavaScript-free path.
- Each item gets an ADR, tests, and a line in this file when it ships.

### Suggested order

1. **G1** backlinks, `(You)`, landing highlight — one focused day, immediate payoff.
2. **G2** reply preview + "show all" — same day; they share the thread page work.
3. **G7 + G6** relative time, short links, expand/shrink, lazy images — half a day.
4. **G4** reader filters and hide-thread — a day.
5. **G5** catalog sort/filter/OP text — half a day.
6. **G8** "quoted you" — a day, after G1 settles.
7. **G3** live thread — two days; needs the post markup extracted into a fragment.

### What not to copy

- Per-thread poster IDs, country flags, tripcodes, name/email fields — all banned
  by the anonymity requirement, and the Master Prompt forbids poster IDs.
- Auto-deletion for inactivity — the board is explicitly non-ephemeral.
- "Sage" and the optional staff capcode already exist and stay as they are.
- Any feature that would let a crowd remove content. Flags stay with staff; the
  reader tools above only change what *that reader* sees.
