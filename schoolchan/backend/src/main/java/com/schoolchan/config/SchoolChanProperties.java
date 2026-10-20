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
    private Ml ml = new Ml();
    private Moderation moderation = new Moderation();
    private BoardDefaults boardDefaults = new BoardDefaults();
    private Media media = new Media();
    private RateLimit rateLimit = new RateLimit();

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getClosedMode() {
        return closedMode;
    }

    public void setClosedMode(String closedMode) {
        this.closedMode = closedMode;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public int getMetadataRetentionDays() {
        return metadataRetentionDays;
    }

    public void setMetadataRetentionDays(int metadataRetentionDays) {
        this.metadataRetentionDays = metadataRetentionDays;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Ml getMl() {
        return ml;
    }

    public void setMl(Ml ml) {
        this.ml = ml;
    }

    public Moderation getModeration() {
        return moderation;
    }

    public void setModeration(Moderation moderation) {
        this.moderation = moderation;
    }

    public BoardDefaults getBoardDefaults() {
        return boardDefaults;
    }

    public void setBoardDefaults(BoardDefaults boardDefaults) {
        this.boardDefaults = boardDefaults;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public static class Ml {
        private String baseUrl = "http://localhost:8090";
        private boolean enabled = true;
        private long timeoutMs = 3000;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    public static class Moderation {
        private boolean baselineOnly;
        private double blockThreshold = 0.90;
        private double queueThreshold = 0.55;
        private double escalationThreshold = 0.85;
        private double reputationBoostCap = 0.15;
        private String failOnMlUnavailable = "QUEUE";

        public boolean isBaselineOnly() {
            return baselineOnly;
        }

        public void setBaselineOnly(boolean baselineOnly) {
            this.baselineOnly = baselineOnly;
        }

        public double getBlockThreshold() {
            return blockThreshold;
        }

        public void setBlockThreshold(double blockThreshold) {
            this.blockThreshold = blockThreshold;
        }

        public double getQueueThreshold() {
            return queueThreshold;
        }

        public void setQueueThreshold(double queueThreshold) {
            this.queueThreshold = queueThreshold;
        }

        public double getEscalationThreshold() {
            return escalationThreshold;
        }

        public void setEscalationThreshold(double escalationThreshold) {
            this.escalationThreshold = escalationThreshold;
        }

        public double getReputationBoostCap() {
            return reputationBoostCap;
        }

        public void setReputationBoostCap(double reputationBoostCap) {
            this.reputationBoostCap = reputationBoostCap;
        }

        public String getFailOnMlUnavailable() {
            return failOnMlUnavailable;
        }

        public void setFailOnMlUnavailable(String failOnMlUnavailable) {
            this.failOnMlUnavailable = failOnMlUnavailable;
        }
    }

    public static class BoardDefaults {
        private int activeThreadCap = 50;
        private int bumpLimit = 300;
        private int imageLimit = 150;

        public int getActiveThreadCap() {
            return activeThreadCap;
        }

        public void setActiveThreadCap(int activeThreadCap) {
            this.activeThreadCap = activeThreadCap;
        }

        public int getBumpLimit() {
            return bumpLimit;
        }

        public void setBumpLimit(int bumpLimit) {
            this.bumpLimit = bumpLimit;
        }

        public int getImageLimit() {
            return imageLimit;
        }

        public void setImageLimit(int imageLimit) {
            this.imageLimit = imageLimit;
        }
    }

    public static class Media {
        private String root = "./data/media";
        private long maxImageBytes = 5_242_880;
        private long maxVideoBytes = 20_971_520;

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public long getMaxImageBytes() {
            return maxImageBytes;
        }

        public void setMaxImageBytes(long maxImageBytes) {
            this.maxImageBytes = maxImageBytes;
        }

        public long getMaxVideoBytes() {
            return maxVideoBytes;
        }

        public void setMaxVideoBytes(long maxVideoBytes) {
            this.maxVideoBytes = maxVideoBytes;
        }
    }

    public static class RateLimit {
        private int postsPerMinute = 10;

        public int getPostsPerMinute() {
            return postsPerMinute;
        }

        public void setPostsPerMinute(int postsPerMinute) {
            this.postsPerMinute = postsPerMinute;
        }
    }
}
