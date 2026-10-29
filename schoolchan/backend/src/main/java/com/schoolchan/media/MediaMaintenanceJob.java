package com.schoolchan.media;

import com.schoolchan.config.SchoolChanProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Keeps the media folder small.
 *
 * <p>Two jobs:
 * <ul>
 *   <li><strong>Shrink</strong> — an image that is bigger than it needs to be
 *       is written again at a sensible size, with a small preview copy beside
 *       it. A phone photo of 4 MB becomes a few hundred kilobytes, and the
 *       board page only ever downloads the small copy.</li>
 *   <li><strong>Tidy</strong> — files that no longer belong to any post are
 *       removed, so a deleted post does not leave its picture behind.</li>
 * </ul>
 */
@Component
public class MediaMaintenanceJob {

    private static final Logger log = LoggerFactory.getLogger(MediaMaintenanceJob.class);

    private final MediaRepository mediaRepository;
    private final SchoolChanProperties properties;

    public MediaMaintenanceJob(MediaRepository mediaRepository, SchoolChanProperties properties) {
        this.mediaRepository = mediaRepository;
        this.properties = properties;
    }

    /**
     * Shrinks a batch of images each night. Runs in small batches so it never
     * holds the disk busy for long.
     */
    @Scheduled(cron = "0 15 4 * * *")
    public void shrinkNightly() {
        int done = shrinkBatch(40);
        if (done > 0) {
            log.info("Shrank {} stored image(s).", done);
        }
    }

    /**
     * Shrinks up to {@code limit} images that are too large or have no
     * preview copy. Returns how many were rewritten.
     */
    @Transactional
    public int shrinkBatch(int limit) {
        Path root = Path.of(properties.getMedia().getRoot());
        if (!Files.isDirectory(root)) {
            return 0;
        }
        List<MediaEntity> all = mediaRepository.findAll();
        int done = 0;
        for (MediaEntity media : all) {
            if (done >= limit) {
                break;
            }
            if (!needsShrinking(root, media)) {
                continue;
            }
            if (shrinkOne(root, media)) {
                done++;
            }
        }
        return done;
    }

    private boolean needsShrinking(Path root, MediaEntity media) {
        if (media.getContentType() == null || !media.getContentType().startsWith("image/")) {
            return false;
        }
        if (media.getThumbnailName() == null) {
            return true;
        }
        Path file = root.resolve(media.getStoredName());
        try {
            return Files.exists(file) && Files.size(file) > 400 * 1024;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean shrinkOne(Path root, MediaEntity media) {
        Path file = root.resolve(media.getStoredName());
        try {
            if (!Files.exists(file)) {
                return false;
            }
            byte[] original = Files.readAllBytes(file);
            ImageResizer.Result shrunk = ImageResizer.shrink(original, media.getContentType());
            byte[] thumb = ImageResizer.thumbnail(shrunk.bytes(), shrunk.contentType());

            if (shrunk.resized()) {
                Files.write(file, shrunk.bytes());
                media.setSizeBytes(shrunk.bytes().length);
                media.setContentType(shrunk.contentType());
                media.setWidth(shrunk.width());
                media.setHeight(shrunk.height());
            }
            if (thumb != null) {
                String thumbName = media.getStoredName() + ".thumb.jpg";
                Files.write(root.resolve(thumbName), thumb);
                media.setThumbnailName(thumbName);
            } else if (media.getThumbnailName() == null) {
                // The image is already small, so the preview can use it directly.
                media.setThumbnailName(null);
            }
            mediaRepository.save(media);
            return true;
        } catch (IOException e) {
            log.warn("Could not shrink {}: {}", media.getStoredName(), e.getMessage());
            return false;
        }
    }

    /** Removes files in the media folder that no post refers to any more. */
    @Scheduled(cron = "0 45 4 * * *")
    public void removeOrphanFiles() {
        int removed = removeOrphans();
        if (removed > 0) {
            log.info("Removed {} orphaned media file(s).", removed);
        }
    }

    @Transactional(readOnly = true)
    public int removeOrphans() {
        Path root = Path.of(properties.getMedia().getRoot());
        if (!Files.isDirectory(root)) {
            return 0;
        }
        java.util.Set<String> known = new java.util.HashSet<>();
        for (MediaEntity m : mediaRepository.findAll()) {
            known.add(m.getStoredName());
            if (m.getThumbnailName() != null) {
                known.add(m.getThumbnailName());
            }
        }
        int removed = 0;
        try (java.util.stream.Stream<Path> files = Files.list(root)) {
            for (Path file : files.toList()) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                if (!known.contains(file.getFileName().toString())) {
                    Files.deleteIfExists(file);
                    removed++;
                }
            }
        } catch (IOException e) {
            log.warn("Could not tidy the media folder: {}", e.getMessage());
        }
        return removed;
    }

    /** How much disk the media folder is using, in bytes. */
    public long mediaBytesOnDisk() {
        Path root = Path.of(properties.getMedia().getRoot());
        if (!Files.isDirectory(root)) {
            return 0;
        }
        try (java.util.stream.Stream<Path> files = Files.list(root)) {
            return files.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    return 0L;
                }
            }).sum();
        } catch (IOException e) {
            return 0;
        }
    }
}
