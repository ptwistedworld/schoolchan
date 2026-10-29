package com.schoolchan.web;

import com.schoolchan.moderation.BlacklistEntry;
import com.schoolchan.moderation.BlacklistEntryRepository;
import com.schoolchan.moderation.KeywordStage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Blacklist / keyword management for staff: add, disable, import, export, and
 * a test-a-string tool that shows exactly how the normaliser sees the input.
 */
@Controller
@RequestMapping("/staff/blacklist")
public class BlacklistController {

    private final BlacklistEntryRepository repository;
    private final KeywordStage keywordStage;
    private final com.schoolchan.staff.AuditLogRepository auditLogRepository;

    public BlacklistController(
            BlacklistEntryRepository repository,
            KeywordStage keywordStage,
            com.schoolchan.staff.AuditLogRepository auditLogRepository) {
        this.repository = repository;
        this.keywordStage = keywordStage;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("entries", repository.findAll());
        model.addAttribute("testText", "");
        model.addAttribute("testResults", List.of());
        return "staff/blacklist";
    }

    @PostMapping("/test")
    public String test(@RequestParam String text, Model model) {
        model.addAttribute("entries", repository.findAll());
        model.addAttribute("testText", text);
        model.addAttribute("testResults", keywordStage.test(text));
        return "staff/blacklist";
    }

    @PostMapping("/add")
    public String add(@RequestParam String pattern,
                      @RequestParam(defaultValue = "WORD") String matchType,
                      @RequestParam(defaultValue = "PROFANITY") String category,
                      @RequestParam(defaultValue = "0.6") double severity,
                      @RequestParam(defaultValue = "") String language,
                      @AuthenticationPrincipal UserDetails user) {
        if (pattern == null || pattern.isBlank()) {
            return "redirect:/staff/blacklist";
        }
        BlacklistEntry entry = new BlacklistEntry();
        entry.setId(UUID.randomUUID());
        entry.setPattern(pattern.trim());
        entry.setMatchType(matchType.toUpperCase());
        entry.setCategory(category.toUpperCase());
        entry.setSeverity(Math.max(0, Math.min(1, severity)));
        entry.setEnabled(true);
        entry.setLanguage(language.isBlank() ? null : language.trim());
        repository.save(entry);
        audit("BLACKLIST_ADD", entry.getPattern(), user);
        return "redirect:/staff/blacklist";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam UUID id, @AuthenticationPrincipal UserDetails user) {
        repository.findById(id).ifPresent(e -> {
            e.setEnabled(!e.isEnabled());
            repository.save(e);
            audit("BLACKLIST_TOGGLE", e.getPattern(), user);
        });
        return "redirect:/staff/blacklist";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam UUID id, @AuthenticationPrincipal UserDetails user) {
        repository.findById(id).ifPresent(e -> {
            repository.delete(e);
            audit("BLACKLIST_DELETE", e.getPattern(), user);
        });
        return "redirect:/staff/blacklist";
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam String csv, @AuthenticationPrincipal UserDetails user) {
        int added = 0;
        for (String line : csv.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] parts = trimmed.split("\\|");
            if (parts.length < 2) {
                continue;
            }
            BlacklistEntry entry = new BlacklistEntry();
            entry.setId(UUID.randomUUID());
            entry.setPattern(parts[0].trim());
            entry.setMatchType(parts.length > 1 && !parts[1].isBlank() ? parts[1].trim().toUpperCase() : "WORD");
            entry.setCategory(parts.length > 2 && !parts[2].isBlank() ? parts[2].trim().toUpperCase() : "OTHER");
            entry.setSeverity(parts.length > 3 ? safeSeverity(parts[3]) : 0.6);
            entry.setEnabled(true);
            repository.save(entry);
            added++;
        }
        audit("BLACKLIST_IMPORT", added + " entries", user);
        return "redirect:/staff/blacklist";
    }

    private double safeSeverity(String value) {
        try {
            return Math.max(0, Math.min(1, Double.parseDouble(value.trim())));
        } catch (NumberFormatException e) {
            return 0.6;
        }
    }

    @GetMapping("/export.csv")
    public ResponseEntity<String> exportCsv() {
        StringBuilder sb = new StringBuilder("pattern|match_type|category|severity|enabled\n");
        for (BlacklistEntry e : repository.findAll()) {
            sb.append(e.getPattern()).append('|')
              .append(e.getMatchType()).append('|')
              .append(e.getCategory()).append('|')
              .append(e.getSeverity()).append('|')
              .append(e.isEnabled()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=schoolchan-blacklist.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sb.toString());
    }

    private void audit(String action, String detail, UserDetails user) {
        com.schoolchan.staff.AuditLog log = new com.schoolchan.staff.AuditLog();
        log.setAction(action);
        log.setDetail(detail + (user != null ? " by " + user.getUsername() : ""));
        auditLogRepository.save(log);
    }
}
