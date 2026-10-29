package com.schoolchan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schoolchan")
public class SchoolChanProperties {

    private String timezone = "Asia/Manila";
    private String closedMode = "READ_ONLY";
    private String metadataKey;
    private int metadataRetentionDays = 90;
<<<<<<< HEAD
=======
    /** How long the moderation log is kept. */
    private int moderationResultRetentionDays = 180;
>>>>>>> d5851d3 (Initial commit)
    private String adminUsername = "admin";
    private String adminPassword = "change-me-now";
    private Ml ml = new Ml();
    private Moderation moderation = new Moderation();
    private BoardDefaults boardDefaults = new BoardDefaults();
    private Media media = new Media();
    private RateLimit rateLimit = new RateLimit();
<<<<<<< HEAD
=======
    private Access access = new Access();
    private Students students = new Students();
    private Threads threads = new Threads();
    private Thread thread = new Thread();
>>>>>>> d5851d3 (Initial commit)

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

<<<<<<< HEAD
=======
    public int getModerationResultRetentionDays() {
        return moderationResultRetentionDays;
    }

    public void setModerationResultRetentionDays(int moderationResultRetentionDays) {
        this.moderationResultRetentionDays = moderationResultRetentionDays;
    }

>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
=======
    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Students getStudents() {
        return students;
    }

    public void setStudents(Students students) {
        this.students = students;
    }

    public Threads getThreads() {
        return threads;
    }

    public void setThreads(Threads threads) {
        this.threads = threads;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * How a long thread is rendered. The opening post is always shown, then the
     * newest {@code replyPreviewLimit} replies; the rest are one click away.
     */
    public static class Threads {
        private int replyPreviewLimit = 60;

        public int getReplyPreviewLimit() {
            return replyPreviewLimit;
        }

        public void setReplyPreviewLimit(int replyPreviewLimit) {
            this.replyPreviewLimit = replyPreviewLimit;
        }
    }

    /**
     * Live thread polling. The interval is a property so it can be tuned without
     * a rebuild; whether it runs at all is a staff setting (site_settings key
     * {@code live_poll_enabled}) so moderators can turn it off on demand.
     */
    public static class Thread {
        private int livePollSeconds = 25;

        public int getLivePollSeconds() {
            return livePollSeconds;
        }

        public void setLivePollSeconds(int livePollSeconds) {
            if (livePollSeconds < 5) {
                livePollSeconds = 5;
            }
            this.livePollSeconds = livePollSeconds;
        }
    }

    /**
     * Optional student gate. When {@code loginRequired} is true, only
     * provisioned students (and staff) can use the board. Posts stay anonymous
     * either way - the account is never attached to a post.
     */
    public static class Students {        private boolean loginRequired = true;
        private boolean showLoginLink = true;
        /** When true, signed-out visitors cannot even read the board. */
        private boolean blockReading = false;
        private int maxFailedAttempts = 5;
        private int lockoutMinutes = 10;

        public boolean isLoginRequired() {
            return loginRequired;
        }

        public void setLoginRequired(boolean loginRequired) {
            this.loginRequired = loginRequired;
        }

        public boolean isBlockReading() {
            return blockReading;
        }

        public void setBlockReading(boolean blockReading) {
            this.blockReading = blockReading;
        }

        public boolean isShowLoginLink() {
            return showLoginLink;
        }

        public void setShowLoginLink(boolean showLoginLink) {
            this.showLoginLink = showLoginLink;
        }

        public int getMaxFailedAttempts() {
            return maxFailedAttempts;
        }

        public void setMaxFailedAttempts(int maxFailedAttempts) {
            this.maxFailedAttempts = maxFailedAttempts;
        }

        public int getLockoutMinutes() {
            return lockoutMinutes;
        }

        public void setLockoutMinutes(int lockoutMinutes) {
            this.lockoutMinutes = lockoutMinutes;
        }
    }

    public static class Access {
        /** Development convenience: keep the board writable regardless of access windows. */
        private boolean alwaysOpen = false;
        /** Minutes an admin "open now" override stays active. */
        private int overrideMinutes = 120;

        public boolean isAlwaysOpen() {
            return alwaysOpen;
        }

        public void setAlwaysOpen(boolean alwaysOpen) {
            this.alwaysOpen = alwaysOpen;
        }

        public int getOverrideMinutes() {
            return overrideMinutes;
        }

        public void setOverrideMinutes(int overrideMinutes) {
            this.overrideMinutes = overrideMinutes;
        }
    }

>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        private String failOnMlUnavailable = "QUEUE";
=======
        /**
         * Whether a machine may remove a post on its own. Off by default: the
         * board publishes and flags, and a person decides.
         */
        private boolean autoBlockEnabled = false;
        /**
         * Whether escalation-category posts (a credible safety risk) are hidden
         * from the public board. On by default because that pathway exists for
         * genuine threats to students.
         */
        private boolean hideEscalations = true;

        public boolean isAutoBlockEnabled() {
            return autoBlockEnabled;
        }

        public void setAutoBlockEnabled(boolean autoBlockEnabled) {
            this.autoBlockEnabled = autoBlockEnabled;
        }

        public boolean isHideEscalations() {
            return hideEscalations;
        }

        public void setHideEscalations(boolean hideEscalations) {
            this.hideEscalations = hideEscalations;
        }
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD

        public String getFailOnMlUnavailable() {
            return failOnMlUnavailable;
        }

        public void setFailOnMlUnavailable(String failOnMlUnavailable) {
            this.failOnMlUnavailable = failOnMlUnavailable;
        }
=======
>>>>>>> d5851d3 (Initial commit)
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
