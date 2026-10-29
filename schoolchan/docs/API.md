# API / Routes

Server-rendered HTML is primary. JSON endpoints are limited to htmx partials and staff tools.

## Public

| Method | Path | Description |
| --- | --- | --- |
| GET | `/` | Home, boards list, rotating quote |
| GET | `/rules` | Rules and conditions |
| GET | `/{board}` | Active threads |
| GET | `/{board}/catalog` | Catalog grid |
| GET | `/{board}/archive` | Archived threads |
| GET | `/{board}/thread/{id}` | Thread view |
| POST | `/{board}/thread` | Create thread |
| POST | `/{board}/thread/{id}/reply` | Reply |
| POST | `/posts/{id}/vote` | Up/down vote |
| POST | `/posts/{id}/report` | Report post |
<<<<<<< HEAD
=======
| GET | `/my-posts` | Posts from this browser (needs a student sign-in) |
| GET | `/bookmarks` | Saved threads (needs a student sign-in) |
>>>>>>> d5851d3 (Initial commit)
| GET | `/media/{id}` | Released media only |
| GET | `/actuator/health` | Health |

## Staff (`/staff/**`)

| Method | Path | Description |
| --- | --- | --- |
| GET/POST | `/staff/login` | Staff login |
| GET | `/staff` | Dashboard |
<<<<<<< HEAD
| GET | `/staff/review` | Review queue |
| POST | `/staff/review/{id}/approve` | Approve held post |
| POST | `/staff/review/{id}/block` | Block |
| POST | `/staff/review/{id}/confirm` | Confirm violation (reputation loop) |
| GET | `/staff/escalations` | Escalation queue (ADMIN) |
| POST | `/staff/escalations/{id}/reveal-metadata` | Decrypt IP (audited) |
| GET | `/staff/settings` | Access windows, limits, rules |
| GET | `/staff/blacklist` | Keyword management |
| GET | `/staff/eval` | Eval harness UI |
=======
| GET | `/staff/review` | Review queue (flagged, hidden, reported) — shows the whole post |
| GET | `/staff/media/{name}` | An attachment for staff, including one still in quarantine (audited) |
| POST | `/staff/posts/{id}/publish` | Leave it up; clears the flag |
| POST | `/staff/posts/{id}/flag` | Flag a post for review (with a reason) |
| POST | `/staff/posts/{id}/block` | Remove the post; confirms its reports |
| POST | `/staff/settings/moderation-policy` | Change the moderation policy (ADMIN) |
| POST | `/staff/settings/shrink-media` | Shrink stored images and make preview copies |
| POST | `/staff/settings/tidy-media` | Delete media files no post points at |
| POST | `/staff/review/{id}/confirm` | Confirm violation (reputation loop) |
| GET | `/staff/escalations` | Escalation queue (ADMIN) |
| POST | `/staff/escalations/{id}/reveal-metadata` | Decrypt IP (audited) |
| GET | `/staff/settings` | Access windows, limits, rules, storage |
| GET | `/staff/blacklist` | Keyword management |
| POST | `/staff/api/eval/confusion` | Confusion matrix (ADMIN) |
| POST | `/staff/api/eval/mcnemar` | McNemar statistic (ADMIN) |
>>>>>>> d5851d3 (Initial commit)

## ML sidecar

| Method | Path | Description |
| --- | --- | --- |
| GET | `/health` | Sidecar health |
| POST | `/v1/text/classify` | Toxicity / hate scores |
| POST | `/v1/image/classify` | NSFW score |
| POST | `/v1/translate` | Translate to English |
