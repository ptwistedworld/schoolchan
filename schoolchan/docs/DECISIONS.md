# Architecture Decision Records

## ADR-001: Build tool — Gradle (Kotlin DSL)

- **Decision:** Use Gradle with Kotlin DSL and the Spring Boot plugin.
- **Alternatives:** Maven.
- **Why:** First-class Spring Boot support, faster incremental builds, wrapper for zero local install of Gradle itself.
- **Date:** 2026-10-20

## ADR-002: Text toxicity — ToxiScan + Tagalog RoBERTa boost

<<<<<<< HEAD
- **Decision:** Primary classifier `vectorsense/toxiscan` (INT8 ONNX, ~136 MB). Secondary Tagalog hate model `mapsoriano/roberta-tagalog-base-philippine-elections-2016-2022-hate-speech` as boost when language is Tagalog/Taglish.
- **Alternatives:** Horizon-Labs multilingual-toxicity-small; gravitee DistilBERT toxicity.
- **Why:** Multi-label categories including threat/self_harm; CPU-friendly; Tagalog specialist improves Filipino coverage. Cebuano remains weak → rely on translation ensemble (Part 7).
- **Date:** 2026-10-20

## ADR-003: NSFW — OpenNSFW ONNX

- **Decision:** `yahoo-open-nsfw-onnx` / OpenNSFW2 (~24 MB).
- **Alternatives:** Falconsai ViT (~75 MB); NudeNet (AGPL — avoided for license friction).
- **Why:** Small, fast on CPU, permissive licensing.
- **Date:** 2026-10-20
=======
- **Decision:** Primary classifier `vectorsense/toxiscan` (ONNX, ~136 MB, Apache-2.0).
  Secondary Tagalog hate model
  `mapsoriano/roberta-tagalog-base-philippine-elections-2016-2022-hate-speech` (CC-BY-SA-4.0)
  as a boost when the language is Tagalog.
- **Alternatives:** Horizon-Labs multilingual-toxicity-small; gravitee DistilBERT toxicity.
- **Why:** Multi-label categories including threat/self_harm; CPU-friendly; the
  Tagalog specialist improves Filipino coverage. Cebuano remains weak, so the
  translation ensemble (ADR-005) is the main defence there.
- **Verified:** 2026-10-21 — ToxiScan loads and runs through `onnxruntime` with
  no PyTorch. Benign text scored harassment 0.13; clearly abusive text scored
  0.9999. See `ml-service/scripts/smoke_real_models.py`.
- **Date:** 2026-10-20 (verified 2026-10-21)

## ADR-003: NSFW — OpenNSFW2 (ONNX)

