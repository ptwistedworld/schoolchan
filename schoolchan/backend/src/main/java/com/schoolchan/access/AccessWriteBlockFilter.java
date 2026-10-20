package com.schoolchan.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AccessWriteBlockFilter extends OncePerRequestFilter {

    private final AccessService accessService;

    public AccessWriteBlockFilter(AccessService accessService) {
        this.accessService = accessService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isWrite(request) && !accessService.writesAllowed(isStaffAuthenticated())) {
            if (request.getRequestURI().startsWith("/staff")) {
                filterChain.doFilter(request, response);
                return;
            }
            response.sendRedirect("/closed");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isWrite(HttpServletRequest request) {
        String method = request.getMethod();
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method)
                || HttpMethod.DELETE.matches(method);
    }

    private boolean isStaffAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }
}
