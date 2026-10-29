package com.schoolchan.staff;

import com.schoolchan.config.SchoolChanProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RetentionJob {

    private static final Logger log = LoggerFactory.getLogger(RetentionJob.class);

    private final PosterMetadataRepository metadataRepository;
<<<<<<< HEAD
    private final SchoolChanProperties properties;

    public RetentionJob(PosterMetadataRepository metadataRepository, SchoolChanProperties properties) {
        this.metadataRepository = metadataRepository;
=======
    private final com.schoolchan.moderation.ModerationResultRepository moderationResultRepository;
    private final SchoolChanProperties properties;

    public RetentionJob(PosterMetadataRepository metadataRepository,
                         com.schoolchan.moderation.ModerationResultRepository moderationResultRepository,
                         SchoolChanProperties properties) {
        this.metadataRepository = metadataRepository;
        this.moderationResultRepository = moderationResultRepository;
>>>>>>> d5851d3 (Initial commit)
        this.properties = properties;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void purgeOldMetadata() {
<<<<<<< HEAD
        Instant cutoff = Instant.now().minus(properties.getMetadataRetentionDays(), ChronoUnit.DAYS);
=======
        purgeOldMetadata(false);
        purgeOldModerationResults();
    }

    /**
     * Trims the moderation log. Every post leaves a row behind, so on a busy
     * board this table is the fastest growing one. The decision and the scores
     * are kept for as long as the school wants them; only the history of
     * repeated screenings of the same post is dropped.
     */
    @Transactional
    public int purgeOldModerationResults() {
        Instant cutoff = Instant.now().minus(properties.getModerationResultRetentionDays(), ChronoUnit.DAYS);
        int deleted = moderationResultRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Purged {} moderation result(s) older than {}", deleted, cutoff);
        }
        return deleted;
    }

    @Transactional
    public int purgeOldMetadata(boolean dryRun) {
        Instant cutoff = Instant.now().minus(properties.getMetadataRetentionDays(), ChronoUnit.DAYS);
        long count = metadataRepository.countOlderThan(cutoff);
        if (dryRun) {
            log.info("DRY RUN: would purge {} poster_metadata rows older than {}", count, cutoff);
            return (int) count;
        }
>>>>>>> d5851d3 (Initial commit)
        int deleted = metadataRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Purged {} poster_metadata rows older than {}", deleted, cutoff);
        }
<<<<<<< HEAD
=======
        return deleted;
>>>>>>> d5851d3 (Initial commit)
    }
}
