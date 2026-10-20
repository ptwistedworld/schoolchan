package com.schoolchan.web;

import com.schoolchan.access.AccessService;
import com.schoolchan.access.SiteAccessState;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PageModelAdvice {

    private final AccessService accessService;

    public PageModelAdvice(AccessService accessService) {
        this.accessService = accessService;
    }

    @ModelAttribute("accessState")
    public SiteAccessState accessState() {
        return accessService.currentState(staff());
    }

    @ModelAttribute("writesAllowed")
    public boolean writesAllowed() {
        return accessService.writesAllowed(staff());
    }

    private boolean staff() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(String.valueOf(auth.getPrincipal()));
    }
}
