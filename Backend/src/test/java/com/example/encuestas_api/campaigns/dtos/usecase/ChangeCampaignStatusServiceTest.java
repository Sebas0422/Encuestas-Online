package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.in.ChangeCampaignStatusUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.ChangeCampaignStatusService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.exception.InvalidCampaignStatusTransitionException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeCampaignStatusService Tests")
class ChangeCampaignStatusServiceTest {

    @Mock
    private LoadCampaignPort loadPort;

    @Mock
    private SaveCampaignPort savePort;

    @Mock
    private Clock clock;

    @InjectMocks
    private ChangeCampaignStatusService changeCampaignStatusService;

    private final Instant fixedInstant = Instant.parse("2024-01-01T10:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private final Long campaignId = 1L;
    private Campaign testCampaign;

    @BeforeEach
    void setUp() {
        testCampaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Test Description",
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(30),
            CampaignStatus.draft,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
    }

    @Test
    @DisplayName("Should change campaign status from draft to active successfully")
    void shouldChangeCampaignStatusFromDraftToActiveSuccessfully() {
        // Given
        CampaignStatus targetStatus = CampaignStatus.active;
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeStatus(targetStatus, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(targetStatus);
        assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant);
        assertThat(result.getId()).isEqualTo(campaignId);
        assertThat(result.getName()).isEqualTo(testCampaign.getName());

        verify(loadPort).loadById(campaignId);
        verify(clock).instant();
        verify(savePort).save(any(Campaign.class));
    }

    @ParameterizedTest
    @MethodSource("validStatusTransitions")
    @DisplayName("Should allow valid status transitions")
    void shouldAllowValidStatusTransitions(CampaignStatus currentStatus, CampaignStatus targetStatus) {
        // Given
        Campaign campaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Description",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            currentStatus,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
        
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = campaign.changeStatus(targetStatus, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then
        assertThat(result.getStatus()).isEqualTo(targetStatus);
        assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant);
    }

    private static Stream<Arguments> validStatusTransitions() {
        return Stream.of(
            Arguments.of(CampaignStatus.draft, CampaignStatus.active),
            Arguments.of(CampaignStatus.draft, CampaignStatus.archived),
            Arguments.of(CampaignStatus.active, CampaignStatus.closed),
            Arguments.of(CampaignStatus.active, CampaignStatus.archived),
            Arguments.of(CampaignStatus.closed, CampaignStatus.archived)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidStatusTransitions")
    @DisplayName("Should reject invalid status transitions")
    void shouldRejectInvalidStatusTransitions(CampaignStatus currentStatus, CampaignStatus targetStatus) {
        // Given
        Campaign campaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Description",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            currentStatus,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
        
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaign));
        when(clock.instant()).thenReturn(fixedInstant);

        // When & Then
        assertThatThrownBy(() -> changeCampaignStatusService.handle(campaignId, targetStatus))
            .isInstanceOf(InvalidCampaignStatusTransitionException.class)
            .hasMessageContaining("Transición de estado inválida");

        verify(loadPort).loadById(campaignId);
        verify(clock).instant();
        verifyNoInteractions(savePort);
    }

    private static Stream<Arguments> invalidStatusTransitions() {
        return Stream.of(
            Arguments.of(CampaignStatus.draft, CampaignStatus.closed),
            Arguments.of(CampaignStatus.active, CampaignStatus.draft),
            Arguments.of(CampaignStatus.closed, CampaignStatus.draft),
            Arguments.of(CampaignStatus.closed, CampaignStatus.active),
            Arguments.of(CampaignStatus.archived, CampaignStatus.draft),
            Arguments.of(CampaignStatus.archived, CampaignStatus.active),
            Arguments.of(CampaignStatus.archived, CampaignStatus.closed)
        );
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundExceptionWhenCampaignDoesNotExist() {
        // Given
        when(loadPort.loadById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> changeCampaignStatusService.handle(campaignId, CampaignStatus.active))
            .isInstanceOf(CampaignNotFoundException.class)
            .hasMessageContaining("no encontrada");

        verify(loadPort).loadById(campaignId);
        verifyNoInteractions(clock, savePort);
    }

    @Test
    @DisplayName("Should not change status when already in target status")
    void shouldNotChangeStatusWhenAlreadyInTargetStatus() {
        // Given - Campaign already in active status
        Campaign activeCampaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Active Campaign"),
            "Description",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            CampaignStatus.active,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
        
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(activeCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        // When changing to same status, the same instance should be returned
        when(savePort.save(any(Campaign.class))).thenReturn(activeCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, CampaignStatus.active);

        // Then - Should save anyway (even if status didn't change, updatedAt might change)
        assertThat(result).isSameAs(activeCampaign);
        verify(savePort).save(any(Campaign.class));
    }

    @Test
    @DisplayName("Should preserve other campaign attributes when changing status")
    void shouldPreserveOtherCampaignAttributesWhenChangingStatus() {
        // Given
        CampaignStatus targetStatus = CampaignStatus.active;
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeStatus(targetStatus, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then
        assertThat(result.getId()).isEqualTo(testCampaign.getId());
        assertThat(result.getName()).isEqualTo(testCampaign.getName());
        assertThat(result.getDescription()).isEqualTo(testCampaign.getDescription());
        assertThat(result.getStartDate()).isEqualTo(testCampaign.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(testCampaign.getEndDate());
        assertThat(result.getCreatedAt()).isEqualTo(testCampaign.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant); // Updated
        assertThat(result.getStatus()).isEqualTo(targetStatus); // Changed
    }

    @Test
    @DisplayName("Should use current timestamp from clock")
    void shouldUseCurrentTimestampFromClock() {
        // Given
        CampaignStatus targetStatus = CampaignStatus.active;
        Instant now = Instant.now();
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(now);
        
        Campaign updatedCampaign = testCampaign.changeStatus(targetStatus, now);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then
        assertThat(result.getUpdatedAt()).isEqualTo(now);
        verify(clock).instant();
    }

    @Test
    @DisplayName("Should capture the exact campaign being saved")
    void shouldCaptureTheExactCampaignBeingSaved() {
        // Given
        CampaignStatus targetStatus = CampaignStatus.active;
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);
        Campaign savedCampaign = testCampaign.changeStatus(targetStatus, fixedInstant);
        when(savePort.save(campaignCaptor.capture())).thenReturn(savedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then
        Campaign capturedCampaign = campaignCaptor.getValue();
        assertThat(capturedCampaign).isNotNull();
        assertThat(capturedCampaign.getId()).isEqualTo(campaignId);
        assertThat(capturedCampaign.getStatus()).isEqualTo(targetStatus);
        assertThat(capturedCampaign.getUpdatedAt()).isEqualTo(fixedInstant);
        assertThat(result).isEqualTo(savedCampaign);
    }

    @Test
    @DisplayName("Should verify transactional annotation")
    void shouldVerifyTransactionalAnnotation() {
        // This test verifies the service has proper transactional annotation
        var annotation = ChangeCampaignStatusService.class
            .getAnnotation(org.springframework.transaction.annotation.Transactional.class);
        
        assertThat(annotation).isNotNull();
    }

    @Test
    @DisplayName("Should verify service annotation")
    void shouldVerifyServiceAnnotation() {
        // This test verifies the service has proper Spring annotation
        var annotation = ChangeCampaignStatusService.class
            .getAnnotation(org.springframework.stereotype.Service.class);
        
        assertThat(annotation).isNotNull();
    }

    @Test
    @DisplayName("Should verify interface implementation")
    void shouldVerifyInterfaceImplementation() {
        // This test verifies the service implements the correct interface
        assertThat(changeCampaignStatusService)
            .isInstanceOf(ChangeCampaignStatusUseCase.class);
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should handle archived status as terminal state")
        void shouldHandleArchivedStatusAsTerminalState() {
            // Given - Campaign already archived
            Campaign archivedCampaign = Campaign.rehydrate(
                campaignId,
                CampaignName.of("Archived Campaign"),
                "Description",
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1),
                CampaignStatus.archived,
                fixedInstant.minusSeconds(7200),
                fixedInstant.minusSeconds(3600)
            );
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(archivedCampaign));
            when(clock.instant()).thenReturn(fixedInstant);

            // When & Then - Cannot change from archived to any other status
            assertThatThrownBy(() -> changeCampaignStatusService.handle(campaignId, CampaignStatus.active))
                .isInstanceOf(InvalidCampaignStatusTransitionException.class);
        }

        @Test
        @DisplayName("Should handle null target status")
        void shouldHandleNullTargetStatus() {
            // Given
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);

            // When & Then - InvalidCampaignStatusTransitionException cuando el estado es null
            assertThatThrownBy(() -> changeCampaignStatusService.handle(campaignId, null))
                .isInstanceOf(InvalidCampaignStatusTransitionException.class)
                .hasMessageContaining("Transición de estado inválida");
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            // When & Then
            assertThatThrownBy(() -> changeCampaignStatusService.handle(null, CampaignStatus.active))
                .isInstanceOf(CampaignNotFoundException.class);
        }

        @Test
        @DisplayName("Should handle campaign with null dates")
        void shouldHandleCampaignWithNullDates() {
            // Given
            Campaign campaignWithNullDates = Campaign.rehydrate(
                campaignId,
                CampaignName.of("Test"),
                "Description",
                null,
                null,
                CampaignStatus.draft,
                fixedInstant,
                fixedInstant
            );
            
            CampaignStatus targetStatus = CampaignStatus.active;
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaignWithNullDates));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = campaignWithNullDates.changeStatus(targetStatus, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

            // Then
            assertThat(result.getStatus()).isEqualTo(targetStatus);
            assertThat(result.getStartDate()).isNull();
            assertThat(result.getEndDate()).isNull();
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {
        
        @Test
        @DisplayName("Should maintain data integrity through multiple status changes")
        void shouldMaintainDataIntegrityThroughMultipleStatusChanges() {
            // Test a complete lifecycle: draft -> active -> closed -> archived
            
            // 1. draft -> active
            Campaign draftCampaign = testCampaign;
            CampaignStatus activeStatus = CampaignStatus.active;
            Instant time1 = fixedInstant;
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(draftCampaign));
            when(clock.instant()).thenReturn(time1);
            
            Campaign activeCampaign = draftCampaign.changeStatus(activeStatus, time1);
            when(savePort.save(any(Campaign.class))).thenReturn(activeCampaign);
            
            Campaign result1 = changeCampaignStatusService.handle(campaignId, activeStatus);
            assertThat(result1.getStatus()).isEqualTo(activeStatus);
            assertThat(result1.getUpdatedAt()).isEqualTo(time1);
            
            // Reset mocks for next step
            reset(loadPort, clock, savePort);
            
            // 2. active -> closed
            CampaignStatus closedStatus = CampaignStatus.closed;
            Instant time2 = time1.plusSeconds(100);
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(activeCampaign));
            when(clock.instant()).thenReturn(time2);
            
            Campaign closedCampaign = activeCampaign.changeStatus(closedStatus, time2);
            when(savePort.save(any(Campaign.class))).thenReturn(closedCampaign);
            
            Campaign result2 = changeCampaignStatusService.handle(campaignId, closedStatus);
            assertThat(result2.getStatus()).isEqualTo(closedStatus);
            assertThat(result2.getUpdatedAt()).isEqualTo(time2);
            
            // Reset mocks for final step
            reset(loadPort, clock, savePort);
            
            // 3. closed -> archived
            CampaignStatus archivedStatus = CampaignStatus.archived;
            Instant time3 = time2.plusSeconds(100);
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(closedCampaign));
            when(clock.instant()).thenReturn(time3);
            
            Campaign archivedCampaign = closedCampaign.changeStatus(archivedStatus, time3);
            when(savePort.save(any(Campaign.class))).thenReturn(archivedCampaign);
            
            Campaign result3 = changeCampaignStatusService.handle(campaignId, archivedStatus);
            assertThat(result3.getStatus()).isEqualTo(archivedStatus);
            assertThat(result3.getUpdatedAt()).isEqualTo(time3);
            
            // Verify all attributes preserved through lifecycle
            assertThat(result3.getId()).isEqualTo(campaignId);
            assertThat(result3.getName()).isEqualTo(draftCampaign.getName());
            assertThat(result3.getDescription()).isEqualTo(draftCampaign.getDescription());
            assertThat(result3.getCreatedAt()).isEqualTo(draftCampaign.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle save failure")
        void shouldHandleSaveFailure() {
            // Given
            CampaignStatus targetStatus = CampaignStatus.active;
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            RuntimeException saveException = new RuntimeException("Database error");
            when(savePort.save(any(Campaign.class))).thenThrow(saveException);

            // When & Then
            assertThatThrownBy(() -> changeCampaignStatusService.handle(campaignId, targetStatus))
                .isEqualTo(saveException);

            // Verify all steps were called
            verify(loadPort).loadById(campaignId);
            verify(clock).instant();
            verify(savePort).save(any(Campaign.class));
        }

        @Test
        @DisplayName("Should not modify original campaign instance")
        void shouldNotModifyOriginalCampaignInstance() {
            // Given
            CampaignStatus originalStatus = testCampaign.getStatus();
            CampaignStatus targetStatus = CampaignStatus.active;
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = testCampaign.changeStatus(targetStatus, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            changeCampaignStatusService.handle(campaignId, targetStatus);

            // Then - Original campaign should remain unchanged
            assertThat(testCampaign.getStatus()).isEqualTo(originalStatus);
            assertThat(testCampaign.getUpdatedAt()).isNotEqualTo(fixedInstant);
        }
    }

    @ParameterizedTest
    @EnumSource(CampaignStatus.class)
    @DisplayName("Should handle all status types as current status")
    void shouldHandleAllStatusTypesAsCurrentStatus(CampaignStatus currentStatus) {
        // Given
        Campaign campaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Description",
            LocalDate.now(),
            LocalDate.now().plusDays(30),
            currentStatus,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
        
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = campaign.changeStatus(currentStatus, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, currentStatus);

        // Then
        assertThat(result.getStatus()).isEqualTo(currentStatus);
    }

    @Test
    @DisplayName("Should verify method signature matches interface")
    void shouldVerifyMethodSignatureMatchesInterface() {
        // Verify the method exists with correct parameters
        var methods = changeCampaignStatusService.getClass().getDeclaredMethods();
        
        assertThat(methods)
            .anyMatch(method -> method.getName().equals("handle") 
                && method.getParameterCount() == 2
                && method.getParameterTypes()[0].equals(Long.class)
                && method.getParameterTypes()[1].equals(CampaignStatus.class));
    }

    @Test
    @DisplayName("Should handle expired campaigns (end date in past)")
    void shouldHandleExpiredCampaigns() {
        // Given - Campaign with end date in the past
        Campaign expiredCampaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Expired Campaign"),
            "Description",
            LocalDate.now().minusDays(30),
            LocalDate.now().minusDays(1), // Ended yesterday
            CampaignStatus.active,
            fixedInstant.minusSeconds(7200),
            fixedInstant.minusSeconds(3600)
        );
        
        CampaignStatus targetStatus = CampaignStatus.closed;
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(expiredCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = expiredCampaign.changeStatus(targetStatus, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignStatusService.handle(campaignId, targetStatus);

        // Then - Should allow status change even for expired campaigns
        assertThat(result.getStatus()).isEqualTo(targetStatus);
        assertThat(result.getEndDate()).isBefore(LocalDate.now());
    }
}