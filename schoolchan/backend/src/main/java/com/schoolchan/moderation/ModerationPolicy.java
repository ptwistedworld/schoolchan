package com.schoolchan.moderation;

import com.schoolchan.board.SiteSetting;
import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.config.SchoolChanProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The moderation policy, kept in the site settings table so a change survives
 * a restart, falling back to the configured defaults.
 *
 * <p>The policy is deliberately simple: the board publishes everything and
 * flags what looks wrong. A person removes it, never a machine.
 */
@Component
public class ModerationPolicy {

    private static final String AUTO_BLOCK = "moderation_auto_block";
    private static final String HIDE_ESCALATIONS = "moderation_hide_escalations";

    private final SiteSettingRepository siteSettingRepository;
    private final SchoolChanProperties properties;

    public ModerationPolicy(SiteSettingRepository siteSettingRepository, SchoolChanProperties properties) {
        this.siteSettingRepository = siteSettingRepository;
        this.properties = properties;
    }

    /** Whether the filter may remove a post on its own. Off by default. */
    @Transactional(readOnly = true)
    public boolean autoBlockEnabled() {
        return siteSettingRepository.findById(AUTO_BLOCK)
                .map(s -> "true".equalsIgnoreCase(s.getValue()))
                .orElseGet(() -> properties.getModeration().isAutoBlockEnabled());
    }

    /**
     * Whether escalation-category posts (a credible safety risk) are hidden
     * from the public board. On by default.
     */
    @Transactional(readOnly = true)
    public boolean hideEscalations() {
        return siteSettingRepository.findById(HIDE_ESCALATIONS)
                .map(s -> "true".equalsIgnoreCase(s.getValue()))
                .orElseGet(() -> properties.getModeration().isHideEscalations());
    }

    @Transactional
    public void setAutoBlockEnabled(boolean enabled) {
        properties.getModeration().setAutoBlockEnabled(enabled);
        write(AUTO_BLOCK, enabled);
    }

    @Transactional
    public void setHideEscalations(boolean hide) {
        properties.getModeration().setHideEscalations(hide);
        write(HIDE_ESCALATIONS, hide);
    }

    private void write(String key, boolean value) {
        SiteSetting setting = siteSettingRepository.findById(key).orElseGet(SiteSetting::new);
        setting.setKey(key);
        setting.setValue(value ? "true" : "false");
        siteSettingRepository.save(setting);
    }
}
