package com.schoolchan.seed;

import com.schoolchan.access.AccessWindow;
import com.schoolchan.access.AccessWindowRepository;
import com.schoolchan.board.Board;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.board.Quote;
import com.schoolchan.board.QuoteRepository;
import com.schoolchan.config.SchoolChanProperties;
import com.schoolchan.moderation.BlacklistEntry;
import com.schoolchan.moderation.BlacklistEntryRepository;
import com.schoolchan.staff.RulesVersion;
import com.schoolchan.staff.RulesVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.UUID;
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final BoardRepository boardRepository;
    private final QuoteRepository quoteRepository;
    private final BlacklistEntryRepository blacklistEntryRepository;
    private final RulesVersionRepository rulesVersionRepository;
    private final AccessWindowRepository accessWindowRepository;
    private final SchoolChanProperties properties;
    private final ResourceLoader resourceLoader;

    public DataSeeder(
            BoardRepository boardRepository,
            QuoteRepository quoteRepository,
            BlacklistEntryRepository blacklistEntryRepository,
            RulesVersionRepository rulesVersionRepository,
            AccessWindowRepository accessWindowRepository,
            SchoolChanProperties properties,
            ResourceLoader resourceLoader) {
        this.boardRepository = boardRepository;
        this.quoteRepository = quoteRepository;
        this.blacklistEntryRepository = blacklistEntryRepository;
        this.rulesVersionRepository = rulesVersionRepository;
        this.accessWindowRepository = accessWindowRepository;
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedBoards();
        seedQuotes();
        seedBlacklist();
        seedRules();
        seedAccessWindow();
    }

    private void seedBoards() {
        if (boardRepository.count() > 0) {
            return;
        }
        createBoard("general", "General", "Campus-wide discussion");
        createBoard("academics", "Academics", "Classes, study groups, and exams");
        createBoard("events", "Events", "Clubs, sports, and school events");
    }

    private void createBoard(String slug, String name, String description) {
        Board board = new Board();
        board.setId(UUID.randomUUID());
        board.setSlug(slug);
        board.setName(name);
        board.setDescription(description);
        board.setActiveThreadCap(properties.getBoardDefaults().getActiveThreadCap());
        board.setBumpLimit(properties.getBoardDefaults().getBumpLimit());
        board.setImageLimit(properties.getBoardDefaults().getImageLimit());
        boardRepository.save(board);
    }

    private void seedQuotes() throws Exception {
        if (quoteRepository.count() > 0) {
            return;
        }
        Path filePath = Path.of("..", "data", "quotes", "seed.txt").toAbsolutePath().normalize();
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                reader.lines().filter(l -> !l.isBlank()).forEach(line -> saveQuote(line.trim(), null));
            }
            return;
        }
        Resource resource = resourceLoader.getResource("classpath:quotes-fallback.txt");
        if (resource.exists()) {
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().filter(l -> !l.isBlank()).forEach(line -> saveQuote(line.trim(), null));
            }
            return;
        }
        saveQuote("Learning never exhausts the mind.", "Leonardo da Vinci");
        saveQuote("Discipline is the bridge between goals and accomplishment.", null);
    }

    private void saveQuote(String body, String attribution) {
        Quote quote = new Quote();
        quote.setId(UUID.randomUUID());
        quote.setBody(body);
        quote.setAttribution(attribution);
        quoteRepository.save(quote);
    }

    private void seedBlacklist() {
        if (blacklistEntryRepository.count() > 0) {
            return;
        }
        addBlacklist("TOKEN_BLOCK", "CONTAINS", "HARASSMENT", 0.95);
        addBlacklist("TOKEN_ESCALATE_THREAT", "CONTAINS", "THREAT", 0.88);
    }

    private void addBlacklist(String pattern, String matchType, String category, double severity) {
        BlacklistEntry entry = new BlacklistEntry();
        entry.setId(UUID.randomUUID());
        entry.setPattern(pattern);
        entry.setMatchType(matchType);
        entry.setCategory(category);
        entry.setSeverity(severity);
        entry.setEnabled(true);
        blacklistEntryRepository.save(entry);
    }

    private void seedRules() {
        if (rulesVersionRepository.count() > 0) {
            return;
        }
        RulesVersion rules = new RulesVersion();
        rules.setId(UUID.randomUUID());
        rules.setVersion(1);
        rules.setBody("""
                # SchoolChan Rules
                1. Be respectful to students and staff.
                2. No sharing of personal information.
                3. Follow campus access hours.
                4. Report concerns using the report button.
                """);
        rulesVersionRepository.save(rules);
    }

    private void seedAccessWindow() {
        if (accessWindowRepository.count() > 0) {
            return;
        }
        AccessWindow window = new AccessWindow();
        window.setId(UUID.randomUUID());
        window.setLabel("Weekday campus hours");
        window.setDaysOfWeek("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY");
        window.setStartTime(LocalTime.of(7, 0));
        window.setEndTime(LocalTime.of(22, 0));
        window.setEnabled(true);
        accessWindowRepository.save(window);
    }
}
