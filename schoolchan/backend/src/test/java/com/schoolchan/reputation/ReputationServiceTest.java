package com.schoolchan.reputation;

import com.schoolchan.config.SchoolChanProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReputationServiceTest {

    @Mock
    private ReputationEntryRepository repository;

    private ReputationService reputationService;

    @org.junit.jupiter.api.BeforeEach
    void init() {
        reputationService = new ReputationService(repository, new SchoolChanProperties());
    }

    @Test
    void shouldAutoHold_whenReliabilityLow() {
        ReputationEntry entry = new ReputationEntry();
        entry.setReliability(0.3);
        when(repository.findBySessionHash("abc")).thenReturn(Optional.of(entry));

        assertTrue(reputationService.shouldAutoHold("abc"));
    }

    @Test
    void confirmReport_raisesReliability() {
        ReputationEntry entry = new ReputationEntry();
        entry.setId(UUID.randomUUID());
        entry.setSessionHash("abc");
        entry.setReliability(1.0);
        when(repository.findBySessionHash("abc")).thenReturn(Optional.of(entry));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        reputationService.confirmReport("abc");

        ArgumentCaptor<ReputationEntry> captor = ArgumentCaptor.forClass(ReputationEntry.class);
        verify(repository).save(captor.capture());
        assertTrue(captor.getValue().getReliability() > 1.0);
    }
}
