package com.schoolchan.moderation;

import com.schoolchan.board.Post;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkStage implements ModerationStage {

    private static final Pattern URL_PATTERN =
            Pattern.compile("(https?://[^\\s]+|www\\.[^\\s]+)", Pattern.CASE_INSENSITIVE);

    private final LinkBlocklistRepository repository;

    public LinkStage(LinkBlocklistRepository repository) {
        this.repository = repository;
    }

    @Override
    public String name() {
        return "link";
    }

    @Override
    public StageResult evaluate(Post post, String combinedText) {
        long start = System.nanoTime();
        double score = 0.0;
        StageResult result = new StageResult(name(), 0, 0);
        var blocked = repository.findAll();
        Matcher matcher = URL_PATTERN.matcher(combinedText);
        while (matcher.find()) {
            String url = matcher.group(1);
            String host = extractHost(url);
            if (host == null) {
                continue;
            }
            for (LinkBlocklistEntry entry : blocked) {
                if (host.equalsIgnoreCase(entry.getDomain())
                        || host.endsWith("." + entry.getDomain().toLowerCase(Locale.ROOT))) {
                    score = Math.max(score, 0.95);
                    result.withReason("blocked-domain:" + entry.getDomain());
                }
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        StageResult out = new StageResult(name(), score, ms);
        out.getReasons().addAll(result.getReasons());
        return out;
    }

    private String extractHost(String url) {
        try {
            if (url.startsWith("www.")) {
                url = "http://" + url;
            }
            URI uri = URI.create(url);
            return uri.getHost();
        } catch (Exception e) {
            return null;
        }
    }
}