- **Decision:** `onnx-community/nsfw_image_detection-ONNX` (the ONNX export of
  Falconsai's `nsfw_image_detection`, which is OpenNSFW2). It runs through
  `onnxruntime` with no PyTorch, and the preprocessing is done in-process
  (224x224, mean/std 0.5).
- **Alternatives:** Falconsai ViT via `transformers` (needs PyTorch, ~800 MB);
  `yahoo-open-nsfw-onnx` (checked on 2026-10-21 — **this model id does not exist**;
  it was an error in the first draft of this ADR).
- **Why:** Small, fast on CPU, no PyTorch dependency, permissive licence.
- **Verified:** 2026-10-21 — the model loads and runs; a plain grey image scores
  0.006. See `ml-service/scripts/smoke_real_models.py`.
- **Date:** 2026-10-21
>>>>>>> d5851d3 (Initial commit)

## ADR-004: Video — ffmpeg frame sampling

- **Decision:** Sample first/middle/last + N configurable frames; max NSFW score wins.
- **Why:** No separate video model; clear failure if ffmpeg missing.
- **Date:** 2026-10-20

## ADR-005: Translation — NLLB-200 distilled 600M via CTranslate2

- **Decision:** `facebook/nllb-200-distilled-600M` with `tgl_Latn` / `ceb_Latn` → `eng_Latn`.
- **Alternatives:** Opus-MT (more permissive license, weaker Cebuano).
- **Why:** Confirmed Tagalog and Cebuano coverage. **License CC-BY-NC** — academic/local only; swap before commercial deploy.
- **Date:** 2026-10-20

## ADR-006: Language detection — lingua-py

- **Decision:** Use lingua-py in the sidecar; low-confidence / short text → skip or mark UNKNOWN.
- **Alternatives:** fastText lid.176 (heavier).
- **Date:** 2026-10-20

## ADR-007: Link screening — offline heuristics

- **Decision:** Local domain blocklist + typosquat/homograph/shortener/IP-literal checks. External reputation behind `LinkReputationClient` with no-op default.
- **Date:** 2026-10-20

## ADR-008: Test database — Testcontainers PostgreSQL; H2 fallback profile

- **Decision:** Integration tests prefer Testcontainers. Unit/smoke may use `test` profile with H2 if Docker unavailable.
- **Why:** Dialect fidelity when Docker present; still runnable without it for basic context tests.
- **Date:** 2026-10-20

## ADR-009: Moderation decision thresholds

- Escalation category ≥ 0.85 or keyword escalation → `ESCALATE`
- Max score ≥ 0.90 → `BLOCK`
- Max in [0.55, 0.90) → `QUEUE_FOR_REVIEW`
- Else → `PUBLISH`
- Reputation boost capped at +0.15
- Text sync; ML unavailable → `QUEUE`
- Media async behind quarantine
- All values overridable via `schoolchan.moderation.*` config
- **Date:** 2026-10-20
<<<<<<< HEAD
=======
- **Superseded in part by ADR-014** (the board no longer blocks or holds on its own).
>>>>>>> d5851d3 (Initial commit)

## ADR-010: Reputation model

- Session reliability ∈ [0.1, 2.0], start 1.0, decay over time
- No single session outweighs the cap
- Votes collapse UI only; never sole moderation signal
- **Date:** 2026-10-20

## ADR-011: Package base — `com.schoolchan`

- Java base package `com.schoolchan` with domain packages under it.
- **Date:** 2026-10-20
<<<<<<< HEAD
=======

## ADR-012: Optional student sign-in (deviation from "account-free")

- **Decision:** A student-ID sign-in exists as an **optional gate**, off by default
  (`schoolchan.students.login-required=false`). Staff provision accounts; a signed-in
  student can request that staff onboard a newly enrolled classmate, which stays pending
  until a staff member approves it.
- **Alternatives:** (a) No student accounts at all - the pure anonymous model of the paper.
  (b) Mandatory student login for everyone.
- **Why:** The school asked to make sure only its own students can use the platform.
  Option (b) would break the paper's defining property - an anonymous, account-free board
  where "no poster IDs" and "anonymity toward users is absolute".
- **Guardrails:** The account is **never** written to a post, vote, report or bookmark.
  Posts stay labelled "Anonymous" and continue to use the salted anonymous session hash,
  so enabling the gate does **not** make any post attributable. The gate is off by default
  so the capstone demo and the Chapter 4 evaluation keep the open, anonymous model.
- **One identity at a time:** a student sign-in invalidates the browser session (dropping
  any staff session) and a staff sign-in clears the student marker, so a staff account and
  a student account are never active in the same browser.
- **Staff areas are role-gated:** `/staff/**` now requires `ROLE_ADMIN` or `ROLE_MODERATOR`.
  Previously any authenticated principal (including a student) could reach them.
- **Signed-out visitors:** when the gate is on they may still read, but every write
  (post, reply, vote, report, bookmark, edit) is sent to the sign-in page. A separate
  `block-reading` option closes reading as well.
- **Risks:** The student ID is a weak identifier and staff could abuse the roster.
  Mitigations: staff actions on the roster are audited, provisioning shows the temporary
  password once, and the roster screen is ADMIN-only.
- **Date:** 2026-10-21

## ADR-013: Theme is light by default, dark only on click

- **Decision:** The board renders the light palette by default. The operating system
  `prefers-color-scheme` is ignored; dark mode is applied only when the reader clicks the
  header toggle, and the choice is remembered in the browser.
- **Why:** The dark palette was the original default and was uncomfortable for some
  readers. Honouring the OS preference meant the page could change appearance without
  warning, which is jarring on a shared school machine.
- **Date:** 2026-10-21

## ADR-014: The board never removes a post on its own — publish, then flag

- **Decision:** Every post is published. Nothing is blocked, held or hidden by a machine.
  When the filter (keyword, link, ML text or ML image), another student, or a moderator
  thinks a post breaks the rules, the post is **flagged** and waits in the staff review
  queue, where a person decides whether it stays or comes down.
- **Why:** The school set this policy directly. Over-blocking by a model is a real harm to
  students, and a human should be the one who validates a removal. It also fits the
  capstone's own rule that "automated flags are a starting point, not a verdict".
- **What changed:**
  - `schoolchan.moderation.auto-block-enabled` (default `false`). `DecisionEngine` can still
    return `BLOCK` when staff turn this on, which keeps the Chapter 4 evaluation and the
    paper's "high confidence = block" wording reproducible. The flag is off in normal use.
  - `QUEUE_FOR_REVIEW` now means "published and flagged", not "hidden". The post status is
    `PUBLISHED`; three new columns record the flag: `flagged`, `flag_reason`, `flag_source`.
  - A student report flags the post (`flag_source = student`). It no longer hides it, even
    when the weighted report count is high.
  - A moderator can flag a post directly from the review queue (`flag_source = moderator`).
  - A low-trust poster is flagged (`flag_source = reputation`) instead of being held.
  - The moderation result is still written for every submission, so the staff screen and the
    evaluation harness see the same scores as before.
- **Flag sources:** `filter`, `student`, `moderator`, `reputation`, `escalation`.
- **Escalation — the one exception.** A post that reads as a genuine safety risk (a credible
  threat or self-harm) is still hidden from the public board and routed to the restricted
  escalation queue. `schoolchan.moderation.hide-escalations` (default `true`) controls this;
  staff can turn it off in **Settings → Moderation policy** if the school wants everything
  published. Even when hidden, the post is still flagged, so it appears in the review queue.
  This exception exists because the capstone requires a working escalation pathway to the
  guidance office, and publishing a credible threat on a school board is a real harm.
- **What the poster sees:** nothing at posting time. A post is published like any other
  post, and the notice simply says "Posted." The flag is a staff-side thing: telling the
  writer "a moderator is looking at this post" makes a published post feel pending, and
  it lets a writer probe which rule they are close to. The writer finds out only when a
  moderator acts — then **My posts** shows the post as removed, with a plain note
  ("A moderator removed this post. You can still read it here, and ask them to look
  again") and an appeal box.
- **The appeal path** therefore starts from a removal, not from a flag: a student can ask
  a moderator to look again at a post that was removed or hidden for safety.
- **Date:** 2026-10-22

## ADR-016: The composer must never empty the message it sends

- **Decision:** The "clear the composer on submit" script may only *mark* the
  textarea as submitted. It must not set its value to an empty string.
- **Why:** The browser builds the request body **after** the `submit` event has
  finished. A handler that empties the textarea therefore posts an empty
  message. This shipped by accident and broke every post and reply made in a
  browser: the post was created, the post number advanced, and the body was
  blank. The empty post was invisible on the board, and staff saw a queue item
  with nothing in it.
- **The fix:** mark the textarea with `data-cleared` and drop the saved draft,
  but leave the value alone. The value is cleared later, when the page comes
  back from the browser cache, so a reply that was already sent is not shown
  again.
- **Safety net:** the server refuses an empty message outright
  ("There was no message to post. Write something and try again"), so a blank
  post can never reach the board even if a browser sends one.
- **Regression test:** `ComposerScriptTest` asserts the submit handler does not
  touch the textarea's value. `PostContentTest` asserts that an empty reply is
  refused and that a real reply keeps its words.
- **Date:** 2026-10-22

## ADR-017: The review queue shows the whole post

- **Decision:** Every item in the staff review queue carries the full content:
  the post text as written, the English translation when there is one, the
  board and thread it came from with a link to it, and any attachment.
  Attachments are served from a staff-only route that also serves files still
  in quarantine; every view is written to the audit log.
- **Why:** A moderator cannot qualify or validate a report they cannot read.
  A blank body in the queue is worse than no item at all, because it looks
  like a decision to make.
- **Empty body:** if a post really has no text, the queue says so in words
  rather than showing an empty box.
- **Date:** 2026-10-22

## ADR-018: Speed and disk — fewer queries, smaller pictures

- **Decision:** The board pages, the catalog and the staff screens are built from a
  fixed number of queries, and pictures are shrunk when they arrive.
- **Why:** The board page used to ask the database once per thread: every post of
  every thread, then every attachment of the first post, then whether this browser
  had bookmarked it. That was **58 queries for one page of twenty threads**, and it
  grew with the size of the board. The staff dashboard was worse at **590**, because
  it looked up each moderation result's post one at a time.
- **What changed:**
  - `BoardPageService` loads a page of threads with three queries: the opening post
    of each thread, the pictures, and the bookmarks.
  - The staff dashboard looks up its posts in one batch instead of one at a time.
  - The review queue asks for flagged posts directly instead of reading every post
    in the database.
  - New indexes back those queries: `media(post_id)`, `posts(thread_id, status,
    board_post_number)`, `moderation_results(created_at)`.
  - The **local** profile now runs the real migrations (baselined at 13) instead of
    letting Hibernate invent the schema, and Hibernate only *checks* the schema. A
    fresh database was created from the migrations and validated, so a new install
    is known to work.
- **Pictures:**
  - An upload is scaled down to 1600 pixels on its long side, so a 4 MB phone photo
    becomes a few hundred kilobytes.
  - A small copy (320 pixels) is written beside it and used for every board
    preview, so a page never downloads a full-size photo.
  - A thread's preview picture may come from the opening post **or from a reply**,
    so a thread whose picture was in a reply still shows something on the board.
  - A nightly job shrinks anything still too large and makes the missing preview
    copies; another removes files no post points at any more.
- **Storage:** the demo media folder went from **55.7 MB to 2.1 MB** once the
  unreachable leftovers were removed. The moderation log is trimmed on the same
  schedule as the poster metadata, because every post leaves a row behind.
- **Measured** (H2, local, warm): the board page went from 58 queries / 54 ms to
  10 queries / 34 ms; the catalog from 88 / 55 ms to 10 / 24 ms; the staff
  dashboard from 590 / 148 ms to 18 / 79 ms.
- **Date:** 2026-10-22

## ADR-015: Personal pages are hidden until a student has signed in

- **Decision:** "My posts" and "Bookmarks" do not appear in the header, and their URLs
  redirect to the student sign-in page, until somebody is signed in as a student. A
  signed-out visitor sees "Student sign in" instead.
- **Why:** Both pages are tied to the browser session. With the student gate on, a
  signed-out visitor cannot post at all, so the pages are always empty for them — a dead
  link that only confuses. Showing them made it look as though the board tracked people
  who had not signed in.
- **Detail:** When the gate is turned off (`schoolchan.students.login-required=false`,
  the paper's pure anonymous mode) the pages are reachable again, because a visitor can
  then post and the list is meaningful. Staff always keep access, since they sign in with
  a staff account rather than a student one (one identity at a time).
- **Date:** 2026-10-22

## ADR-019: A preview copy is served under its own name

- **Decision:** the public media route understands both names a picture has: the
  original stored name and the `.thumb.jpg` preview copy. A preview name is mapped
  back to the row that owns it, and if the preview file is missing the original is
  served instead. Names containing `/`, `\` or `..` are refused.
- **Why:** board and catalog cards ask for the preview copy. The route only looked
  up stored names, so every preview copy answered **404** and the board looked as
  though its pictures had been deleted after the storage tidy.
- **Detail:** the hover preview of a quoted reply (and of a thread card) now shows
  that post's own preview copy next to its text. A post with no picture renders no
  image, and a quarantined picture is still never served.
- **Date:** 2026-10-23

## ADR-020: Signing in returns to a page that can actually be opened

- **Decision:** the student gate sends the browser back to the page the blocked
  action came from (`Referer`), and a `next` value that points at a POST-only
  endpoint (`/reply`, `/vote`, `/report`, `/bookmarks/toggle`, logout, and the
  other write routes) falls back to `/`.
- **Why:** a signed-out visitor who pressed "Post reply" was sent to
  `/student/login?next=…/reply`. After a **correct** sign-in the browser followed
  that value with a GET, the endpoint answered 405, and the visitor saw "Something
  went wrong" instead of the home page.
- **Detail:** `next` must be a same-site path; CR/LF, `//` and `/\` are rejected.
  A wrong password shows an explicit message on both sign-in pages, and the error
  page no longer depends on the CSRF token being present.
- **Date:** 2026-10-23

## ADR-021: On a phone the composer stays in the page

- **Decision:** at 640px and below the posting form is a normal block in the page
  flow. A sticky "Reply to this thread" bar scrolls to it. The pinned opening post
  and the floating scroll arrows are hidden on phones.
- **Why:** the form used to be pinned to the bottom of the screen on every page,
  where it covered a third of the board and the thread while scrolling. The pinned
  opening post covered replies on the same small screen.
- **Detail:** the header navigation wraps onto as many lines as it needs, so the
  board tabs, Rules, About and the theme toggle stay reachable.
- **Date:** 2026-10-23

## ADR-022: A spoiler hides its picture as well as its text

- **Decision:** a spoiler post puts its text **and its attachments** inside the
  same click-to-reveal block. Attachments moved into
  `templates/fragments/attachments.html` so both the spoiler and the normal
  branch can render them. The thread-list card and the hover preview follow the
  same rule.
- **Why:** the thread page rendered the media outside the block, so a spoiler
  opening post showed its picture straight away. The blur on the whole block also
  blurred the "Spoiler" label, which made the post impossible to open on purpose.
- **Detail:** a closed `<details>` already hides its children, so no blur filter
  is applied; the label is styled in the warning colour instead. The pinned
  opening-post bar now shows only the subject and a jump link, so it no longer
  repeats (or leaks) the opening post while the reader scrolls.
- **Caveat:** `th:replace` runs before `th:if` on the same element, so the
  condition for a replaced fragment lives on a wrapping `th:block`.
- **Date:** 2026-10-23

## ADR-023: Tests never write into the running board's data folder

- **Decision:** the `test` profile points `schoolchan.media.root` at
  `./build/test-media`, and the two web tests that had no profile now declare
  `@ActiveProfiles("test")`.
- **Why:** those tests booted the default profile, whose media root is the real
  `./data/media`. Each run re-seeded sample pictures into it, so after a few
  test runs the folder held 644 files that belonged to no post (the numbers in
  the app were still correct, but the folder had grown to 18 MB).
- **Detail:** the staff "Tidy storage" action removed the unused files
  afterwards (back to 112 files, 2.1 MB). It only deletes files that no media row
  names, so nothing that a post can display is ever dropped.
- **Date:** 2026-10-23

## ADR-024: The thread answers back (backlinks, preview, timestamps)

- **Decision:** a reply that quotes `>>123` is listed under post 123 as
  `? 124, 130`. Your own posts carry a quiet `You` chip, and the post you land
  on flashes briefly. A long thread renders its opening post plus the newest
  `schoolchan.threads.reply-preview-limit` (60) replies; the rest are behind
  `N replies omitted � show all` (`?all=true`), and a link to an omitted post
  reopens the thread with every reply automatically.
- **Why:** the board already parsed `>>123` in one direction only, so a reader
  could follow a quote but never see who had answered a post. Long threads also
  rendered every reply at once, and dates were absolute, so "is this live?" took
  arithmetic.
- **Detail:** backlinks are built from published, non-author-deleted posts only
  (a hidden post must not reveal that it quoted something), self-quotes and
  duplicate numbers in one post are ignored, and the target must exist in the
  thread. Timestamps become "12 min ago" in the browser, with the exact time in
  the tooltip and as the server-rendered fallback when JS is off.
- **Detail:** each post has a `#` permalink and `/boards/{slug}/p/{number}`
  redirects to its place in the thread. Posting (a reply or a new thread) lands
  on the new post (`#pN`) instead of the top of the page.
- **Detail:** thread pictures load lazily with their real width and height, the
  "Expand images" button remembers its state per browser, and the lightbox steps
  through the thread's pictures with ? / ?, Escape to close.
- **Bug found while testing:** a deliberate `ResponseStatusException` (a post
  number that does not exist) was flattened into a 500 by the catch-all error
  handler. It now keeps its status code and shows the friendly 404 page.
- **Date:** 2026-10-23

## ADR-025: Reader control belongs to the reader

- **Decision:** keyword rules and "hide this thread" are stored only in the
  browser (`localStorage` keys `sc-reader-filters` and `sc-hidden-threads`).
  Nothing is written to the database and nothing changes for another reader.
  The rule list is capped at 20 entries and the hidden-thread list at 200; a
  storage failure in private mode degrades to "no filters", never an error.
- **Why:** G4 is a reading aid, not a moderation action. Putting it on the
  server would create a per-session table, raise questions about moderation
  telemetry, and make one browser's choices visible after a device change.
  Keeping it local also means the no-JS page is unchanged: the panel, the hide
  buttons and the "N hidden — show" bar stay `hidden` until `board.js` runs.
- **Detail:** a rule matches case-insensitively against the subject plus the
  first 400 characters of the opening post. A spoiler's body is left out of the
  `data-filter-text` attribute so a filter cannot surface text the reader has
  not opened. The strongest matching action wins: hide, then collapse, then
  highlight. "Collapse" leaves a one-line note with a Show button; "Hide this
  thread" adds the thread to the hidden list and the bar offers Show / Hide
  again / Unhide all.
- **Detail:** the endless-scroll loader appends cards after page load, so a
  `MutationObserver` re-applies the rules when new thread cards arrive. It
  watches only thread-card/catalog-card additions, not the filter notes the
  script itself inserts, which avoids an apply loop.
- **Detail (catalog):** the catalog is server-sorted into bump, newest, most
  replies or most images; pinned threads stay first in every order. "Has
  pictures" keeps threads with a released image anywhere in the thread (the
  same preview query used for thumbnails), not merely a nonzero image counter.
  Each card shows two lines of opening text, except a spoiler thread, which
  says "Spoiler thread — open to read" instead. An unknown `sort` value falls
  back to bump rather than returning an error.
- **Detail (Quoted you):** `/my-posts?tab=quotes` builds an inbox from the
  session's own posts. Quote numbers are board-scoped, so each board with one
  of your posts is scanned once, the existing `QuoteIndex` supplies the
  backlinks, and the result is grouped by reply. Your own quotes, hidden or
  author-deleted replies, and replies in the wrong board are excluded. The tab
  links to the thread at `#pN` and to each of your quoted numbers through
  `/boards/{slug}/p/{n}`.
- **Tests:** `CatalogOrderTest`, `QuoteInboxServiceTest`, `CatalogPageTest`,
  `MyPostsQuotesTest`; the reader-filter behaviour itself is verified in the
  browser because it is client-only.
- **Date:** 2026-10-29

## ADR-026: Live threads poll, they do not push

- **Decision:** a thread page polls `/boards/{slug}/threads/{id}/posts-after/{n}` every
  `schoolchan.thread.live-poll-seconds` seconds (default 25) and shows a
  "N new posts — show" bar. Nothing is inserted until the reader clicks it.
  The post markup lives in `fragments/post.html` so the polled fragment is
  byte-for-byte the same as the page.
- **Why:** WebSockets or server-sent events would add infrastructure and a
  long-lived connection to a capstone that is deliberately local-first. Polling
  a small fragment is enough for a school board, and the "show" button means no
  post moves under a reader who is mid-sentence.
- **Detail:** polling stops while the tab is hidden (`document.hidden`) or while
  any textarea on the page has text in it, and resumes when those clear. The
  interval is a property; whether polling runs at all is the site setting
  `live_poll_enabled` (default on), toggled from Staff → Settings. The page
  carries `data-live-poll-seconds` and `data-live-poll-enabled` so the script
  needs no extra request to learn them.
- **Detail:** the endpoint returns only published posts with a number greater
  than the last one the page already has, so a held or removed post never
  appears live and a second poll cannot duplicate one already shown. Inserted
  posts are skipped if their `#pN` anchor already exists in the document.
- **Tests:** `LiveThreadTest` covers "posts after N" and the empty case; the
  pause/resume behaviour is checked in the browser.
- **Date:** 2026-10-29

## ADR-027: Finish-line polish stays in existing seams

- **Decision:** cache headers are applied in `WebConfig` and `MediaController` rather than
  a new filter; the bump-limit banner reuses the board's `bumpLimit` and the thread's
  `replyCount`; image dimensions come from the already-stored `media.width/height`;
  focus management is a small client-side enhancement.
- **Why:** these are cross-cutting but small, and each fits an existing seam. A new
  caching or accessibility subsystem would be out of proportion for the finishing pass.
- **Detail:** `/media` and static assets get a public, one-week (media: 30-day) cache.
  The bump banner appears once `thread.replyCount >= board.bumpLimit`. `aria-live` was
  already on the new-posts bar; focus moves to the post named in the URL hash after
  load. Empty states already existed for search, bookmarks and quotes; this pass only
  confirmed them.
- **Date:** 2026-10-29
>>>>>>> d5851d3 (Initial commit)
