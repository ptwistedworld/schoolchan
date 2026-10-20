#!/usr/bin/env python3
"""Generate SchoolChan source tree."""
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "schoolchan"
JAVA = ROOT / "backend" / "src" / "main" / "java" / "com" / "schoolchan"
TEST = ROOT / "backend" / "src" / "test" / "java" / "com" / "schoolchan"
RES = ROOT / "backend" / "src" / "main" / "resources"
TEMPL = RES / "templates"
STATIC = RES / "static" / "css"
ML = ROOT / "ml-service"


def w(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.lstrip("\n"), encoding="utf-8")
    print("wrote", path.relative_to(ROOT))


def main() -> None:
    w(
        RES / "application.yml",
        """
spring:
  application:
    name: schoolchan
  thymeleaf:
    cache: false
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 25MB
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        jdbc:
          time_zone: UTC
  flyway:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info

schoolchan:
  timezone: Asia/Manila
  closed-mode: READ_ONLY
  metadata-key: ${SCHOOLCHAN_METADATA_KEY:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=}
  metadata-retention-days: 90
  admin-username: ${SCHOOLCHAN_ADMIN_USERNAME:admin}
  admin-password: ${SCHOOLCHAN_ADMIN_PASSWORD:change-me-now}
  ml:
    base-url: ${SCHOOLCHAN_ML_BASE_URL:http://localhost:8090}
    enabled: ${SCHOOLCHAN_ML_ENABLED:true}
    timeout-ms: 3000
  moderation:
    baseline-only: false
    block-threshold: 0.90
    queue-threshold: 0.55
    escalation-threshold: 0.85
    reputation-boost-cap: 0.15
    fail-on-ml-unavailable: QUEUE
  board-defaults:
    active-thread-cap: 50
    bump-limit: 300
    image-limit: 150
  media:
    root: ./data/media
    max-image-bytes: 5242880
    max-video-bytes: 20971520
  rate-limit:
    posts-per-minute: 10
""",
    )

    w(
        RES / "application-dev.yml",
        """
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/schoolchan}
    username: ${SPRING_DATASOURCE_USERNAME:schoolchan}
    password: ${SPRING_DATASOURCE_PASSWORD:schoolchan}
""",
    )

    w(
        RES / "application-test.yml",
        """
spring:
  datasource:
    url: jdbc:h2:mem:schoolchan;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
  flyway:
    enabled: true
schoolchan:
  ml:
    enabled: false
""",
    )

    # Fix H2: TIMESTAMPTZ and UUID work in PostgreSQL mode; BYTEA -> VARBINARY via H2
    # Use a test-specific migration placeholder - actually Flyway V1 uses TIMESTAMPTZ and BYTEA
    # For H2 we need compatible SQL. Add H2-compatible note: use PostgreSQL mode which maps TIMESTAMPTZ.
    # BYTEA is supported as alias in H2 PostgreSQL mode.

    files = {}

    files[JAVA / "SchoolChanApplication.java"] = """
package com.schoolchan;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(SchoolChanProperties.class)
@EnableScheduling
public class SchoolChanApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchoolChanApplication.class, args);
    }
}
"""

    files[JAVA / "config" / "SchoolChanProperties.java"] = """
package com.schoolchan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schoolchan")
public class SchoolChanProperties {
    private String timezone = "Asia/Manila";
    private String closedMode = "READ_ONLY";
    private String metadataKey;
    private int metadataRetentionDays = 90;
    private String adminUsername = "admin";
    private String adminPassword = "change-me-now";
    private final Ml ml = new Ml();
    private final Moderation moderation = new Moderation();
    private final BoardDefaults boardDefaults = new BoardDefaults();
    private final Media media = new Media();
    private final RateLimit rateLimit = new RateLimit();

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getClosedMode() { return closedMode; }
    public void setClosedMode(String closedMode) { this.closedMode = closedMode; }
    public String getMetadataKey() { return metadataKey; }
    public void setMetadataKey(String metadataKey) { this.metadataKey = metadataKey; }
    public int getMetadataRetentionDays() { return metadataRetentionDays; }
    public void setMetadataRetentionDays(int metadataRetentionDays) { this.metadataRetentionDays = metadataRetentionDays; }
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
    public String getAdminPassword() { return adminPassword; }
    public void setAdminPassword(String adminPassword) { this.adminPassword = adminPassword; }
    public Ml getMl() { return ml; }
    public Moderation getModeration() { return moderation; }
    public BoardDefaults getBoardDefaults() { return boardDefaults; }
    public Media getMedia() { return media; }
    public RateLimit getRateLimit() { return rateLimit; }

    public static class Ml {
        private String baseUrl = "http://localhost:8090";
        private boolean enabled = true;
        private int timeoutMs = 3000;
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public int getTimeoutMs() { return timeoutMs; }
        public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    }

    public static class Moderation {
        private boolean baselineOnly;
        private double blockThreshold = 0.90;
        private double queueThreshold = 0.55;
        private double escalationThreshold = 0.85;
        private double reputationBoostCap = 0.15;
        private String failOnMlUnavailable = "QUEUE";
        public boolean isBaselineOnly() { return baselineOnly; }
        public void setBaselineOnly(boolean baselineOnly) { this.baselineOnly = baselineOnly; }
        public double getBlockThreshold() { return blockThreshold; }
        public void setBlockThreshold(double blockThreshold) { this.blockThreshold = blockThreshold; }
        public double getQueueThreshold() { return queueThreshold; }
        public void setQueueThreshold(double queueThreshold) { this.queueThreshold = queueThreshold; }
        public double getEscalationThreshold() { return escalationThreshold; }
        public void setEscalationThreshold(double escalationThreshold) { this.escalationThreshold = escalationThreshold; }
        public double getReputationBoostCap() { return reputationBoostCap; }
        public void setReputationBoostCap(double reputationBoostCap) { this.reputationBoostCap = reputationBoostCap; }
        public String getFailOnMlUnavailable() { return failOnMlUnavailable; }
        public void setFailOnMlUnavailable(String failOnMlUnavailable) { this.failOnMlUnavailable = failOnMlUnavailable; }
    }

    public static class BoardDefaults {
        private int activeThreadCap = 50;
        private int bumpLimit = 300;
        private int imageLimit = 150;
        public int getActiveThreadCap() { return activeThreadCap; }
        public void setActiveThreadCap(int activeThreadCap) { this.activeThreadCap = activeThreadCap; }
        public int getBumpLimit() { return bumpLimit; }
        public void setBumpLimit(int bumpLimit) { this.bumpLimit = bumpLimit; }
        public int getImageLimit() { return imageLimit; }
        public void setImageLimit(int imageLimit) { this.imageLimit = imageLimit; }
    }

    public static class Media {
        private String root = "./data/media";
        private long maxImageBytes = 5_242_880;
        private long maxVideoBytes = 20_971_520;
        public String getRoot() { return root; }
        public void setRoot(String root) { this.root = root; }
        public long getMaxImageBytes() { return maxImageBytes; }
        public void setMaxImageBytes(long maxImageBytes) { this.maxImageBytes = maxImageBytes; }
        public long getMaxVideoBytes() { return maxVideoBytes; }
        public void setMaxVideoBytes(long maxVideoBytes) { this.maxVideoBytes = maxVideoBytes; }
    }

    public static class RateLimit {
        private int postsPerMinute = 10;
        public int getPostsPerMinute() { return postsPerMinute; }
        public void setPostsPerMinute(int postsPerMinute) { this.postsPerMinute = postsPerMinute; }
    }
}
"""

    # Continue in part 2 of generator - write remaining via this same file
    for path, content in files.items():
        w(path, content)

    print("phase1 done")


if __name__ == "__main__":
    main()
