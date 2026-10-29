package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import com.schoolchan.config.SchoolChanProperties;
import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
import com.schoolchan.media.VideoFrameSampler;
<<<<<<< HEAD
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
=======
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
<<<<<<< HEAD
=======
import java.time.Duration;
>>>>>>> d5851d3 (Initial commit)
import java.util.List;
import java.util.Map;

@Component
public class MediaNsfwStage implements ModerationStage {

    private final SchoolChanProperties properties;
    private final MediaRepository mediaRepository;
    private final VideoFrameSampler frameSampler;
    private final RestClient restClient;

    public MediaNsfwStage(
            SchoolChanProperties properties, MediaRepository mediaRepository, VideoFrameSampler frameSampler) {
        this.properties = properties;
        this.mediaRepository = mediaRepository;
        this.frameSampler = frameSampler;
<<<<<<< HEAD
        this.restClient = RestClient.builder().baseUrl(properties.getMl().getBaseUrl()).build();
=======
        // Force HTTP/1.1: the default JDK client tries an h2c upgrade, which the
        // sidecar mishandles and the multipart body is then read as empty (422).
        var httpClient = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMillis(properties.getMl().getTimeoutMs()))
                .build();
        this.restClient = RestClient.builder()
                .baseUrl(properties.getMl().getBaseUrl())
                .requestFactory(new org.springframework.http.client.JdkClientHttpRequestFactory(httpClient))
                .build();
>>>>>>> d5851d3 (Initial commit)
    }

    @Override
    public String name() {
        return "media-nsfw";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.currentTimeMillis();
        List<MediaEntity> media = mediaRepository.findByPostId(post.getId());
        if (media.isEmpty() || !properties.getMl().isEnabled()) {
            return new StageResult(name(), 0, System.currentTimeMillis() - start);
        }
        double max = 0;
        try {
            for (MediaEntity m : media) {
                if ("BLOCKED".equals(m.getQuarantineStatus())) {
                    max = Math.max(max, 0.99);
                    continue;
                }
                Path file = Path.of(properties.getMedia().getRoot()).resolve(m.getStoredName());
                if (!Files.exists(file)) {
                    continue;
                }
                if (m.getContentType() != null && m.getContentType().startsWith("video/")) {
                    if (!frameSampler.isAvailable()) {
                        return new StageResult(name(), 0, System.currentTimeMillis() - start, true)
                                .withReason("ffmpeg missing");
                    }
                    Path tmp = Files.createTempDirectory("sc-frames");
                    List<Path> frames = frameSampler.sampleFrames(file, tmp, 3);
                    for (Path frame : frames) {
                        max = Math.max(max, classifyImage(frame, m.getStoredName()));
                    }
                } else {
                    max = Math.max(max, classifyImage(file, m.getStoredName()));
                }
            }
            return new StageResult(name(), max, System.currentTimeMillis() - start)
                    .withReason("explicit=" + max);
        } catch (Exception e) {
            return new StageResult(name(), 0, System.currentTimeMillis() - start, true)
                    .withReason("ML_UNAVAILABLE:" + e.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
<<<<<<< HEAD
    private double classifyImage(Path path, String pathHint) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(path)).contentType(MediaType.APPLICATION_OCTET_STREAM);
        Map<String, Object> resp = restClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v1/image/classify").queryParam("path_hint", pathHint).build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
=======
    private double classifyImage(Path path, String pathHint) throws java.io.IOException {
        // The multipart part MUST carry a filename, otherwise the sidecar sees a
        // plain string field instead of an upload and answers HTTP 422. The body
        // is assembled by hand so the Content-Disposition is always correct.
        String boundary = "sc-" + java.util.UUID.randomUUID().toString().replace("-", "");
        String fileName = path.getFileName() == null ? "upload.bin" : path.getFileName().toString();
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        body.write(("--" + boundary + "\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        body.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        body.write("Content-Type: application/octet-stream\r\n\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        body.write(Files.readAllBytes(path));
        body.write(("\r\n--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("multipart/form-data; boundary=" + boundary));
        Map<String, Object> resp = restClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v1/image/classify").queryParam("path_hint", pathHint).build())
                .headers(h -> h.addAll(headers))
                .body(body.toByteArray())
>>>>>>> d5851d3 (Initial commit)
                .retrieve()
                .body(Map.class);
        if (resp == null || resp.get("nsfw_score") == null) {
            return 0;
        }
        return ((Number) resp.get("nsfw_score")).doubleValue();
    }
}
