package com.schoolchan.student;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * The student gate.
 *
 * <p>When {@code schoolchan.students.login-required} is true:
 * <ul>
 *   <li>a signed-out visitor may still read the board, but every write
 *       (post, reply, vote, report, bookmark, edit) is sent to the sign-in page;</li>
 *   <li>when {@code block-reading} is also true, reading is sent to sign-in as well.</li>
 * </ul>
 *
 * <p>Only a provisioned student or a staff member can act on the platform.
 * The account is a gate only and is never attached to a post.
 */
@Component
public class StudentGateFilter extends OncePerRequestFilter {

    private final StudentAccessPolicy policy;

    public StudentGateFilter(StudentAccessPolicy policy) {
        this.policy = policy;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (!policy.loginRequired() || isOpen(request) || isAuthenticated()) {
            chain.doFilter(request, response);
            return;
        }
        // A signed-out visitor may read, but may not act on the platform.
        if (isWrite(request) || policy.blockReading()) {
            redirectToLogin(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ctx = request.getContextPath();
        String next = returnPath(request);
        response.sendRedirect(ctx + "/student/login?next=" + java.net.URLEncoder.encode(next, java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * The page to come back to after signing in.
     *
     * <p>A blocked write (a reply, a vote, a report) names an endpoint that
     * only accepts POST. Sending the browser there after sign-in used to land
     * on a "405 / something went wrong" page, so use the page the write came
     * from instead. Only a same-site path is ever returned.
     */
    private String returnPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                String path = java.net.URI.create(referer).getRawPath();
                if (path != null && path.startsWith("/") && !path.startsWith("//")) {
                    return path;
                }
            } catch (IllegalArgumentException ignored) {
                // A malformed Referer is simply ignored.
            }
        }
        return "/";
    }

    private boolean isWrite(HttpServletRequest request) {
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            return true;
        }
        // A signed-out visitor must not be able to flip a vote or a report by
        // following a link either. These are the only GET action endpoints.
        String uri = request.getRequestURI();
        return uri.endsWith("/vote") || uri.endsWith("/report")
                || uri.endsWith("/bookmarks/toggle") || uri.endsWith("/reply");
    }

    private boolean isOpen(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;
        return path.startsWith("/student/")
                || path.startsWith("/staff/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/img/")
                || path.equals("/favicon.ico")
                || path.equals("/robots.txt")
                || path.startsWith("/error")
                || path.equals("/actuator/health")
                || path.equals("/about")
                || path.equals("/rules")
                || path.equals("/login");
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_STUDENT".equals(a.getAuthority())
                        || "ROLE_ADMIN".equals(a.getAuthority())
                        || "ROLE_MODERATOR".equals(a.getAuthority()));
    }
}
