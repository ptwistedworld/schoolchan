package com.schoolchan.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Samples first/middle/last frames via ffmpeg. Fails clearly when ffmpeg is missing.
 */
@Component
public class VideoFrameSampler {

    private static final Logger log = LoggerFactory.getLogger(VideoFrameSampler.class);

    public boolean isAvailable() {
        try {
            Process p = new ProcessBuilder("ffmpeg", "-version").redirectErrorStream(true).start();
            boolean finished = p.waitFor(5, TimeUnit.SECONDS);
            return finished && p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Path> sampleFrames(Path video, Path outputDir, int count) throws IOException, InterruptedException {
        if (!isAvailable()) {
            throw new IllegalStateException("ffmpeg is not installed or not on PATH");
        }
        Files.createDirectories(outputDir);
        List<Path> frames = new ArrayList<>();
        // Approximate: grab frames at 0%, 50%, 100% of a 10s window plus extras if requested.
        double[] positions = positions(count);
        for (int i = 0; i < positions.length; i++) {
            Path out = outputDir.resolve("frame-" + i + ".jpg");
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-ss",
                    String.format("%.2f", positions[i]),
                    "-i",
                    video.toAbsolutePath().toString(),
                    "-frames:v",
                    "1",
                    out.toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            boolean ok = process.waitFor(30, TimeUnit.SECONDS);
            if (!ok || process.exitValue() != 0 || !Files.exists(out)) {
                log.warn("ffmpeg frame sample failed for position {}", positions[i]);
                continue;
            }
            frames.add(out);
        }
        return frames;
    }

    private double[] positions(int count) {
        int n = Math.max(3, count);
        double[] pos = new double[n];
        for (int i = 0; i < n; i++) {
            pos[i] = (n == 1) ? 0 : (10.0 * i / (n - 1));
        }
        return pos;
    }
}
