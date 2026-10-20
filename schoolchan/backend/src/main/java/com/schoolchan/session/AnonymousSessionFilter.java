package com.schoolchan.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AnonymousSessionFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "sc_session";
    public static final String SESSION_HASH_ATTR = "scSessionHash";

    private final SessionHashService sessionHashService;

    public AnonymousSessionFilter(SessionHashService sessionHashService) {
        this.sessionHashService = sessionHashService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = readCookie(request).orElseGet(sessionHashService::newSessionToken);
        if (readCookie(request).isEmpty()) {
            Cookie cookie = new Cookie(COOKIE_NAME, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365);
            cookie.setSecure(request.isSecure());
            response.addCookie(cookie);
        }
        request.setAttribute(SESSION_HASH_ATTR, sessionHashService.hashToken(token));
        filterChain.doFilter(request, response);
    }

    private Optional<String> readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank())
                .findFirst();
    }
}
