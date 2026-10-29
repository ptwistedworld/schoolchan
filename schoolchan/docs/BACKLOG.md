# SchoolChan Backlog — Missing, Improvements, Innovations

## Currently missing (from Master Prompt / capstone objectives)

### Core board
- [ ] Per-thread image limit enforcement in UI (show remaining slots).
- [ ] Catalog view polish (grid cards with thumbnails).
- [ ] Thread locking/pinning admin UI (backend fields exist).
- [ ] Search across boards and archive (basic subject search).
- [ ] Pagination on archive and board (currently loads all).

### Moderation
- [ ] Real ML wiring (ToxiScan / Tagalog RoBERTa) with model download + cache.
- [ ] NSFW image classifier (OpenNSFW ONNX) instead of mock.
- [ ] Video frame sampling requires ffmpeg; document + fail clearly.
- [ ] Translation caching by content hash; async fallback.
- [ ] Reputation-weighted auto-hold threshold tuning UI.
- [ ] Learned-blacklist candidate approval UI (backend exists).
- [ ] Similarity boost using embeddings from sidecar.

### Access & staff
- [ ] Admin override "open now / close now" with expiry (backend param exists, no UI).
- [ ] Rules text versioning/edit UI (backend exists).
- [ ] Quote management UI (add/remove quotes).
- [ ] Staff management (create/disable moderators).
- [ ] Audit log viewer + accountability view.
- [ ] Escalation package PDF export (print view exists).
- [ ] Retention purge job dry-run + logging.

### UX / usability
- [ ] Mobile-first responsive layout pass.
- [ ] Keyboard navigation + accessibility (ARIA, focus states).
- [ ] Loading/empty states, non-revealing blocked messages.
- [ ] Dark mode refinement (prefers-color-scheme).
- [ ] Thread hover preview (already partial via preview endpoint).

### Evaluation
- [ ] Load probe (k6 or Python) for ISO 25010 performance.
- [ ] Threshold sweep notebook/script.
- [ ] Dataset loader guide with approved public datasets.
- [ ] Chart generation (PNG) in eval results.

### Security / privacy
- [ ] OWASP Top 10 review checklist (docs/SECURITY.md started).
- [ ] Dependency vulnerability scan target.
- [ ] Anonymity audit automated test (expanded; add header/cookie checks).
- [ ] Honeypot (done) + CAPTCHA fallback for abuse spikes.

## Improvement ideas

1. **Live activity feed** — show recent posts/votes on home page (anonymized).
2. **Thread tags / flair** — optional tags (Academics, Events) for filtering.
3. **Bookmark threads** — per-session bookmark list (client-side or hashed session).
4. **Report reasons analytics** — staff dashboard charts of report categories over time.
5. **Post edit window** — allow poster to edit within N minutes (anonymous but session-bound).
6. **Staff notes on threads** — internal notes not shown publicly.
7. **Rate-limit backoff** — progressive cooldown instead of hard block.
8. **Image thumbnails + lazy loading** — reduce page weight.
9. **Spoiler topic labels** — richer spoiler UX with blur + reveal animation.
10. **Pinned thread banner** — highlight pinned threads on board.

## Innovations (capstone differentiators)

- **Adaptive moderation thresholds** based on time-of-day / board activity.
- **Community reputation leaderboard (anonymized)** — show aggregate trust tiers without identities.
- **Automatic FAQ detection** — cluster repeated questions and surface answers.
- **Multilingual profanity normalization** — expand leetspeak/confusable handling to Tagalog/Cebuano.
- **Safety escalation dry-run mode** — staff can rehearse escalation workflow with synthetic data.
- **Explainable moderation** — per-post "why this was held" card (scores + evidence) for transparency.
- **Offline-first PWA** — cache recent threads for read-only offline access.
- **Accessibility audit badge** — automated a11y check in CI.

## Quick wins (do next)

- Populate demo data (done via DataSeeder enhancement).
- Add `/search` endpoint and page.
- Add staff review queue bulk actions.
- Add thread pin/lock toggles in staff UI.
- Refresh README quickstart with screenshots.