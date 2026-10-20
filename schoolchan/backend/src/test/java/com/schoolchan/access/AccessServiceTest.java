package com.schoolchan.access;

import com.schoolchan.board.SiteSettingRepository;
import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @Mock
    private AccessWindowRepository repository;

    @Mock
    private SiteSettingRepository siteSettingRepository;

    @Test
    void currentState_adminOverride_isOpen() {
        AccessService accessService = new AccessService(repository, siteSettingRepository, new SchoolChanProperties());
        assertEquals(SiteAccessState.OPEN, accessService.currentState(true));
    }

    @Test
    void currentState_outsideWindow_readOnly() {
        SchoolChanProperties props = new SchoolChanProperties();
        props.setClosedMode("READ_ONLY");
        when(siteSettingRepository.findById("closed_mode")).thenReturn(Optional.empty());
        AccessService service = new AccessService(repository, siteSettingRepository, props);

        AccessWindow window = new AccessWindow();
        window.setId(UUID.randomUUID());
        window.setDaysOfWeek("MONDAY");
        window.setStartTime(LocalTime.of(23, 0));
        window.setEndTime(LocalTime.of(23, 30));
        window.setEnabled(true);
        when(repository.findByEnabledTrue()).thenReturn(List.of(window));

        SiteAccessState state = service.currentState(false);
        assertEquals(SiteAccessState.READ_ONLY, state);
    }
}
