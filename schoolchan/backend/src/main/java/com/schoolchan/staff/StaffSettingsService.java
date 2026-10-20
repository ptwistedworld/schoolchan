package com.schoolchan.staff;

import com.schoolchan.access.AccessWindow;
import com.schoolchan.access.AccessWindowRepository;
import com.schoolchan.board.Board;
import com.schoolchan.board.BoardRepository;
import com.schoolchan.board.SiteSetting;
import com.schoolchan.board.SiteSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

@Service
public class StaffSettingsService {

    private final BoardRepository boardRepository;
    private final AccessWindowRepository accessWindowRepository;
    private final RulesVersionRepository rulesVersionRepository;
    private final SiteSettingRepository siteSettingRepository;

    public StaffSettingsService(
            BoardRepository boardRepository,
            AccessWindowRepository accessWindowRepository,
            RulesVersionRepository rulesVersionRepository,
            SiteSettingRepository siteSettingRepository) {
        this.boardRepository = boardRepository;
        this.accessWindowRepository = accessWindowRepository;
        this.rulesVersionRepository = rulesVersionRepository;
        this.siteSettingRepository = siteSettingRepository;
    }

    @Transactional
    public void updateBoardLimits(UUID boardId, int activeCap, int bumpLimit, int imageLimit) {
        if (bumpLimit < 300 || bumpLimit > 500) {
            throw new IllegalArgumentException("bump limit must be between 300 and 500");
        }
        if (activeCap < 1 || imageLimit < 0) {
            throw new IllegalArgumentException("invalid board limits");
        }
        Board board = boardRepository.findById(boardId).orElseThrow();
        board.setActiveThreadCap(activeCap);
        board.setBumpLimit(bumpLimit);
        board.setImageLimit(imageLimit);
        boardRepository.save(board);
    }

    @Transactional
    public void setClosedMode(String mode) {
        String normalized = "UNAVAILABLE".equalsIgnoreCase(mode) ? "UNAVAILABLE" : "READ_ONLY";
        SiteSetting setting = siteSettingRepository.findById("closed_mode").orElseGet(SiteSetting::new);
        setting.setKey("closed_mode");
        setting.setValue(normalized);
        siteSettingRepository.save(setting);
    }

    @Transactional
    public void saveRules(String body) {
        int next = rulesVersionRepository.findTopByOrderByVersionDesc().map(r -> r.getVersion() + 1).orElse(1);
        RulesVersion version = new RulesVersion();
        version.setId(UUID.randomUUID());
        version.setVersion(next);
        version.setBody(body == null ? "" : body);
        rulesVersionRepository.save(version);
    }

    @Transactional
    public void saveWindow(UUID id, String label, String days, String start, String end, boolean enabled) {
        AccessWindow window = id == null
                ? new AccessWindow()
                : accessWindowRepository.findById(id).orElseGet(AccessWindow::new);
        if (window.getId() == null) {
            window.setId(UUID.randomUUID());
        }
        window.setLabel(label);
        window.setDaysOfWeek(days.toUpperCase().replace(" ", ""));
        window.setStartTime(LocalTime.parse(start));
        window.setEndTime(LocalTime.parse(end));
        window.setEnabled(enabled);
        accessWindowRepository.save(window);
    }
}
