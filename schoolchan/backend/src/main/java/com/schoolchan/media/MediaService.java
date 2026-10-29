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
<<<<<<< HEAD
=======
    private static final String THUMB_SUFFIX = ".thumb.jpg";
>>>>>>> d5851d3 (Initial commit)

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
<<<<<<< HEAD
        String storedName = UUID.randomUUID() + extension(file.getOriginalFilename());
        Path root = Path.of(properties.getMedia().getRoot());
        Files.createDirectories(root);
        Path target = root.resolve(storedName);
        byte[] bytes = ExifStripper.strip(file.getBytes());
=======
        Path root = Path.of(properties.getMedia().getRoot());
        Files.createDirectories(root);
        byte[] bytes = ExifStripper.strip(file.getBytes());

        // A phone photo is megabytes; the board only shows it small. Shrink it
        // and keep a tiny copy for the previews.
        ImageResizer.Result shrunk = ImageResizer.shrink(bytes, file.getContentType());
        bytes = shrunk.bytes();
        String contentType = shrunk.contentType();

        // The name must match what the bytes actually are.
        String ext = extensionForContentType(contentType);
        if (".bin".equals(ext)) {
            ext = extension(file.getOriginalFilename());
        }
        String storedName = UUID.randomUUID() + ext;
        Path target = root.resolve(storedName);
>>>>>>> d5851d3 (Initial commit)
        Files.write(target, bytes);

        MediaEntity entity = new MediaEntity();
        entity.setId(UUID.randomUUID());
        entity.setPostId(postId);
        entity.setOriginalFilename(file.getOriginalFilename());
        entity.setStoredName(storedName);
<<<<<<< HEAD
        entity.setContentType(file.getContentType());
        entity.setSizeBytes(bytes.length);
        entity.setQuarantineStatus("QUARANTINED");

        if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
=======
        entity.setContentType(contentType);
        entity.setSizeBytes(bytes.length);
        entity.setWidth(shrunk.width() > 0 ? shrunk.width() : null);
        entity.setHeight(shrunk.height() > 0 ? shrunk.height() : null);
        entity.setQuarantineStatus("QUARANTINED");

        if (contentType != null && contentType.startsWith("image/")) {
            byte[] thumb = ImageResizer.thumbnail(bytes, contentType);
            if (thumb != null) {
                String thumbName = storedName + ".thumb.jpg";
                Files.write(root.resolve(thumbName), thumb);
                entity.setThumbnailName(thumbName);
            }
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
    public Optional<Resource> loadReleased(String storedName) throws IOException {
        Optional<MediaEntity> media = mediaRepository.findByStoredName(storedName);
        if (media.isEmpty() || !"RELEASED".equals(media.get().getQuarantineStatus())) {
            return Optional.empty();
        }
=======
    public Optional<Resource> loadReleased(String name) throws IOException {
        return resolve(name)
                .filter(r -> "RELEASED".equals(r.entity().getQuarantineStatus()))
                .flatMap(r -> {
                    try {
                        Optional<Resource> file = loadFile(r.fileName());
                        if (file.isEmpty() && r.thumbnail()) {
                            // The small copy is missing; the original is still
                            // the same picture, so serve that instead of a
                            // broken image on the board.
                            return loadFile(r.entity().getStoredName());
                        }
                        return file;
                    } catch (IOException ex) {
                        return Optional.empty();
                    }
                });
    }

    /**
     * Loads a file whatever its quarantine state. Staff use this to look at
     * what a student attached before deciding whether it stays.
     */
    public Optional<Resource> loadForStaff(String name) throws IOException {
        Optional<Resolved> resolved = resolve(name);
        if (resolved.isEmpty()) {
            return Optional.empty();
        }
        return loadFile(resolved.get().fileName());
    }

    /**
     * Finds a media row for an original file name or for its
     * {@code .thumb.jpg} copy. The thumbnail name is not a stored name, so it
     * has to map back to the row that owns it.
     */
    private Optional<Resolved> resolve(String name) {
        if (name == null || name.isBlank()
                || name.contains("/") || name.contains("\\") || name.contains("..")) {
            return Optional.empty();
        }
        Optional<MediaEntity> byStored = mediaRepository.findByStoredName(name);
        if (byStored.isPresent()) {
            return Optional.of(new Resolved(byStored.get(), name, false));
        }
        if (name.endsWith(THUMB_SUFFIX)) {
            String original = name.substring(0, name.length() - THUMB_SUFFIX.length());
            return mediaRepository.findByStoredName(original)
                    .filter(m -> name.equals(m.getThumbnailName()))
                    .map(m -> new Resolved(m, name, true));
        }
        return Optional.empty();
    }

    private record Resolved(MediaEntity entity, String fileName, boolean thumbnail) {
    }

    private Optional<Resource> loadFile(String storedName) throws IOException {
>>>>>>> d5851d3 (Initial commit)
        Path path = Path.of(properties.getMedia().getRoot()).resolve(storedName);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            return Optional.empty();
        }
        return Optional.of(resource);
    }

<<<<<<< HEAD
    public Optional<String> contentType(String storedName) {
        return mediaRepository.findByStoredName(storedName).map(MediaEntity::getContentType);
=======
    public Optional<String> contentType(String name) {
        return resolve(name).map(r -> r.thumbnail() ? "image/jpeg" : r.entity().getContentType());
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======

    /** The file extension that matches the content actually stored. */
    private String extensionForContentType(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            default -> ".bin";
        };
    }
>>>>>>> d5851d3 (Initial commit)
}
