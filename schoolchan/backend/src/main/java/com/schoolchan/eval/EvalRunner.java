package com.schoolchan.eval;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "schoolchan.eval.run", havingValue = "true")
public class EvalRunner implements ApplicationRunner {

    private final EvalService evalService;

    public EvalRunner(EvalService evalService) {
        this.evalService = evalService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path fixture = Path.of("../data/fixtures/eval-sample.jsonl");
        if (!Files.exists(fixture)) {
            fixture = Path.of("data/fixtures/eval-sample.jsonl");
        }
        Map<String, Object> report = evalService.runFixture(fixture);
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path out = Path.of("../eval/results", stamp);
        if (!Files.exists(Path.of("../eval"))) {
            out = Path.of("eval/results", stamp);
        }
        evalService.writeReport(report, out);
        System.out.println("Wrote eval results to " + out.toAbsolutePath());
        System.exit(0);
    }
}
