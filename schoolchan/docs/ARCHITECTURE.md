# SchoolChan Architecture

## 1. Component overview

```mermaid
flowchart TB
  subgraph clients [Clients]
    Browser[Browser_Thymeleaf_htmx]
  end

  subgraph backend [Spring_Boot_Backend]
    Web[web_controllers]
    Board[board]
    Access[access]
    Mod[moderation]
    Rep[reputation]
    Trans[translation]
    Media[media]
    Staff[staff]
    Eval[eval]
  end

  subgraph data [Data]
    PG[(PostgreSQL)]
    Disk[LocalMediaStore]
  end

  subgraph ml [ML_Sidecar_optional]
    FastAPI[FastAPI]
    TextModel[ToxiScan_ONNX]
    NsfwModel[OpenNSFW_ONNX]
    MtModel[NLLB_CTranslate2]
  end

  Browser --> Web
  Web --> Board
  Web --> Access
  Web --> Mod
  Web --> Rep
  Web --> Media
  Web --> Staff
  Web --> Eval
  Board --> PG
  Access --> PG
  Mod --> PG
  Mod --> FastAPI
  Rep --> PG
  Trans --> FastAPI
  Media --> Disk
  Media --> FastAPI
  Staff --> PG
  Eval --> Mod
  FastAPI --> TextModel
  FastAPI --> NsfwModel
  FastAPI --> MtModel
```

## 2. Post lifecycle

```mermaid
sequenceDiagram
  participant U as User
  participant W as Web
  participant A as AccessFilter
  participant B as BoardService
  participant M as MediaStore
  participant P as Pipeline
  participant D as DecisionEngine
  participant DB as Postgres
  participant ML as ML_Sidecar

  U->>W: POST thread or reply
  W->>A: Check access window and rate limits
  alt Closed unavailable
    A-->>U: Closed page
  else Closed read_only
    A-->>U: 403 write blocked
  else Open
    W->>B: Create post PENDING_SCREENING
    B->>DB: Insert post and poster_metadata encrypted
    opt Has media
      B->>M: Store in quarantine
    end
    B->>P: Run stages
    P->>P: KeywordBlacklist
    P->>P: LinkScreening
    P->>ML: Text classify optional
    P->>ML: Translate optional
    opt Media present
      P->>ML: NSFW classify async path
    end
    P->>D: Combine scores and reputation
    alt ESCALATE
<<<<<<< HEAD
      D->>DB: HELD escalation queue
    else BLOCK
      D->>DB: BLOCKED delete media
    else QUEUE
      D->>DB: HELD_FOR_REVIEW
    else PUBLISH
      D->>DB: PUBLISHED bump or archive if cap
    end
    W-->>U: Result page or friendly block message
  end
```

=======
      D->>DB: Flagged, hidden, and added to the escalation queue
    else Flagged (filter, report, or moderator)
      D->>DB: PUBLISHED and flagged for the review queue
    else Clean
      D->>DB: PUBLISHED bump or archive if cap
    end
    W-->>U: Post page, with a note if it was flagged
  end
```

> **Policy note (ADR-014).** The board never removes a post on its own. `BLOCK` and
> `QUEUE_FOR_REVIEW` both mean "published and flagged" in normal use; only a person
> removes a post, from the staff review queue. The one exception is the escalation
> category, which is hidden and routed to the restricted escalation queue.

>>>>>>> d5851d3 (Initial commit)
## 3. Thread lifecycle state machine

```mermaid
stateDiagram-v2
  [*] --> ACTIVE: Thread created and published
  ACTIVE --> SINKING: Reply count reaches bump_limit
  ACTIVE --> ARCHIVED: Evicted by active_thread_cap
  SINKING --> ARCHIVED: Evicted by active_thread_cap
  ARCHIVED --> [*]: Permanent read-only archive
```

- **ACTIVE**: replies bump `bumped_at` (unless sage).
- **SINKING**: past bump limit; replies allowed but do not bump.
- **ARCHIVED**: read-only; never deleted for inactivity; browsable under `/{board}/archive`.

## 4. Data model (ER)

```mermaid
erDiagram
  boards ||--o{ threads : contains
  threads ||--o{ posts : contains
  posts ||--o{ media : has
  posts ||--o| moderation_results : screened_by
  posts ||--o{ reports : receives
  posts ||--o{ votes : receives
  posts ||--o| poster_metadata : tracks
  posts ||--o{ escalations : may_escalate
  staff ||--o{ audit_log : writes
  boards {
    uuid id PK
    string slug UK
    string name
    int active_thread_cap
    int bump_limit
    int image_limit
  }
  threads {
    uuid id PK
    uuid board_id FK
    string subject
    timestamp bumped_at
    string status
    int reply_count
    int image_count
  }
  posts {
    uuid id PK
    uuid thread_id FK
    bigint board_post_number
    text original_text
    text translated_text
    string detected_language
    string status
    boolean spoiler
    int score
  }
  media {
    uuid id PK
    uuid post_id FK
    string stored_name
    string content_type
    string quarantine_status
    string phash
  }
  moderation_results {
    uuid id PK
    uuid post_id FK
    string decision
    jsonb stage_results
    bigint total_latency_ms
  }
  reports {
    uuid id PK
    uuid post_id FK
    string session_hash
    string reason
    string status
  }
  votes {
    uuid id PK
    uuid post_id FK
    string session_hash
    smallint value
  }
  reputation_entries {
    uuid id PK
    string session_hash UK
    decimal reliability
    int strike_level
    timestamp updated_at
  }
  blacklist_entries {
    uuid id PK
    string pattern
    string match_type
    string category
    decimal severity
    boolean enabled
  }
  access_windows {
    uuid id PK
    string label
    string days_of_week
    time start_time
    time end_time
  }
  site_settings {
    string key PK
    text value
  }
  quotes {
    uuid id PK
    text body
    string attribution
  }
  staff {
    uuid id PK
    string username UK
    string password_hash
    string role
    boolean enabled
  }
  audit_log {
    bigint id PK
    uuid staff_id FK
    string action
    text detail
    timestamp created_at
  }
  poster_metadata {
    uuid id PK
    uuid post_id FK
    bytes ip_encrypted
    bytes session_hash
    timestamp created_at
  }
  escalations {
    uuid id PK
    uuid post_id FK
    string status
    text notes
  }
```

## 5. Package map (backend)

| Package | Responsibility |
| --- | --- |
| `board` | Boards, threads, posts, bump, archive, votes |
| `access` | Access windows, closed-mode, admin override |
| `moderation` | Pipeline stages, decision engine, queues |
| `reputation` | Reports, reliability, similarity boost |
| `translation` | Java client to sidecar translation |
| `media` | Upload, quarantine, serve, phash |
| `staff` | Auth, roles, audit, retention jobs |
| `eval` | Benchmark harness, McNemar, latency |
| `web` | Controllers, Thymeleaf, static assets |

## 6. Security boundaries

- Public routes never emit IP, session token, or staff identity.
- `poster_metadata` is AES-GCM encrypted; decrypt only on explicit staff action with audit reason.
- Media served only after release from quarantine; blocked files deleted (hash retained).
- Staff routes under `/staff/**` with CSRF, BCrypt, rate-limited login.
