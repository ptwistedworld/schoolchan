package com.schoolchan.reputation;

import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReputationService reputationService;
    private final PostRepository postRepository;
    private final SchoolChanProperties properties;

    public ReportService(
            ReportRepository reportRepository,
            ReputationService reputationService,
            PostRepository postRepository,
            SchoolChanProperties properties) {
        this.reportRepository = reportRepository;
        this.reputationService = reputationService;
        this.postRepository = postRepository;
        this.properties = properties;
    }

    @Transactional
    public void report(UUID postId, String sessionHash, String reason, String note) {
        if (reportRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return;
        }
        reputationService.getOrCreate(sessionHash);
        Report report = new Report();
        report.setId(UUID.randomUUID());
        report.setPostId(postId);
        report.setSessionHash(sessionHash);
        report.setReason(reason == null ? "OTHER" : reason);
        report.setNote(note);
        report.setStatus("OPEN");
        reportRepository.save(report);

        double weight = reportWeight(postId);
        if (weight >= 2.0) {
            postRepository.findById(postId).ifPresent(p -> {
                if (p.getStatus() == PostStatus.PUBLISHED) {
                    p.setStatus(PostStatus.HELD_FOR_REVIEW);
                    postRepository.save(p);
                }
            });
        }
    }

    @Transactional(readOnly = true)
    public double reportWeight(UUID postId) {
        List<Report> reports = reportRepository.findByPostId(postId);
        double sum = 0;
        for (Report r : reports) {
            if (!"OPEN".equals(r.getStatus()) && !"CONFIRMED".equals(r.getStatus())) {
                continue;
            }
            sum += reputationService.getOrCreate(r.getSessionHash()).getReliability();
        }
        return Math.min(sum, properties.getModeration().getReputationBoostCap() * 20);
    }

    @Transactional
    public void confirm(UUID reportId) {
        reportRepository.findById(reportId).ifPresent(r -> {
            r.setStatus("CONFIRMED");
            reportRepository.save(r);
            reputationService.confirmReport(r.getSessionHash());
        });
    }

    @Transactional
    public void reject(UUID reportId) {
        reportRepository.findById(reportId).ifPresent(r -> {
            r.setStatus("REJECTED");
            reportRepository.save(r);
            reputationService.rejectReport(r.getSessionHash());
        });
    }
}
