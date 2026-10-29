package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import com.schoolchan.eval.TrainingExample;
import com.schoolchan.eval.TrainingExampleRepository;
import com.schoolchan.reputation.TextSimilarity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimilarityStage implements ModerationStage {

    private final TrainingExampleRepository examples;
<<<<<<< HEAD

    public SimilarityStage(TrainingExampleRepository examples) {
        this.examples = examples;
=======
    private final com.schoolchan.board.SiteSettingRepository siteSettingRepository;

    public SimilarityStage(TrainingExampleRepository examples,
                           com.schoolchan.board.SiteSettingRepository siteSettingRepository) {
        this.examples = examples;
        this.siteSettingRepository = siteSettingRepository;
>>>>>>> d5851d3 (Initial commit)
    }

    @Override
    public String name() {
        return "similarity";
    }

<<<<<<< HEAD
=======
    /** The similarity threshold above which a post is pushed toward review. */
    private double threshold() {
        return siteSettingRepository.findById("similarity_threshold")
                .map(s -> parse(s.getValue()))
                .orElse(0.45);
    }

    private double parse(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.45;
        }
    }

>>>>>>> d5851d3 (Initial commit)
    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.currentTimeMillis();
        List<TrainingExample> known = examples.findByLabel("violating");
        double best = 0;
        for (TrainingExample example : known) {
            best = Math.max(best, TextSimilarity.jaccard(combinedText, example.getTextSnapshot()));
        }
<<<<<<< HEAD
        StageResult result = new StageResult(name(), best, System.currentTimeMillis() - start);
        if (best >= 0.45) {
=======
        double limit = threshold();
        StageResult result = new StageResult(name(), best, System.currentTimeMillis() - start);
        if (best >= limit) {
>>>>>>> d5851d3 (Initial commit)
            result.withReason("similar-to-confirmed=" + String.format("%.2f", best));
        }
        return result;
    }
}
