package com.schoolchan.student;

import com.schoolchan.board.SiteSetting;
import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads the student-access policy from the site settings table so a change
 * survives a restart, falling back to the configured defaults.
 */
@Component
public class StudentAccessPolicy {

    private static final String LOGIN_REQUIRED = "student_login_required";
    private static final String BLOCK_READING = "student_block_reading";

    private final SiteSettingRepository siteSettingRepository;
    private final SchoolChanProperties properties;

    public StudentAccessPolicy(SiteSettingRepository siteSettingRepository, SchoolChanProperties properties) {
        this.siteSettingRepository = siteSettingRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public boolean loginRequired() {
        return siteSettingRepository.findById(LOGIN_REQUIRED)
                .map(s -> "true".equalsIgnoreCase(s.getValue()))
                .orElseGet(() -> properties.getStudents().isLoginRequired());
    }

    @Transactional(readOnly = true)
    public boolean blockReading() {
        return siteSettingRepository.findById(BLOCK_READING)
                .map(s -> "true".equalsIgnoreCase(s.getValue()))
                .orElseGet(() -> properties.getStudents().isBlockReading());
    }

    @Transactional
    public void setLoginRequired(boolean required) {
        write(LOGIN_REQUIRED, required);
    }

    @Transactional
    public void setBlockReading(boolean block) {
        write(BLOCK_READING, block);
    }

    private void write(String key, boolean value) {
        SiteSetting setting = siteSettingRepository.findById(key).orElseGet(SiteSetting::new);
        setting.setKey(key);
        setting.setValue(value ? "true" : "false");
        siteSettingRepository.save(setting);
    }
}
