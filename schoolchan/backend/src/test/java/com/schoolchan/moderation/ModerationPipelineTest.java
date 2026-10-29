package com.schoolchan.moderation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostStatus;
import com.schoolchan.config.SchoolChanProperties;
import com.schoolchan.reputation.ReputationService;
import com.schoolchan.staff.EscalationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * The board never removes anything on its own: a post that the filter does not
 * like is published and flagged, so a person can look at it.
 */
@ExtendWith(MockitoExtension.class)
class ModerationPipelineTest {

    @Mock
    private ModerationResultRepository resultRepository;

    @Mock
    private EscalationRepository escalationRepository;

    @Mock
    private ReputationService reputationService;

    @Mock
    private com.schoolchan.board.SiteSettingRepository siteSettingRepository;

    private SchoolChanProperties properties;
    private DecisionEngine decisionEngine;
    private ModerationPolicy policy;

    @BeforeEach
    void setUp() {
        properties = new SchoolChanProperties();
        lenient().when(siteSettingRepository.findById(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.empty());
        policy = new ModerationPolicy(siteSettingRepository, properties);
        decisionEngine = new DecisionEngine(properties, policy);
    }

    private ModerationPipeline pipelineWith(ModerationStage stage) {
        return new ModerationPipeline(
                List.of(stage),
                decisionEngine,
                resultRepository,
                escalationRepository,
                reputationService,
                properties,
                policy,
                new ObjectMapper());
    }

    private ModerationStage stage(String name, double score, String reason) {
        return new ModerationStage() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public StageResult evaluate(Post post, String combinedText) {
                return new StageResult(name, score, 3).withReason(reason);
            }
        };
    }

    @Test
    void aFlaggedPostIsPublishedAndFlagged() {
        Post post = newPost();

        PostStatus status = pipelineWith(stage("ml-text", 0.95, "harassment=0.95")).screen(post, "session");

        assertEquals(PostStatus.PUBLISHED, status);
        assertTrue(post.isFlagged());
        assertEquals("filter", post.getFlagSource());
        assertTrue(post.getFlagReason().contains("harassment=0.95"));
    }

    @Test
    void aCleanPostIsPublishedAndNotFlagged() {
        Post post = newPost();

        PostStatus status = pipelineWith(stage("keyword", 0.02, "ok")).screen(post, "session");

        assertEquals(PostStatus.PUBLISHED, status);
        assertFalse(post.isFlagged());
    }

    /** An escalation is the one category that is hidden, and it is flagged too. */
    @Test
    void anEscalationIsHiddenAndFlagged() {
        Post post = newPost();

        PostStatus status = pipelineWith(stage("ml-text", 0.99, "threat=0.99")).screen(post, "session");

        assertEquals(PostStatus.HELD_FOR_REVIEW, status);
        assertTrue(post.isFlagged());
        assertEquals("escalation", post.getFlagSource());
    }

    /** Staff can turn the safety hold off, and then even that is published. */
    @Test
    void anEscalationIsPublishedWhenStaffAllowIt() {
        policy.setHideEscalations(false);
        Post post = newPost();

        PostStatus status = pipelineWith(stage("ml-text", 0.99, "threat=0.99")).screen(post, "session");

        assertEquals(PostStatus.PUBLISHED, status);
        assertTrue(post.isFlagged());
    }

    @Test
    void aMissingFilterNeverHidesAPost() {
        Post post = newPost();
        ModerationStage broken = new ModerationStage() {
            @Override
            public String name() {
                return "ml-text";
            }

            @Override
            public StageResult evaluate(Post p, String combinedText) {
                return new StageResult("ml-text", 0, 1, true);
            }
        };

        PostStatus status = pipelineWith(broken).screen(post, "session");

        assertEquals(PostStatus.PUBLISHED, status);
        assertFalse(post.isFlagged());
    }

    @Test
    void theModerationResultIsAlwaysRecorded() {
        Post post = newPost();
        when(reputationService.reputationBoost("session")).thenReturn(0.0);

        pipelineWith(stage("keyword", 0.02, "ok")).screen(post, "session");

        org.mockito.Mockito.verify(resultRepository).save(org.mockito.ArgumentMatchers.any(ModerationResult.class));
    }

    private Post newPost() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setOriginalText("a test post");
        post.setStatus(PostStatus.PENDING_SCREENING);
        return post;
    }
}
