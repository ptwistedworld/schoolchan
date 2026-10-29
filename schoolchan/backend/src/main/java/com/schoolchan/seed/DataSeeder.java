package com.schoolchan.seed;

import com.schoolchan.access.AccessWindow;
import com.schoolchan.access.AccessWindowRepository;
import com.schoolchan.board.Board;
import com.schoolchan.board.BoardRepository;
<<<<<<< HEAD
import com.schoolchan.board.Quote;
import com.schoolchan.board.QuoteRepository;
=======
import com.schoolchan.board.BoardService;
import com.schoolchan.board.Post;
import com.schoolchan.board.PostRepository;
import com.schoolchan.board.PostStatus;
import com.schoolchan.board.Quote;
import com.schoolchan.board.QuoteRepository;
import com.schoolchan.board.ThreadEntity;
import com.schoolchan.board.ThreadRepository;
import com.schoolchan.media.MediaEntity;
import com.schoolchan.media.MediaRepository;
>>>>>>> d5851d3 (Initial commit)
import com.schoolchan.config.SchoolChanProperties;
import com.schoolchan.moderation.BlacklistEntry;
import com.schoolchan.moderation.BlacklistEntryRepository;
import com.schoolchan.staff.RulesVersion;
import com.schoolchan.staff.RulesVersionRepository;
<<<<<<< HEAD
=======
import com.schoolchan.translation.TranslationClient;
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
import java.time.LocalTime;
import java.util.UUID;
=======
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;
import javax.imageio.ImageIO;
>>>>>>> d5851d3 (Initial commit)
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

<<<<<<< HEAD
=======
    /**
     * The demo volume: a wide spread of subjects, replies, greentext and quotes.
     * Kept as class fields so a missing thread can be repaired on the next start.
     */
    private static final String[] volumeTopics = {
            "Weekend plans", "Lost phone charger", "Best pizza near school", "Class rep election",
            "Project deadline moved?", "Library book returns", "Who is the new teacher?", "Bike parking rules",
            "Tutoring request (Math)", "Science fair volunteers", "Intramurals tryouts", "School choir signup",
            "Paying fees", "Scholarship forms", "Uniform shop open", "Canteen queue times",
            "Wi-Fi slow again", "Classroom aircon broken", "Guess the teacher", "Favourite snack",
            "Sports roster", "Newspaper club", "Robotics build night", "Debate practice",
            "Math problem of the week", "Book recommendations please", "Anyone selling a calculator",
            "Shoe size exchange", "Lost ID (found)", "Compost drive", "Recycling bin locations",
            "Health check reminders", "Eye check at the clinic", "Dental day", "Flooded hallway",
            "Canteen menu poll", "Spirit week ideas", "Colour run sign-up", "Blood drive",
            "Peer tutoring sign-up", "Study playlist", "Notes for Calculus", "Cell phone rules",
            "Lost house keys", "Guitar amp practice room", "Drawing tablet recommendations",
            "Photoshop alternatives for free", "Track and field roster", "Basketball practice schedule",
            "Choir audition piece", "Camera club meetup", "How to balance a budget", "Study abroad tips",
            "Scholarship essay review", "Shoe size exchange again", "Umbrella left in the court",
            "Board game night idea", "Anime club watchlist", "Running club first run",
            "How to fix a bike chain", "Baking for the fair", "Printing colour posters"
    };
    private static final String[] volumeOpeners = {
            "Has anyone else seen this?", "Quick question for the seniors.",
            "Sharing this for anyone who needs it.", "Not sure where to post this, sorry.",
            "Reminder for everybody.", "Looking for advice.", "This came up in class today.",
            "Posting so it is on record.", "Any tips?", "Update for the group."
    };
    private static final String[] volumeBodies = {
            "The line was out the door by 7am, so go early if you can.",
            "Turns out the office moved to the second floor last term.",
            "Please confirm before you tell the whole group.",
            "I checked the bulletin board and the poster is still up.",
            "Bring an ID and a parent contact form.",
            "It runs for two hours, so plan the rest of the day around it.",
            "Signup closes on Friday at noon.",
            "If you have an extra copy, please leave it in the library.",
            "Free snacks at the back table.",
            "Wear comfortable shoes, the court is slippery.",
            "It was quieter in the morning if you want to try then.",
            "Bring your own water, the fountain is broken again.",
            "You can pay at the office during lunch or after class.",
            "They moved it to the annex, not the main building.",
            "Half the people there were there for the same reason.",
            "Ask your class rep first, they have the list."
    };
    private static final String[] volumeReplies = {
            "Confirmed, same here.", "Thanks for posting!", "Same, mine is gone too.",
            "I'll drop by after class.", "Good to know, thanks.", "Can you send the details?",
            "I went last week, it was fine.", "This is really helpful.",
            "Let me ask in the faculty room.", "Yep, seen it too.",
            "Count me in.", "Any update on this?", "I can help with that if you need it.",
            "Same thing happened to me last year.", "Bringing a friend if that is allowed."
    };
    /** Spread the volume over every board so the whole site looks alive. */
    private static final String[] volumeBoards = {"general", "academics", "events", "random", "tech", "sports",
            "music", "arts", "lunch", "advice", "announcements"};

