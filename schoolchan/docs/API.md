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
| GET | `/media/{id}` | Released media only |
| GET | `/actuator/health` | Health |

## Staff (`/staff/**`)

| Method | Path | Description |
| --- | --- | --- |
| GET/POST | `/staff/login` | Staff login |
| GET | `/staff` | Dashboard |
| GET | `/staff/review` | Review queue |
| POST | `/staff/review/{id}/approve` | Approve held post |
| POST | `/staff/review/{id}/block` | Block |
| POST | `/staff/review/{id}/confirm` | Confirm violation (reputation loop) |
| GET | `/staff/escalations` | Escalation queue (ADMIN) |
| POST | `/staff/escalations/{id}/reveal-metadata` | Decrypt IP (audited) |
| GET | `/staff/settings` | Access windows, limits, rules |
| GET | `/staff/blacklist` | Keyword management |
| GET | `/staff/eval` | Eval harness UI |

## ML sidecar

| Method | Path | Description |
| --- | --- | --- |
| GET | `/health` | Sidecar health |
| POST | `/v1/text/classify` | Toxicity / hate scores |
| POST | `/v1/image/classify` | NSFW score |
| POST | `/v1/translate` | Translate to English |
