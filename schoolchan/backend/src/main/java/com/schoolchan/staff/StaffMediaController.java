package com.schoolchan.staff;

import com.schoolchan.media.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Serves an attachment to staff, including one that is still in quarantine.
 *
 * <p>A moderator cannot judge a post that has an image without seeing the
 * image, so the review queue links here. Every view is written to the audit
 * log: opening a quarantined file is a sensitive action.
 */
@Controller
@RequestMapping("/staff/media")
public class StaffMediaController {

    private final MediaService mediaService;
    private final AuditLogRepository auditLogRepository;

    public StaffMediaController(MediaService mediaService, AuditLogRepository auditLogRepository) {
        this.mediaService = mediaService;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/{storedName}")
    public ResponseEntity<Resource> serve(@PathVariable String storedName,
                                           @AuthenticationPrincipal UserDetails user) throws Exception {
        return mediaService.loadForStaff(storedName)
                .map(resource -> {
                    MediaType type = MediaType.APPLICATION_OCTET_STREAM;
                    String raw = mediaService.contentType(storedName).orElse("");
                    if (!raw.isBlank()) {
                        type = MediaType.parseMediaType(raw);
                    }
                    audit("MEDIA_VIEW", storedName, user);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                            .contentType(type)
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void audit(String action, String detail, UserDetails user) {
        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setDetail(detail + (user != null ? " by " + user.getUsername() : ""));
        entry.setCreatedAt(java.time.Instant.now());
        auditLogRepository.save(entry);
    }
}
