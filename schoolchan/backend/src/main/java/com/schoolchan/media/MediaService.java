package com.schoolchan.media;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class MediaService {

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/webm");

    private final SchoolChanProperties properties;
    private final MediaRepository mediaRepository;
    private final MediaPhashBlocklistRepository phashBlocklistRepository;

    public MediaService(
            SchoolChanProperties properties,
            MediaRepository mediaRepository,
            MediaPhashBlocklistRepository phashBlocklistRepository) {
        this.properties = properties;
        this.mediaRepository = mediaRepository;
        this.phashBlocklistRepository = phashBlocklistRepository;
    }

    public MediaEntity store(UUID postId, MultipartFile file) throws IOException {
        validateMagic(file);
        String storedName = UUID.randomUUID() + extension(file.getOriginalFilename());
        Path root = Path.of(properties.getMedia().getRoot());
        Files.createDirectories(root);
        Path target = root.resolve(storedName);
        byte[] bytes = ExifStripper.strip(file.getBytes());
        Files.write(target, bytes);

        MediaEntity entity = new MediaEntity();
        entity.setId(UUID.randomUUID());
        entity.setPostId(postId);
        entity.setOriginalFilename(file.getOriginalFilename());
        entity.setStoredName(storedName);
        entity.setContentType(file.getContentType());
        entity.setSizeBytes(bytes.length);
        entity.setQuarantineStatus("QUARANTINED");

        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
            String hash = PhashUtil.dHash(bytes);
            entity.setPhash(hash);
            if (hash != null && phashBlocklistRepository.existsByPhash(hash)) {
                entity.setQuarantineStatus("BLOCKED");
            }
        }
        return mediaRepository.save(entity);
    }

    public void release(UUID mediaId) {
        mediaRepository.findById(mediaId).ifPresent(m -> {
            if (!"BLOCKED".equals(m.getQuarantineStatus())) {
                m.setQuarantineStatus("RELEASED");
                mediaRepository.save(m);
            }
        });
    }

    public java.util.List<MediaEntity> findByPost(UUID postId) {
        return mediaRepository.findByPostId(postId);
    }

    public Optional<Resource> loadReleased(String storedName) throws IOException {
        Optional<MediaEntity> media = mediaRepository.findByStoredName(storedName);
        if (media.isEmpty() || !"RELEASED".equals(media.get().getQuarantineStatus())) {
            return Optional.empty();
        }
        Path path = Path.of(properties.getMedia().getRoot()).resolve(storedName);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            return Optional.empty();
        }
        return Optional.of(resource);
    }

    public Optional<String> contentType(String storedName) {
        return mediaRepository.findByStoredName(storedName).map(MediaEntity::getContentType);
    }

    private void validateMagic(MultipartFile file) throws IOException {
        byte[] head = file.getInputStream().readNBytes(12);
        String type = file.getContentType();
        if (type == null) {
            throw new IOException("missing content type");
        }
        long max = IMAGE_TYPES.contains(type)
                ? properties.getMedia().getMaxImageBytes()
                : properties.getMedia().getMaxVideoBytes();
        if (file.getSize() > max) {
            throw new IOException("file too large");
        }
        if (!looksLike(type, head)) {
            throw new IOException("magic bytes mismatch");
        }
    }

    private boolean looksLike(String contentType, byte[] head) {
        if (head.length < 4) {
            return false;
        }
        if ("image/jpeg".equals(contentType)) {
            return (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8;
        }
        if ("image/png".equals(contentType)) {
            return head[0] == (byte) 0x89 && head[1] == 0x50;
        }
        if ("image/gif".equals(contentType)) {
            return head[0] == 'G' && head[1] == 'I';
        }
        if ("image/webp".equals(contentType)) {
            return head[0] == 'R' && head[1] == 'I';
        }
        if ("video/mp4".equals(contentType)) {
            return containsAscii(head, "ftyp");
        }
        if ("video/webm".equals(contentType)) {
            return (head[0] & 0xFF) == 0x1A && (head[1] & 0xFF) == 0x45;
        }
        return IMAGE_TYPES.contains(contentType) || VIDEO_TYPES.contains(contentType);
    }

    private boolean containsAscii(byte[] data, String needle) {
        String s = new String(data);
        return s.contains(needle);
    }

    private String extension(String name) {
        if (name == null || !name.contains(".")) {
            return ".bin";
        }
        return name.substring(name.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }
}
