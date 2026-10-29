package com.schoolchan.web;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.SiteAccessState;
<<<<<<< HEAD
=======
import com.schoolchan.board.BookmarkService;
import com.schoolchan.session.AnonymousSessionFilter;
>>>>>>> d5851d3 (Initial commit)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PageModelAdvice {

    private final AccessService accessService;
<<<<<<< HEAD

    public PageModelAdvice(AccessService accessService) {
        this.accessService = accessService;
=======
    private final BookmarkService bookmarkService;
    private final com.schoolchan.config.SchoolChanProperties properties;
    private final com.schoolchan.student.StudentAccessPolicy studentPolicy;
    private final com.schoolchan.session.ActiveSessions activeSessions;
    private final LanAddress lanAddress;
    private final org.springframework.core.env.Environment environment;

    public PageModelAdvice(AccessService accessService, BookmarkService bookmarkService,
                           com.schoolchan.config.SchoolChanProperties properties,
                           com.schoolchan.student.StudentAccessPolicy studentPolicy,
                           com.schoolchan.session.ActiveSessions activeSessions,
                           LanAddress lanAddress,
                           org.springframework.core.env.Environment environment) {
        this.accessService = accessService;
        this.bookmarkService = bookmarkService;
        this.properties = properties;
        this.studentPolicy = studentPolicy;
        this.activeSessions = activeSessions;
        this.lanAddress = lanAddress;
        this.environment = environment;
>>>>>>> d5851d3 (Initial commit)
    }

    @ModelAttribute("accessState")
    public SiteAccessState accessState() {
        return accessService.currentState(staff());
    }

    @ModelAttribute("writesAllowed")
    public boolean writesAllowed() {
        return accessService.writesAllowed(staff());
    }

<<<<<<< HEAD
    private boolean staff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(String.valueOf(auth.getPrincipal()));
=======
    /** True when a staff member is signed in: the public board gains staff tools. */
    @ModelAttribute("staffView")
    public boolean staffView() {
        return staff();
    }

    /** Whether to show the student sign-in link. */
    @ModelAttribute("showStudentLogin")
    public boolean showStudentLogin() {
        return properties.getStudents().isShowLoginLink();
    }

    /** True when a signed-out visitor must sign in before they can post. */
    @ModelAttribute("signInToPost")
    public boolean signInToPost() {
        return studentPolicy.loginRequired() && !staff() && studentId() == null;
    }

    /** How many browsers are on the board right now. A number only. */
    @ModelAttribute("activeNow")
    public int activeNow() {
        return activeSessions.activeCount();
    }

    /** The address a phone on the same network can use, or null. */
    @ModelAttribute("lanUrl")
    public String lanUrl() {
        String address = lanAddress.address();
        return address == null ? null : "http://" + address + ":" + port();
    }

    private String port() {
        String configured = environment.getProperty("server.port");
        return (configured == null || configured.isBlank()) ? "8080" : configured;
    }

    /** The signed-in student ID, or null. Used for the student ribbon. */
    @ModelAttribute("studentId")
    public String studentId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        if (auth.getAuthorities().stream().noneMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()))) {
            return null;
        }
        return String.valueOf(auth.getPrincipal());
    }

    @ModelAttribute("bookmarkCount")
    public int bookmarkCount() {
        String sessionHash = currentSessionHash();
        if (sessionHash == null) {
            return 0;
        }
        return (int) bookmarkService.count(sessionHash);
    }

    private String currentSessionHash() {
        var request = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (request instanceof org.springframework.web.context.request.ServletRequestAttributes attrs) {
            Object value = attrs.getRequest().getAttribute(AnonymousSessionFilter.SESSION_HASH_ATTR);
            return value == null ? null : value.toString();
        }
        return null;
    }

    private boolean staff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        // Only a real staff role counts. A signed-in student is NOT staff.
        return auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_MODERATOR".equals(a.getAuthority()));
>>>>>>> d5851d3 (Initial commit)
    }
}
