package com.schoolchan.reputation;

import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
<<<<<<< HEAD
import com.schoolchan.board.PostStatus;
=======
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======
    private final com.schoolchan.board.PosterTierService posterTierService;
>>>>>>> d5851d3 (Initial commit)

    public ReportService(
            ReportRepository reportRepository,
            ReputationService reputationService,
            PostRepository postRepository,
<<<<<<< HEAD
            SchoolChanProperties properties) {
=======
            SchoolChanProperties properties,
            com.schoolchan.board.PosterTierService posterTierService) {
>>>>>>> d5851d3 (Initial commit)
        this.reportRepository = reportRepository;
        this.reputationService = reputationService;
        this.postRepository = postRepository;
        this.properties = properties;
<<<<<<< HEAD
    }

    @Transactional
    public void report(UUID postId, String sessionHash, String reason, String note) {
        if (reportRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return;
=======
        this.posterTierService = posterTierService;
    }

    @Transactional
    public boolean report(UUID postId, String sessionHash, String reason, String note) {
        if (reportRepository.findByPostIdAndSessionHash(postId, sessionHash).isPresent()) {
            return false;
>>>>>>> d5851d3 (Initial commit)
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

<<<<<<< HEAD
        double weight = reportWeight(postId);
        if (weight >= 2.0) {
            postRepository.findById(postId).ifPresent(p -> {
                if (p.getStatus() == PostStatus.PUBLISHED) {
                    p.setStatus(PostStatus.HELD_FOR_REVIEW);
                    postRepository.save(p);
                }
            });
        }
=======
        // A student report flags the post for a person to look at. The post
        // stays published: the board does not remove anything by itself.
        postRepository.findById(postId).ifPresent(p -> {
            p.setFlagged(true);
            p.setFlagSource("student");
            String label = "reported by a student: " + (reason == null ? "OTHER" : reason);
            p.setFlagReason(p.getFlagReason() == null ? label : p.getFlagReason() + "; " + label);
            postRepository.save(p);
        });

        // Reports never hide a post. They flag it, and a person decides in the
        // review queue whether it stays or comes down.
        return true;
>>>>>>> d5851d3 (Initial commit)
    }

    @Transactional(readOnly = true)
    public double reportWeight(UUID postId) {
        List<Report> reports = reportRepository.findByPostId(postId);
        double sum = 0;
        for (Report r : reports) {
            if (!"OPEN".equals(r.getStatus()) && !"CONFIRMED".equals(r.getStatus())) {
                continue;
            }
<<<<<<< HEAD
            sum += reputationService.getOrCreate(r.getSessionHash()).getReliability();
=======
            double reliability = reputationService.getOrCreate(r.getSessionHash()).getReliability();
            // A trusted reporter counts for slightly more, but the multiplier is
            // small and the total is capped, so no session can dominate.
            String tier = posterTierService.tierOf(r.getSessionHash());
            double multiplier = switch (tier) {
                case "VETERAN" -> 1.5;
                case "TRUSTED" -> 1.3;
                case "REGULAR" -> 1.1;
                default -> 1.0;
            };
            sum += reliability * multiplier;
>>>>>>> d5851d3 (Initial commit)
        }
        return Math.min(sum, properties.getModeration().getReputationBoostCap() * 20);
    }

    @Transactional
    public void confirm(UUID reportId) {
        reportRepository.findById(reportId).ifPresent(r -> {
            r.setStatus("CONFIRMED");
            reportRepository.save(r);
            reputationService.confirmReport(r.getSessionHash());
<<<<<<< HEAD
=======
            // A confirmed report earns the reporter a small anonymous trust step.
            posterTierService.recordConfirmedReport(r.getSessionHash());
>>>>>>> d5851d3 (Initial commit)
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
