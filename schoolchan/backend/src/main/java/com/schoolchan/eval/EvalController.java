package com.schoolchan.eval;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff/api/eval")
@PreAuthorize("hasRole('ADMIN')")
public class EvalController {

    private final EvalService evalService;

    public EvalController(EvalService evalService) {
        this.evalService = evalService;
    }

    @PostMapping("/confusion")
    public Map<String, Object> confusion(@RequestBody EvalRequest request) {
        return evalService.confusionMatrix(request.predicted(), request.actual());
    }

    @PostMapping("/mcnemar")
    public Map<String, Double> mcNemar(@RequestBody McNemarRequest request) {
        return Map.of("chiSquare", evalService.mcNemar(request.b(), request.c()));
    }

    public record EvalRequest(List<String> predicted, List<String> actual) {}

    public record McNemarRequest(int b, int c) {}
}
