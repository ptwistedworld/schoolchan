package com.schoolchan.access;

import com.schoolchan.board.SiteSetting;
import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessService {

    private final AccessWindowRepository repository;
    private final SiteSettingRepository siteSettingRepository;
    private final SchoolChanProperties properties;

    public AccessService(
            AccessWindowRepository repository,
            SiteSettingRepository siteSettingRepository,
            SchoolChanProperties properties) {
        this.repository = repository;
        this.siteSettingRepository = siteSettingRepository;
        this.properties = properties;
    }

    public SiteAccessState currentState(boolean adminOverride) {
        if (adminOverride) {
            return SiteAccessState.OPEN;
        }
<<<<<<< HEAD
=======
        if (properties.getAccess().isAlwaysOpen()) {
            return SiteAccessState.OPEN;
        }
>>>>>>> d5851d3 (Initial commit)
        if (isWithinOpenWindow(nowLocal())) {
            return SiteAccessState.OPEN;
        }
        return "UNAVAILABLE".equalsIgnoreCase(closedMode())
                ? SiteAccessState.UNAVAILABLE
                : SiteAccessState.READ_ONLY;
    }

    public String closedMode() {
        return siteSettingRepository.findById("closed_mode")
                .map(SiteSetting::getValue)
                .orElse(properties.getClosedMode());
    }

    public boolean writesAllowed(boolean adminOverride) {
        SiteAccessState state = currentState(adminOverride);
        return state == SiteAccessState.OPEN;
    }

    public ZonedDateTime nowLocal() {
        return ZonedDateTime.now(ZoneId.of(properties.getTimezone()));
    }

    private boolean isWithinOpenWindow(ZonedDateTime when) {
        var windows = repository.findByEnabledTrue();
        if (windows.isEmpty()) {
            return true;
        }
        DayOfWeek dow = when.getDayOfWeek();
        LocalTime time = when.toLocalTime();
        for (AccessWindow window : windows) {
            Set<DayOfWeek> days = parseDays(window.getDaysOfWeek());
            if (!days.contains(dow)) {
                continue;
            }
            if (window.getStartTime().equals(window.getEndTime())) {
                return true;
            }
            if (window.getStartTime().isBefore(window.getEndTime())) {
                if (!time.isBefore(window.getStartTime()) && time.isBefore(window.getEndTime())) {
                    return true;
                }
            } else {
                if (!time.isBefore(window.getStartTime()) || time.isBefore(window.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<DayOfWeek> parseDays(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }
}
