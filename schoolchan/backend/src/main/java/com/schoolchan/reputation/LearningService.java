package com.schoolchan.reputation;

import com.schoolchan.board.Post;
import com.schoolchan.eval.TrainingExample;
import com.schoolchan.eval.TrainingExampleRepository;
import com.schoolchan.moderation.BlacklistEntry;
import com.schoolchan.moderation.BlacklistEntryRepository;
import com.schoolchan.moderation.LearnedBlacklistCandidate;
import com.schoolchan.moderation.LearnedBlacklistCandidateRepository;
import com.schoolchan.staff.MetadataCryptoService;
import com.schoolchan.staff.PosterMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class LearningService {

    private final TrainingExampleRepository trainingExamples;
    private final LearnedBlacklistCandidateRepository candidates;
    private final BlacklistEntryRepository blacklist;
    private final PosterMetadataRepository posterMetadata;
    private final MetadataCryptoService crypto;
    private final ReputationService reputationService;

    public LearningService(
            TrainingExampleRepository trainingExamples,
            LearnedBlacklistCandidateRepository candidates,
            BlacklistEntryRepository blacklist,
            PosterMetadataRepository posterMetadata,
            MetadataCryptoService crypto,
            ReputationService reputationService) {
        this.trainingExamples = trainingExamples;
        this.candidates = candidates;
        this.blacklist = blacklist;
        this.posterMetadata = posterMetadata;
        this.crypto = crypto;
        this.reputationService = reputationService;
    }

    @Transactional
    public void learnFromConfirmation(Post post) {
        TrainingExample example = new TrainingExample();
        example.setId(UUID.randomUUID());
        example.setPostId(post.getId());
        example.setTextSnapshot(post.getOriginalText());
        example.setLabel("violating");
        trainingExamples.save(example);

        for (String token : TextSimilarity.tokens(post.getOriginalText())) {
            if (token.startsWith("token")) {
                continue;
            }
            if (candidates.existsByPatternAndApprovedFalse(token)) {
                continue;
            }
            LearnedBlacklistCandidate candidate = new LearnedBlacklistCandidate();
            candidate.setId(UUID.randomUUID());
            candidate.setPattern(token);
            candidate.setSourcePostId(post.getId());
            candidate.setConfidence(0.6);
            candidate.setApproved(false);
            candidates.save(candidate);
            break;
        }

        posterMetadata.findByPostId(post.getId()).ifPresent(meta -> {
            String session = crypto.decrypt(meta.getSessionHashEncrypted());
            reputationService.recordStrike(session, Instant.now().plus(7, ChronoUnit.DAYS));
        });
    }

    @Transactional
    public void approveCandidate(UUID id) {
        candidates.findById(id).ifPresent(candidate -> {
            candidate.setApproved(true);
            candidates.save(candidate);
            BlacklistEntry entry = new BlacklistEntry();
            entry.setId(UUID.randomUUID());
            entry.setPattern(candidate.getPattern());
            entry.setMatchType("CONTAINS");
            entry.setCategory("LEARNED");
            entry.setSeverity(0.7);
            entry.setEnabled(true);
            blacklist.save(entry);
        });
    }
}
