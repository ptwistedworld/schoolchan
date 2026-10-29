package com.schoolchan.student;

import com.schoolchan.config.SchoolChanProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Student sign-in. The account is a gate for the platform, never an identity
 * attached to a post.
 */
@Controller
@RequestMapping("/student")
public class StudentAuthController {

    private final StudentService studentService;
    private final SchoolChanProperties properties;
    private final StudentLoginThrottle throttle;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public StudentAuthController(StudentService studentService, SchoolChanProperties properties,
                                 StudentLoginThrottle throttle) {
        this.studentService = studentService;
        this.properties = properties;
        this.throttle = throttle;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String next, Model model) {
        model.addAttribute("pageTitle", "Student sign in — SchoolChan");
        model.addAttribute("error", null);
        model.addAttribute("next", safeNext(next));
        return "student/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String studentId,
                              @RequestParam String password,
                              @RequestParam(required = false) String next,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        int max = properties.getStudents().getMaxFailedAttempts();
        long lockout = properties.getStudents().getLockoutMinutes();

        // A locked account is refused even with the right password.
        if (throttle.isLocked(studentId)) {
            model.addAttribute("pageTitle", "Student sign in — SchoolChan");
            model.addAttribute("next", safeNext(next));
            model.addAttribute("formStudentId", studentId);
            model.addAttribute("error", "Too many failed attempts. Try again in "
                    + lockout + " minute" + (lockout == 1 ? "" : "s") + ".");
            return "student/login";
        }

        var authenticated = studentService.authenticate(studentId, password);
        if (authenticated.isEmpty()) {
            boolean locked = throttle.recordFailure(studentId, max, lockout);
            model.addAttribute("pageTitle", "Student sign in — SchoolChan");
            model.addAttribute("next", safeNext(next));
            model.addAttribute("formStudentId", studentId);
            if (locked) {
                model.addAttribute("error", "Too many failed attempts. Try again in "
                        + lockout + " minute" + (lockout == 1 ? "" : "s") + ".");
            } else {
                int left = throttle.remainingAttempts(studentId, max);
                model.addAttribute("error", "That student ID and password did not match."
                        + (left > 0 && left <= 2 ? " " + left + " attempt" + (left == 1 ? "" : "s") + " left." : ""));
            }
            return "student/login";
        }
        throttle.recordSuccess(studentId);
        StudentAccount account = authenticated.get();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
        Authentication auth = new UsernamePasswordAuthenticationToken(
                account.getStudentId(), null, authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        // One identity at a time. Saving the student context replaces whatever
        // was in the session, so a staff account and a student account can
        // never be active in the same browser.
        securityContextRepository.saveContext(context, request, response);
        request.getSession().setAttribute("sc-identity", "student");
        String target = safeNext(next);
        return "redirect:" + target;
    }

    /** Only same-site pages are accepted as a redirect target. */
    static String safeNext(String next) {
        if (next == null || next.isBlank()) {
            return "/";
        }
        // Control characters would break the Location header.
        if (next.chars().anyMatch(c -> c == '\r' || c == '\n')) {
            return "/";
        }
        if (!next.startsWith("/") || next.startsWith("//") || next.startsWith("/\\")) {
            return "/";
        }
        String path = next;
        int query = path.indexOf('?');
        if (query >= 0) {
            path = path.substring(0, query);
        }
        // A write endpoint only answers POST, so a browser that followed it
        // with GET would land on an error page. Go to the board instead.
        if (path.endsWith("/logout") || path.endsWith("/vote") || path.endsWith("/report")
                || path.endsWith("/reply") || path.endsWith("/bookmarks/toggle")
                || path.endsWith("/watch") || path.endsWith("/unwatch")
                || path.endsWith("/bump") || path.endsWith("/delete")
                || path.endsWith("/appeal")) {
            return "/";
        }
        return next;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContext empty = SecurityContextHolder.createEmptyContext();
            securityContextRepository.saveContext(empty, request, response);
        }
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    @GetMapping("/reset")
    public String resetPage(Model model) {
        model.addAttribute("pageTitle", "Reset your password — SchoolChan");
        model.addAttribute("error", null);
        return "student/reset";
    }

    @PostMapping("/reset")
    public String resetSubmit(@RequestParam String studentId,
                              @RequestParam String code,
                              @RequestParam String newPassword,
                              Model model) {
        model.addAttribute("pageTitle", "Reset your password — SchoolChan");
        if (studentService.useResetCode(studentId, code, newPassword)) {
            model.addAttribute("ok", "Your password has been reset. You can sign in now.");
        } else {
            model.addAttribute("error", "That code is not valid or has expired. Ask staff for a new one.");
        }
        return "student/reset";
    }

    @GetMapping("/enroll")
    public String enroll(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Onboard a new student — SchoolChan");
        model.addAttribute("requests", myRequests(currentStudentId(request)));
        return "student/enroll";
    }

    @GetMapping("/password")
    public String passwordPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Change your password — SchoolChan");
        model.addAttribute("error", null);
        return "student/password";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 HttpServletRequest request) {
        String studentId = currentStudentId(request);
        if (studentId == null) {
            return "redirect:/student/login";
        }
        model.addAttribute("pageTitle", "Change your password — SchoolChan");
        if (newPassword == null || newPassword.length() < 8) {
            model.addAttribute("error", "Please use a password of at least 8 characters.");
            return "student/password";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "The two passwords do not match.");
            return "student/password";
        }
        var account = studentService.find(studentId).orElse(null);
        if (account == null) {
            return "redirect:/student/login";
        }
        if (!studentService.authenticate(studentId, currentPassword).isPresent()) {
            model.addAttribute("error", "Your current password is not correct.");
            return "student/password";
        }
        studentService.changePassword(account, newPassword);
        model.addAttribute("ok", "Your password has been changed.");
        return "student/password";
    }

    @PostMapping("/enroll")
    public String enrollSubmit(@RequestParam String studentId,
                               @RequestParam(required = false) String displayName,
                               @RequestParam(required = false) String gradeSection,
                               Model model,
                               HttpServletRequest request) {
        String requester = currentStudentId(request);
        if (requester == null) {
            return "redirect:/student/login";
        }
        if (!studentService.isValidStudentId(studentId)) {
            model.addAttribute("error", "That does not look like a student ID number (for example 2024-12345).");
            model.addAttribute("requests", myRequests(requester));
            return "student/enroll";
        }
        studentService.requestEnrollment(studentId, displayName, gradeSection, requester);
        model.addAttribute("ok", "Thanks. Staff will review this request and provision the account.");
        model.addAttribute("requests", myRequests(requester));
        return "student/enroll";
    }

    private List<StudentEnrollmentRequest> myRequests(String studentId) {
        if (studentId == null) {
            return List.of();
        }
        return studentService.pendingRequests().stream()
                .filter(r -> studentId.equalsIgnoreCase(r.getRequestedBy()))
                .toList();
    }

    private String currentStudentId(HttpServletRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String principal = String.valueOf(auth.getPrincipal());
        if (principal.equals("anonymousUser") || "ROLE_STUDENT".equals(auth.getAuthorities().toString())) {
            return null;
        }
        if (auth.getAuthorities().stream().noneMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()))) {
            return null;
        }
        return principal;
    }
}
