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

    public SimilarityStage(TrainingExampleRepository examples) {
        this.examples = examples;
    }

    @Override
    public String name() {
        return "similarity";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.currentTimeMillis();
        List<TrainingExample> known = examples.findByLabel("violating");
        double best = 0;
        for (TrainingExample example : known) {
            best = Math.max(best, TextSimilarity.jaccard(combinedText, example.getTextSnapshot()));
        }
        StageResult result = new StageResult(name(), best, System.currentTimeMillis() - start);
        if (best >= 0.45) {
            result.withReason("similar-to-confirmed=" + String.format("%.2f", best));
        }
        return result;
    }
}
