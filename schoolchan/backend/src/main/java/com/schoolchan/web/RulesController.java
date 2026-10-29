package com.schoolchan.web;

import com.schoolchan.staff.RulesVersionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RulesController {

    private final RulesVersionRepository rulesVersionRepository;

    public RulesController(RulesVersionRepository rulesVersionRepository) {
        this.rulesVersionRepository = rulesVersionRepository;
    }

    @GetMapping("/rules")
    public String rules(Model model) {
<<<<<<< HEAD
        rulesVersionRepository.findTopByOrderByVersionDesc().ifPresent(r -> model.addAttribute("rules", r));
=======
        rulesVersionRepository.findTopByOrderByVersionDesc().ifPresent(r -> {
            model.addAttribute("rules", r);
            model.addAttribute("rulesHtml", RulesText.toHtml(r.getBody()));
        });
>>>>>>> d5851d3 (Initial commit)
        return "rules";
    }
}
