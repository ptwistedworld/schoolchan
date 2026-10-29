package com.schoolchan.web;

import com.schoolchan.media.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/media/{storedName}")
    public ResponseEntity<Resource> serve(@PathVariable String storedName) throws Exception {
        return mediaService.loadReleased(storedName)
                .map(resource -> {
                    MediaType type = MediaType.APPLICATION_OCTET_STREAM;
                    String raw = mediaService.contentType(storedName).orElse("");
                    if (!raw.isBlank()) {
                        type = MediaType.parseMediaType(raw);
                    }
                    return ResponseEntity.ok()
<<<<<<< HEAD
=======
                            .cacheControl(org.springframework.http.CacheControl.maxAge(30, java.util.concurrent.TimeUnit.DAYS).cachePublic())
>>>>>>> d5851d3 (Initial commit)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                            .contentType(type)
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