>>>>>>> d5851d3 (Initial commit)
    private final BoardRepository boardRepository;
    private final QuoteRepository quoteRepository;
    private final BlacklistEntryRepository blacklistEntryRepository;
    private final RulesVersionRepository rulesVersionRepository;
    private final AccessWindowRepository accessWindowRepository;
    private final SchoolChanProperties properties;
    private final ResourceLoader resourceLoader;
<<<<<<< HEAD
=======
    private final BoardService boardService;
    private final ThreadRepository threadRepository;
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final com.schoolchan.student.StudentService studentService;
    private final TranslationClient translationClient;
    private final com.schoolchan.moderation.ModerationResultRepository moderationResultRepository;
    private final com.schoolchan.staff.AuditLogRepository auditLogRepository;
>>>>>>> d5851d3 (Initial commit)

    public DataSeeder(
            BoardRepository boardRepository,
            QuoteRepository quoteRepository,
            BlacklistEntryRepository blacklistEntryRepository,
            RulesVersionRepository rulesVersionRepository,
            AccessWindowRepository accessWindowRepository,
            SchoolChanProperties properties,
<<<<<<< HEAD
            ResourceLoader resourceLoader) {
=======
            ResourceLoader resourceLoader,
            BoardService boardService,
            ThreadRepository threadRepository,
            PostRepository postRepository,
            MediaRepository mediaRepository,
            com.schoolchan.student.StudentService studentService,
            TranslationClient translationClient,
            com.schoolchan.moderation.ModerationResultRepository moderationResultRepository,
            com.schoolchan.staff.AuditLogRepository auditLogRepository) {
>>>>>>> d5851d3 (Initial commit)
        this.boardRepository = boardRepository;
        this.quoteRepository = quoteRepository;
        this.blacklistEntryRepository = blacklistEntryRepository;
        this.rulesVersionRepository = rulesVersionRepository;
        this.accessWindowRepository = accessWindowRepository;
        this.properties = properties;
        this.resourceLoader = resourceLoader;
<<<<<<< HEAD
=======
        this.boardService = boardService;
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
        this.mediaRepository = mediaRepository;
        this.studentService = studentService;
        this.translationClient = translationClient;
        this.moderationResultRepository = moderationResultRepository;
        this.auditLogRepository = auditLogRepository;
>>>>>>> d5851d3 (Initial commit)
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedBoards();
        seedQuotes();
        seedBlacklist();
        seedRules();
        seedAccessWindow();
<<<<<<< HEAD
=======
        seedSampleContent();
        ensurePublishedPosts();
        ensureVolumeThreads();
        backfillFlags();
        ensureSampleImages();
        ensureTranslations();
>>>>>>> d5851d3 (Initial commit)
    }

    private void seedBoards() {
        if (boardRepository.count() > 0) {
            return;
        }
        createBoard("general", "General", "Campus-wide discussion");
        createBoard("academics", "Academics", "Classes, study groups, and exams");
        createBoard("events", "Events", "Clubs, sports, and school events");
<<<<<<< HEAD
=======
        createBoard("random", "Random", "Anything that does not fit anywhere else");
        createBoard("tech", "Tech", "Computer labs, projects, gadgets and IT help");
        createBoard("sports", "Sports", "Training, tournaments and intramurals");
        createBoard("music", "Music", "Choir, band, instruments and playlists");
        createBoard("arts", "Arts", "Drawing, painting, photography and design");
        createBoard("lunch", "Lunch", "Canteen talk, food finds and meal deals");
        createBoard("advice", "Advice", "Ask for help, give advice, share what worked");
        createBoard("announcements", "Announcements", "Notices from staff and student councils");
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        addBlacklist("TOKEN_BLOCK", "CONTAINS", "HARASSMENT", 0.95);
        addBlacklist("TOKEN_ESCALATE_THREAT", "CONTAINS", "THREAT", 0.88);
=======
        // Starter lists per language, kept in data/blacklists/ for review.
        for (String file : new String[] {"seed-en.txt", "seed-tagalog.txt", "seed-cebuano.txt"}) {
            Path path = Path.of("..", "data", "blacklists", file).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                path = Path.of("data", "blacklists", file).toAbsolutePath().normalize();
            }
            if (!Files.exists(path)) {
                continue;
            }
            try {
                for (String line : Files.readAllLines(path)) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    String[] parts = trimmed.split("\\|");
                    if (parts.length < 4) {
                        continue;
                    }
                    addBlacklist(parts[0].trim(), parts[1].trim(), parts[2].trim(), Double.parseDouble(parts[3].trim()));
                }
            } catch (Exception e) {
                log.warn("Could not load blacklist {}: {}", file, e.getMessage());
            }
        }
        if (blacklistEntryRepository.count() == 0) {
            addBlacklist("TOKEN_BLOCK", "CONTAINS", "HARASSMENT", 0.95);
            addBlacklist("TOKEN_ESCALATE_THREAT", "CONTAINS", "THREAT", 0.88);
        }
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
        rules.setBody("""
                # SchoolChan Rules
                1. Be respectful to students and staff.
                2. No sharing of personal information.
                3. Follow campus access hours.
                4. Report concerns using the report button.
                """);
