package com.schoolchan.web;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.SiteAccessState;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.board.QuoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class HomeController {

    private final BoardRepository boardRepository;
    private final QuoteRepository quoteRepository;
    private final AccessService accessService;

    public HomeController(BoardRepository boardRepository, QuoteRepository quoteRepository, AccessService accessService) {
        this.boardRepository = boardRepository;
        this.quoteRepository = quoteRepository;
        this.accessService = accessService;
    }

    @GetMapping("/")
    public String home(Model model) {
        var quotes = quoteRepository.findAll();
        if (!quotes.isEmpty()) {
            model.addAttribute("quote", quotes.get(ThreadLocalRandom.current().nextInt(quotes.size())));
        }
        model.addAttribute("boards", boardRepository.findAll());
        model.addAttribute("accessState", accessService.currentState(false));
        return "home";
    }

    @GetMapping("/closed")
    public String closed(Model model) {
        SiteAccessState state = accessService.currentState(false);
        model.addAttribute("accessState", state);
        return "closed";
    }
}
