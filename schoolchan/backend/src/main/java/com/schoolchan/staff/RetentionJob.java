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
    private final SchoolChanProperties properties;

    public RetentionJob(PosterMetadataRepository metadataRepository, SchoolChanProperties properties) {
        this.metadataRepository = metadataRepository;
        this.properties = properties;
    }

    @Scheduled(cron = "0 30 3 * * *")
    @Transactional
    public void purgeOldMetadata() {
        Instant cutoff = Instant.now().minus(properties.getMetadataRetentionDays(), ChronoUnit.DAYS);
        int deleted = metadataRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Purged {} poster_metadata rows older than {}", deleted, cutoff);
        }
    }
}