=======
        rules.setBody(com.schoolchan.staff.DefaultRules.BODY);
>>>>>>> d5851d3 (Initial commit)
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
<<<<<<< HEAD
=======

    private void seedSampleContent() {
        if (threadRepository.count() >= 5) {
            log.info("Skipping sample content: {} threads already present.", threadRepository.count());
            return;
        }
        try {
            // General board threads
            Post t1 = boardService.createThread("general", "Welcome to SchoolChan",
                    "Hello everyone! This is the first thread on SchoolChan. Use >greentext and >>123 to quote.", false, false, "seed-s1", "10.0.0.1");
            attachSampleImage(t1.getId());
            boardService.reply("general", t1.getThreadId(), ">greentext works", false, false, "seed-s2", "10.0.0.2");
            boardService.reply("general", t1.getThreadId(), ">>1 Welcome! Glad to be here.", false, false, "seed-s3", "10.0.0.3");
            boardService.reply("general", t1.getThreadId(), "[spoiler]The cake is a lie[/spoiler]", true, true, "seed-s4", "10.0.0.4");

            Post t2 = boardService.createThread("general", "Best study spots on campus",
                    "Where do you usually study? Library? Cafeteria? Rooftop?", false, false, "seed-s5", "10.0.0.5");
            attachSampleImage(t2.getId(), "Study Spots", 0.20f, 0.55f, 0.35f);
            boardService.reply("general", t2.getThreadId(), "Library 2nd floor is quiet.", false, false, "seed-s6", "10.0.0.6");
            boardService.reply("general", t2.getThreadId(), "Cafeteria after 3pm.", false, false, "seed-s7", "10.0.0.7");

            Post t3 = boardService.createThread("general", "Lost: blue umbrella",
                    "Left a blue umbrella in room 204 last Friday. Please return if found.", false, false, "seed-s8", "10.0.0.8");
            attachSampleImage(t3.getId(), "Lost Item", 0.75f, 0.35f, 0.20f);

            // Tagalog / Cebuano entries to exercise translation
            Post tl1 = boardService.createThread("general", "Usapin sa Tagalog",
                    "Magandang umaga sa inyong lahat! Ito ay isang pagsubok ng automatic translation.", false, false, "seed-tl1", "10.0.0.11");
            attachSampleImage(tl1.getId(), "Tagalog", 0.55f, 0.30f, 0.65f);
            boardService.reply("general", tl1.getThreadId(), "Salamat po! Ako'y nakatulong.", true, false, "seed-tl2", "10.0.0.12");
            boardService.reply("general", tl1.getThreadId(), "Ang ganda ng topic na ito.", true, false, "seed-tl3", "10.0.0.13");

            Post tl2 = boardService.createThread("academics", "Pag-aaral ng grupo",
                    "Kumusta kayo? May grupo tayo para mag-aral ng Calculus tuwing Sabado.", false, false, "seed-tl4", "10.0.0.14");
            boardService.reply("academics", tl2.getThreadId(), "Ako'y pupunta, nga una kay kaayo.", true, false, "seed-tl5", "10.0.0.15");

            Post ceb1 = boardService.createThread("events", "Sports day registration",
                    "Unsa kaayo, mga kaibigan! Sugdan nga sa sports day registration.", false, false, "seed-ceb1", "10.0.0.16");
            attachSampleImage(ceb1.getId(), "Sports Day", 0.80f, 0.55f, 0.15f);
            boardService.reply("events", ceb1.getThreadId(), "Maayo kaayo! Ako gikan sa team sa bolleyball.", true, false, "seed-ceb2", "10.0.0.17");

            // blocked/escalated candidates
            boardService.createThread("general", "TOKEN_BLOCK placeholder",
                    "TOKEN_BLOCK placeholder", false, false, "seed-s9", "10.0.0.9");
            boardService.createThread("general", "TOKEN_ESCALATE_THREAT placeholder",
                    "TOKEN_ESCALATE_THREAT placeholder", false, false, "seed-s10", "10.0.0.10");

            // Academics
            Post a1 = boardService.createThread("academics", "Midterm study group",
                    "Anyone up for a study group this weekend? Math and Science.", false, false, "seed-a1", "10.0.1.1");
            boardService.reply("academics", a1.getThreadId(), "Count me in! Saturday 2pm.", false, false, "seed-a2", "10.0.1.2");
            boardService.reply("academics", a1.getThreadId(), "Can we do Sunday instead?", true, false, "seed-a3", "10.0.1.3");

            boardService.createThread("academics", "Calculus: derivatives help",
                    "Struggling with chain rule. Any tips or resources?", false, false, "seed-a4", "10.0.1.4");
            boardService.createThread("academics", "English essay structure",
                    "How do you outline a 500-word essay? Any templates?", false, false, "seed-a5", "10.0.1.5");

            // Events
            boardService.createThread("events", "Intramurals 2026 schedule",
                    "Intramurals kick off next week. Check the bulletin board for brackets.", false, false, "seed-e1", "10.0.2.1");
            boardService.createThread("events", "Club fair this Friday",
                    "All clubs will have booths in the gym. Come and join!", false, false, "seed-e2", "10.0.2.2");
            boardService.createThread("events", "Yearbook photo submission",
                    "Submit your photos by Oct 30. Spoiler: some funny ones already in.", true, false, "seed-e3", "10.0.2.3");

            // votes
            try {
                boardService.vote(t1.getId(), "voter-1", (short) 1);
                boardService.vote(t1.getId(), "voter-2", (short) 1);
                boardService.vote(t2.getId(), "voter-3", (short) -1);
            } catch (Exception ignored) {
            }
            // Publish seeded clean posts so the board is populated even without the ML sidecar.
            for (Post p : postRepository.findAll()) {
                if ((p.getStatus() == PostStatus.HELD_FOR_REVIEW || p.getStatus() == PostStatus.PENDING_SCREENING)
                        && p.getOriginalText() != null && !p.getOriginalText().contains("TOKEN_")) {
                    p.setStatus(PostStatus.PUBLISHED);
                    postRepository.save(p);
                }
            }
            seedBulkContent();
            seedExtraVolume();
            seedStudentAccounts();
            log.info("Seeded sample content.");
        } catch (Exception e) {
            log.warn("Sample content seeding failed: {}", e.getMessage());
        }
    }

    private static final String TINY_PNG_B64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    private void attachSampleImage(UUID postId) {
        attachSampleImage(postId, "Sample", 0.35f, 0.45f, 0.75f);
    }

    private void attachSampleImage(UUID postId, String label, float r, float g, float b) {
        try {
            byte[] png = renderPlaceholderPng(label, r, g, b);
            String storedName = java.util.UUID.randomUUID() + ".png";
            java.nio.file.Path root = java.nio.file.Path.of(properties.getMedia().getRoot());
            java.nio.file.Files.createDirectories(root);
            java.nio.file.Files.write(root.resolve(storedName), png);
            MediaEntity m = new MediaEntity();
            m.setId(java.util.UUID.randomUUID());
            m.setPostId(postId);
            m.setOriginalFilename("sample.png");
            m.setStoredName(storedName);
            m.setContentType("image/png");
            m.setSizeBytes(png.length);
            m.setQuarantineStatus("RELEASED");
            mediaRepository.save(m);
        } catch (Exception e) {
            log.warn("attachSampleImage failed: {}", e.getMessage());
        }
    }

    /** Renders a visible placeholder image (no external image assets needed). */
    private byte[] renderPlaceholderPng(String label, float r, float g, float b) throws IOException {
        int w = 480;
        int h = 320;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(r, g, b), w, h, new Color(
                Math.min(1f, r + 0.25f), Math.min(1f, g + 0.25f), Math.min(1f, b + 0.25f))));
        g2.fillRect(0, 0, w, h);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillOval(w / 2 - 60, h / 2 - 70, 120, 120);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        String text = label == null ? "Sample" : label;
        g2.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2 + 10);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String sub = "SchoolChan sample image";
        g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, h / 2 + 40);
        g2.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private void ensurePublishedPosts() {
        try {
            long published = postRepository.findAll().stream()
                    .filter(p -> p.getStatus() == PostStatus.PUBLISHED).count();
            if (published > 0) return;
            for (Post p : postRepository.findAll()) {
                if ((p.getStatus() == PostStatus.HELD_FOR_REVIEW || p.getStatus() == PostStatus.PENDING_SCREENING)
                        && p.getOriginalText() != null && !p.getOriginalText().contains("TOKEN_")) {
                    p.setStatus(PostStatus.PUBLISHED);
                    postRepository.save(p);
                }
            }
            log.info("Ensured published posts for demo.");
        } catch (Exception e) {
            log.warn("ensurePublishedPosts failed: {}", e.getMessage());
        }
    }

    /**
     * The board used to hold a post when the filter did not like it. Those posts
     * were screened before the "publish, then flag" policy, so they carry no
     * flag. Give them one, using the decision the pipeline already made, so the
     * review queue matches what the filter said at the time.
     */
    private void backfillFlags() {
        try {
            int changed = 0;
            java.util.Set<java.util.UUID> staffBlocked = staffBlockedPostIds();
            for (Post post : postRepository.findAll()) {
                // A staff decision in the audit log wins over anything else.
                if (staffBlocked.contains(post.getId())) {
                    if (post.getStatus() != PostStatus.BLOCKED) {
                        post.setStatus(PostStatus.BLOCKED);
                        changed++;
                    }
                    if (post.isFlagged()) {
                        post.setFlagged(false);
                        post.setFlagReason(null);
                        post.setFlagSource(null);
                        changed++;
                    }
                    postRepository.save(post);
                    continue;
                }
                // The old policy let the machine remove a post. Under the current
                // policy it should be published and flagged instead, unless a
                // person actually removed it.
                if (post.getStatus() == PostStatus.BLOCKED) {
                    post.setStatus(PostStatus.PUBLISHED);
                    postRepository.save(post);
                    changed++;
                }
                // Only a genuine safety escalation stays hidden. Anything the
                // old policy hid for a normal rule break comes back up, flagged.
                if (post.getStatus() == PostStatus.HELD_FOR_REVIEW) {
                    var held = moderationResultRepository
                            .findFirstByPostIdOrderByCreatedAtDesc(post.getId())
                            .orElse(null);
                    boolean escalation = held != null
                            && held.getDecision() == com.schoolchan.moderation.ModerationDecision.ESCALATE;
                    if (!escalation) {
                        post.setStatus(PostStatus.PUBLISHED);
                        postRepository.save(post);
                        changed++;
                    }
                }
                // A post that the author deleted is settled. It should not sit
                // in the review queue.
                if (post.isDeletedByAuthor()) {
                    if (post.isFlagged()) {
                        post.setFlagged(false);
                        post.setFlagReason(null);
                        post.setFlagSource(null);
                        postRepository.save(post);
                        changed++;
                    }
                    continue;
                }
                if (post.isFlagged()) {
                    continue;
                }
                var result = moderationResultRepository
                        .findFirstByPostIdOrderByCreatedAtDesc(post.getId())
                        .orElse(null);
                if (result == null) {
                    continue;
                }
                var decision = result.getDecision();
                if (decision == com.schoolchan.moderation.ModerationDecision.PUBLISH) {
                    continue;
                }
                post.setFlagged(true);
                post.setFlagSource(decision == com.schoolchan.moderation.ModerationDecision.ESCALATE
                        ? "escalation" : "filter");
                post.setFlagReason(result.getReasons());
                postRepository.save(post);
                changed++;
            }
            if (changed > 0) {
                log.info("Backfilled {} flags from earlier moderation decisions.", changed);
            }
        } catch (Exception e) {
            log.warn("backfillFlags failed: {}", e.getMessage());
        }
    }

    /** Posts a staff member removed by hand, which the backfill must not undo. */
    private java.util.Set<java.util.UUID> staffBlockedPostIds() {
        java.util.Set<java.util.UUID> ids = new java.util.HashSet<>();
        for (com.schoolchan.staff.AuditLog entry : auditLogRepository.findAll()) {
            if (entry.getAction() == null || !entry.getAction().startsWith("BLOCK")
                    || entry.getDetail() == null) {
                continue;
            }
            // The detail reads "<post id> by <staff>".
            String first = entry.getDetail().trim().split("\\s+")[0];
            try {
                ids.add(java.util.UUID.fromString(first));
            } catch (IllegalArgumentException ignored) {
                // not a post id (for example a bulk action)
            }
        }
        return ids;
    }

    private void ensureSampleImages() {
        try {
            if (mediaRepository.count() > 0) {
                return;
            }
            // attach a sample image to the first published post without media
            for (Post p : postRepository.findAll()) {
                if (p.getStatus() == PostStatus.PUBLISHED
                        && mediaRepository.findByPostId(p.getId()).isEmpty()) {
                    attachSampleImage(p.getId());
                    log.info("Attached sample image to post {}", p.getId());
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("ensureSampleImages failed: {}", e.getMessage());
        }
    }

    /**
     * Re-runs translation for posts whose stored translation is missing or was
     * detected as English but whose text is Tagalog/Cebuano. Keeps the demo
     * accurate after the sidecar's language heuristics improve.
     */
    private void ensureTranslations() {
        try {
            int refreshed = 0;
            for (Post p : postRepository.findAll()) {
                String text = p.getOriginalText();
                if (text == null || text.isBlank()) {
                    continue;
                }
                boolean looksForeign = text.matches("(?s).*\\b(ang|mga|po|nga|kaayo|maayo|salamat|kumusta|hindi)\\b.*");
                boolean missing = p.getTranslatedText() == null || p.getTranslatedText().isBlank();
                boolean misdetected = looksForeign && "en".equals(p.getDetectedLanguage());
                if (!missing && !misdetected) {
                    continue;
                }
                var result = translationClient.translate(text);
                if (result.isEmpty()) {
                    continue;
                }
                var tr = result.get();
                if (tr.translatedText() == null || tr.translatedText().isBlank()) {
                    continue;
                }
                if (tr.translatedText().equals(text) && !misdetected) {
                    continue;
                }
                p.setTranslatedText(tr.translatedText());
                p.setDetectedLanguage(tr.detectedLanguage());
                p.setTranslationModel(tr.modelId());
                postRepository.save(p);
                refreshed++;
            }
            if (refreshed > 0) {
                log.info("Refreshed translations for {} posts.", refreshed);
            }
        } catch (Exception e) {
            log.warn("ensureTranslations failed: {}", e.getMessage());
        }
    }

    private void seedBulkContent() {
        String[] generalSubjects = {
                "Morning announcements", "Cafeteria menu this week", "Lost ID card",
                "Anyone up for basketball?", "Study tips for finals", "School fair volunteers",
                "Rainy season tips", "Book recommendations", "Club recruitment", "Exam schedule",
                "Where to print?", "Locker issues", "Uniform reminders", "Transportation tips",
                "After-school activities", "Library hours", "Wi-Fi password?", "Parking info",
                "Canteen prices", "New student tips"
        };
        String[] academicsSubjects = {
                "Algebra help", "Science project ideas", "English grammar", "History essay",
                "Programming basics", "Physics formulas", "Chemistry lab", "Biology quiz",
                "Economics notes", "Filipino poem", "Research tips", "Presentation skills",
                "Group work tips", "Time management", "Memory techniques", "Reading list"
        };
        String[] eventsSubjects = {
                "Sports day", "Foundation day", "Art exhibit", "Music night", "Dance workshop",
                "Science fair", "Debate tournament", "Chess club", "Robotics demo",
                "Volunteer drive", "Blood donation", "Tree planting", "Career talk",
                "Alumni homecoming", "Graduation prep", "Open house"
        };
        String[] replies = {
                "Agreed!", "Thanks for sharing.", "Same here.", "Good point.",
                "I'll check that out.", "Count me in.", "Noted.", "Interesting.",
                "Can you elaborate?", "I disagree slightly.", "Great idea.", "Let's do it."
        };
        String[] boards = {"general", "academics", "events"};
        String[][] subjects = {generalSubjects, academicsSubjects, eventsSubjects};
        int sessionCounter = 100;
        for (int b = 0; b < boards.length; b++) {
            for (int i = 0; i < subjects[b].length; i++) {
                try {
                    String subj = subjects[b][i];
                    Post op = boardService.createThread(boards[b], subj,
                            "Discussion about " + subj.toLowerCase() + ". Share your thoughts!",
                            false, i % 5 == 0, "bulk-s" + (sessionCounter++), "10.0.3." + (sessionCounter % 255));
                    int numReplies = 1 + (i % 6);
                    for (int r = 0; r < numReplies; r++) {
                        boolean spoiler = (r == 2 && i % 3 == 0);
                        boardService.reply(boards[b], op.getThreadId(),
                                replies[(i + r) % replies.length] + (spoiler ? " [spoiler]hidden detail[/spoiler]" : ""),
                                r % 4 == 0, spoiler, "bulk-r" + (sessionCounter++), "10.0.4." + (sessionCounter % 255));
                    }
                    if (i % 7 == 0) {
                        try { boardService.vote(op.getId(), "bulk-voter-" + i, (short) 1); } catch (Exception ignored) {}
                    }
                    if (i % 3 == 0) {
                        attachSampleImage(op.getId(), subj.substring(0, Math.min(18, subj.length())),
                                0.25f + (i % 5) * 0.14f, 0.45f, 0.70f - (i % 4) * 0.12f);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        // Publish all clean posts
        for (Post p : postRepository.findAll()) {
            if ((p.getStatus() == PostStatus.HELD_FOR_REVIEW || p.getStatus() == PostStatus.PENDING_SCREENING)
                    && p.getOriginalText() != null && !p.getOriginalText().contains("TOKEN_")) {
                p.setStatus(PostStatus.PUBLISHED);
                postRepository.save(p);
            }
        }
    }

    /**
     * Extra volume so the board looks alive: a wide spread of subjects, replies,
     * greentext and quotes, plus a few pinned announcement threads.
     */
    private void seedExtraVolume() {
        int session = 500;
        for (int i = 0; i < volumeTopics.length; i++) {
            session = seedVolumeTopic(i, volumeBoards, volumeOpeners, volumeBodies, volumeReplies, session);
        }

        // A few Tagalog and Cebuano threads so translation has something to show.
        seedMultilingualThreads(session);
        // Pinned announcements for the catalog view
        pinAnnouncement("announcements", "READ FIRST - board rules and access hours",
                "Welcome to SchoolChan. Read /rules before posting. Reports go to the staff team.");
        pinAnnouncement("announcements", "Pinned: intramurals schedule is out",
                "The full bracket is on the bulletin board outside the gym.");
        pinAnnouncement("academics", "Pinned: study group sign-up sheet",
                "Add your name to the sheet outside the library if you want a study group.");
        pinAnnouncement("lunch", "Pinned: canteen menu this week",
                "New menu every Monday. Tell us what you think of the new soup.");
    }

    /** Tagalog / Cebuano threads so the translation toggle has real content. */
    /**
     * Creates one volume topic. Split out so the same call can both seed the
     * board and repair a thread that has gone missing.
     */
    private int seedVolumeTopic(int i, String[] boards, String[] openers, String[] bodies,
                                String[] replies, int session) {
        String board = boards[i % boards.length];
        try {
            String subject = volumeTopics[i];
            StringBuilder body = new StringBuilder();
            body.append(openers[i % openers.length]).append(" ")
                 .append(bodies[i % bodies.length]);
            if (i % 4 == 0) {
                body.append("\n>also posted on the board outside the library");
            }
            if (i % 5 == 0) {
                body.append("\n>>1 following up on this");
            }
            Post op = boardService.createThread(board, subject, body.toString(),
                    false, i % 6 == 0, "vol-s" + (session++), "10.1.0." + (session % 250));
            if (i % 3 == 0) {
                attachSampleImage(op.getId(), subject.substring(0, Math.min(16, subject.length())),
                        0.25f + (i % 5) * 0.14f, 0.45f, 0.70f - (i % 4) * 0.12f);
            }
            int n = 1 + (i % 6);
            for (int r = 0; r < n; r++) {
                boardService.reply(board, op.getThreadId(), replies[(i + r) % replies.length],
                        r % 3 == 0, false, "vol-r" + (session++), "10.1.1." + (session % 250));
            }
        } catch (Exception ignored) {
        }
        return session;
    }

    /**
     * Brings back any volume thread that has gone missing, so the demo board
     * always looks the way it should. Runs on every start.
     */
    private void ensureVolumeThreads() {
        try {
            java.util.Set<String> present = new java.util.HashSet<>();
            for (ThreadEntity t : threadRepository.findAll()) {
                if (t.getSubject() != null) {
                    present.add(t.getSubject());
                }
            }
            int restored = 0;
            for (int i = 0; i < volumeTopics.length; i++) {
                if (present.contains(volumeTopics[i])) {
                    continue;
                }
                seedVolumeTopic(i, volumeBoards, volumeOpeners, volumeBodies, volumeReplies, 900 + i);
                restored++;
            }
            log.info("Checked {} volume threads, restored {}.", volumeTopics.length, restored);
            if (restored > 0) {
                log.info("Restored {} missing volume thread(s).", restored);
            }
        } catch (Exception e) {
            log.warn("ensureVolumeThreads failed: {}", e.getMessage());
        }
    }

    private void seedMultilingualThreads(int startSession) {
        String[][] items = {
                {"general", "Usapin sa Tagalog",
                 "Magandang umaga sa inyong lahat! Ito ay isang pagsubok ng automatic translation."},
                {"general", "Kainan ng tanghali",
                 "Salamat po sa inyong lahat! Ako'y nakatulong sa paghahanda ng pagkain."},
                {"academics", "Grupo ng pag-aaral",
                 "Kumusta kayo? May grupo tayo para mag-aral ng Calculus tuwing Sabado."},
                {"events", "Pag-apply sa sports day",
                 "Unsa kaayo, mga kaibigan! Sugdan nga sa sports day registration."},
                {"events", "Kanta para sa school",
                 "Maayo kaayo! Ako gikan sa team sa bolleyball. Sa laktaw, kita mn."},
                {"random", "Tubong tanong",
                 "Anong favorite ninyo na pagkain sa kantin? Ako, spaghetti."},
                {"advice", "Payo sa pag-aaral",
                 "Ang ganda ng topic na ito. Salamat sa mga tip ninyo!"},
                {"music", "Kantang paborito",
                 "Ang ganda ng awit na ito! Nakatutu ako ngayon sa plakta."}
        };
        int session = startSession;
        for (int i = 0; i < items.length; i++) {
            try {
                String[] it = items[i];
                Post op = boardService.createThread(it[0], it[1], it[2], false, i % 4 == 0,
                        "ml-s" + (session++), "10.3.0." + (session % 250));
                attachSampleImage(op.getId(), it[1].substring(0, Math.min(14, it[1].length())),
                        0.45f + (i % 3) * 0.15f, 0.35f, 0.65f);
                boardService.reply(it[0], op.getThreadId(),
                        i % 2 == 0 ? "Salamat po! Ito ay makakatulong." : "Tunay nga, maraming salamat sa inyo!",
                        false, false, "ml-r" + (session++), "10.3.1." + (session % 250));
                boardService.reply(it[0], op.getThreadId(),
                        i % 2 == 0 ? "Ako'y sumasagree. Nice one!" : "Same, kaayo kaayo!",
                        true, false, "ml-r" + (session++), "10.3.2." + (session % 250));
            } catch (Exception ignored) {
            }
        }
    }

    private void pinAnnouncement(String slug, String subject, String text) {
        try {
            Post op = boardService.createThread(slug, subject, text, false, false,
                    "pin-s" + subject.hashCode(), "10.2.0.1");
            var thread = threadRepository.findById(op.getThreadId()).orElse(null);
            if (thread != null) {
                thread.setPinned(true);
                threadRepository.save(thread);
            }
        } catch (Exception ignored) {
        }
    }

    /** A few demo student accounts so the sign-in flow can be tried immediately. */
    private void seedStudentAccounts() {
        try {
            if (studentService.all().size() > 0) {
                return;
            }
            studentService.provision("2024-10001", "Demo Student", "11 - Rizal", "seed", "student123");
            studentService.provision("2024-10002", "Demo Student Two", "11 - Bonifacio", "seed", "student123");
            studentService.provision("2023-10003", "Demo Student Three", "12 - Mabini", "seed", "student123");
            log.info("Seeded 3 demo student accounts (password: student123).");
        } catch (Exception e) {
            log.warn("Student account seeding failed: {}", e.getMessage());
        }
    }
>>>>>>> d5851d3 (Initial commit)
}
