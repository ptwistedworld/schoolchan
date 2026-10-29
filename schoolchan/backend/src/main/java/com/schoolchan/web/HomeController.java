package com.schoolchan.web;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.SiteAccessState;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.board.QuoteRepository;
<<<<<<< HEAD
=======
import com.schoolchan.board.ThreadRepository;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class HomeController {

    private final BoardRepository boardRepository;
    private final QuoteRepository quoteRepository;
<<<<<<< HEAD
    private final AccessService accessService;

    public HomeController(BoardRepository boardRepository, QuoteRepository quoteRepository, AccessService accessService) {
        this.boardRepository = boardRepository;
        this.quoteRepository = quoteRepository;
=======
    private final ThreadRepository threadRepository;
    private final AccessService accessService;

    public HomeController(BoardRepository boardRepository, QuoteRepository quoteRepository, ThreadRepository threadRepository, AccessService accessService) {
        this.boardRepository = boardRepository;
        this.quoteRepository = quoteRepository;
        this.threadRepository = threadRepository;
>>>>>>> d5851d3 (Initial commit)
        this.accessService = accessService;
    }

    @GetMapping("/")
    public String home(Model model) {
        var quotes = quoteRepository.findAll();
        if (!quotes.isEmpty()) {
            model.addAttribute("quote", quotes.get(ThreadLocalRandom.current().nextInt(quotes.size())));
        }
<<<<<<< HEAD
        model.addAttribute("boards", boardRepository.findAll());
=======
        var boards = boardRepository.findAll();
        model.addAttribute("boards", boards);
        java.util.Map<java.util.UUID, Long> threadCounts = new java.util.HashMap<>();
        for (var b : boards) {
            threadCounts.put(b.getId(), threadRepository.countActiveByBoard(b.getId()));
        }
        model.addAttribute("threadCounts", threadCounts);
>>>>>>> d5851d3 (Initial commit)
        model.addAttribute("accessState", accessService.currentState(false));
        return "home";
    }

<<<<<<< HEAD
=======
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About — SchoolChan");
        model.addAttribute("boards", boardRepository.findAll());
        return "about";
    }

    @GetMapping("/offline")
    public String offline(Model model) {
        model.addAttribute("pageTitle", "Offline — SchoolChan");
        return "offline";
    }

>>>>>>> d5851d3 (Initial commit)
    @GetMapping("/closed")
    public String closed(Model model) {
        SiteAccessState state = accessService.currentState(false);
        model.addAttribute("accessState", state);
        return "closed";
    }
}
