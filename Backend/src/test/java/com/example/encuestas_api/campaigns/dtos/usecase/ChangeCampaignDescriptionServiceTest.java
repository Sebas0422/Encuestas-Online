package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.in.ChangeCampaignDescriptionUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.ChangeCampaignDescriptionService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeCampaignDescriptionService Tests")
class ChangeCampaignDescriptionServiceTest {

    @Mock
    private LoadCampaignPort loadPort;

    @Mock
    private SaveCampaignPort savePort;

    @Mock
    private Clock clock;

    @InjectMocks
    private ChangeCampaignDescriptionService changeCampaignDescriptionService;

    private final Instant fixedInstant = Instant.parse("2024-01-01T10:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private final Long campaignId = 1L;
    private Campaign testCampaign;

    @BeforeEach
    void setUp() {
        testCampaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Original Description",
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(30),
            CampaignStatus.draft,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
    }

    @Test
    @DisplayName("Should change campaign description successfully")
    void shouldChangeCampaignDescriptionSuccessfully() {
        // Given
        String newDescription = "Updated campaign description";
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeDescription(newDescription, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(newDescription);
        assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant);
        assertThat(result.getId()).isEqualTo(campaignId);
        assertThat(result.getName()).isEqualTo(testCampaign.getName());

        verify(loadPort).loadById(campaignId);
        verify(clock).instant();
        verify(savePort).save(any(Campaign.class));
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundExceptionWhenCampaignDoesNotExist() {
        // Given
        when(loadPort.loadById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> changeCampaignDescriptionService.handle(campaignId, "New description"))
            .isInstanceOf(CampaignNotFoundException.class)
            .hasMessageContaining("no encontrada");

        verify(loadPort).loadById(campaignId);
        verifyNoInteractions(clock, savePort);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should handle empty or blank descriptions")
    void shouldHandleEmptyOrBlankDescriptions(String emptyDescription) {
        // Given
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeDescription(emptyDescription, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, emptyDescription);

        // Then
        if (emptyDescription == null) {
            assertThat(result.getDescription()).isNull();
        } else {
            assertThat(result.getDescription()).isEqualTo(emptyDescription.trim());
        }
    }

    @Test
    @DisplayName("Should preserve other campaign attributes when changing description")
    void shouldPreserveOtherCampaignAttributesWhenChangingDescription() {
        // Given
        String newDescription = "Updated description";
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeDescription(newDescription, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

        // Then
        assertThat(result.getId()).isEqualTo(testCampaign.getId());
        assertThat(result.getName()).isEqualTo(testCampaign.getName());
        assertThat(result.getStartDate()).isEqualTo(testCampaign.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(testCampaign.getEndDate());
        assertThat(result.getStatus()).isEqualTo(testCampaign.getStatus());
        assertThat(result.getCreatedAt()).isEqualTo(testCampaign.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant); // Updated
    }

    @Test
    @DisplayName("Should trim whitespace from description")
    void shouldTrimWhitespaceFromDescription() {
        // Given
        String descriptionWithSpaces = "  New description with spaces  ";
        String trimmedDescription = "New description with spaces";
        
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeDescription(descriptionWithSpaces, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, descriptionWithSpaces);

        // Then
        assertThat(result.getDescription()).isEqualTo(trimmedDescription);
    }

    @Test
    @DisplayName("Should use current timestamp from clock")
    void shouldUseCurrentTimestampFromClock() {
        // Given
        String newDescription = "New description";
        Instant now = Instant.now();
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(now);
        
        Campaign updatedCampaign = testCampaign.changeDescription(newDescription, now);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

        // Then
        assertThat(result.getUpdatedAt()).isEqualTo(now);
        verify(clock).instant();
    }

    @Test
    @DisplayName("Should handle very long descriptions")
    void shouldHandleVeryLongDescriptions() {
        // Given
        String longDescription = "A".repeat(2000); // Max length from DTO validation
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = testCampaign.changeDescription(longDescription, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, longDescription);

        // Then
        assertThat(result.getDescription()).isEqualTo(longDescription);
    }

    @Test
    @DisplayName("Should capture the exact campaign being saved")
    void shouldCaptureTheExactCampaignBeingSaved() {
        // Given
        String newDescription = "Captured description";
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(clock.instant()).thenReturn(fixedInstant);
        
        ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);
        Campaign savedCampaign = testCampaign.changeDescription(newDescription, fixedInstant);
        when(savePort.save(campaignCaptor.capture())).thenReturn(savedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

        // Then
        Campaign capturedCampaign = campaignCaptor.getValue();
        assertThat(capturedCampaign).isNotNull();
        assertThat(capturedCampaign.getId()).isEqualTo(campaignId);
        assertThat(capturedCampaign.getDescription()).isEqualTo(newDescription);
        assertThat(capturedCampaign.getUpdatedAt()).isEqualTo(fixedInstant);
        assertThat(result).isEqualTo(savedCampaign);
    }

    @Test
    @DisplayName("Should handle campaign in different statuses")
    void shouldHandleCampaignInDifferentStatuses() {
        // Test with each campaign status
        CampaignStatus[] allStatuses = CampaignStatus.values();
        
        for (CampaignStatus status : allStatuses) {
            // Given
            Campaign campaignWithStatus = Campaign.rehydrate(
                campaignId,
                CampaignName.of("Test Campaign"),
                "Original",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                status,
                fixedInstant.minusSeconds(3600),
                fixedInstant.minusSeconds(3600)
            );
            
            String newDescription = "Updated for status " + status;
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaignWithStatus));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = campaignWithStatus.changeDescription(newDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

            // Then
            assertThat(result.getStatus()).isEqualTo(status);
            assertThat(result.getDescription()).isEqualTo(newDescription);
            
            // Reset mocks for next iteration
            reset(loadPort, clock, savePort);
        }
    }

    @Test
    @DisplayName("Should verify transactional annotation")
    void shouldVerifyTransactionalAnnotation() {
        // This test verifies the service has proper transactional annotation
        var annotation = ChangeCampaignDescriptionService.class
            .getAnnotation(org.springframework.transaction.annotation.Transactional.class);
        
        assertThat(annotation).isNotNull();
    }

    @Test
    @DisplayName("Should verify service annotation")
    void shouldVerifyServiceAnnotation() {
        // This test verifies the service has proper Spring annotation
        var annotation = ChangeCampaignDescriptionService.class
            .getAnnotation(org.springframework.stereotype.Service.class);
        
        assertThat(annotation).isNotNull();
    }

    @Test
    @DisplayName("Should verify interface implementation")
    void shouldVerifyInterfaceImplementation() {
        // This test verifies the service implements the correct interface
        assertThat(changeCampaignDescriptionService)
            .isInstanceOf(ChangeCampaignDescriptionUseCase.class);
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should handle same description (no change)")
        void shouldHandleSameDescriptionNoChange() {
            // Given
            String sameDescription = testCampaign.getDescription();
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            // Even if description is the same, updatedAt should change
            Campaign updatedCampaign = testCampaign.changeDescription(sameDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            Campaign result = changeCampaignDescriptionService.handle(campaignId, sameDescription);

            // Then
            assertThat(result.getDescription()).isEqualTo(sameDescription);
            assertThat(result.getUpdatedAt()).isEqualTo(fixedInstant);
        }

        @Test
        @DisplayName("Should handle description with special characters")
        void shouldHandleDescriptionWithSpecialCharacters() {
            // Given
            String specialDescription = "Descripción con acentos áéíóú y símbolos !@#$%^&*()";
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = testCampaign.changeDescription(specialDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            Campaign result = changeCampaignDescriptionService.handle(campaignId, specialDescription);

            // Then
            assertThat(result.getDescription()).isEqualTo(specialDescription);
        }

        @Test
        @DisplayName("Should handle multiline descriptions")
        void shouldHandleMultilineDescriptions() {
            // Given
            String multilineDescription = "First line\nSecond line\nThird line";
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = testCampaign.changeDescription(multilineDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            Campaign result = changeCampaignDescriptionService.handle(campaignId, multilineDescription);

            // Then
            assertThat(result.getDescription()).isEqualTo(multilineDescription);
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            // When & Then
            assertThatThrownBy(() -> changeCampaignDescriptionService.handle(null, "New description"))
                .isInstanceOf(CampaignNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {
        
        @Test
        @DisplayName("Should maintain data integrity after multiple changes")
        void shouldMaintainDataIntegrityAfterMultipleChanges() {
            // Given
            String firstDescription = "First update";
            String secondDescription = "Second update";
            
            // First update
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign firstUpdate = testCampaign.changeDescription(firstDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(firstUpdate);
            
            Campaign firstResult = changeCampaignDescriptionService.handle(campaignId, firstDescription);
            assertThat(firstResult.getDescription()).isEqualTo(firstDescription);
            
            // Reset mocks for second update
            reset(loadPort, clock, savePort);
            
            // Second update (using the result from first update as starting point)
            Instant secondInstant = fixedInstant.plusSeconds(100);
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(firstUpdate));
            when(clock.instant()).thenReturn(secondInstant);
            
            Campaign secondUpdate = firstUpdate.changeDescription(secondDescription, secondInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(secondUpdate);
            
            // When
            Campaign secondResult = changeCampaignDescriptionService.handle(campaignId, secondDescription);

            // Then
            assertThat(secondResult.getDescription()).isEqualTo(secondDescription);
            assertThat(secondResult.getUpdatedAt()).isEqualTo(secondInstant);
            assertThat(secondResult.getCreatedAt()).isEqualTo(testCampaign.getCreatedAt());
        }

        @Test
        @DisplayName("Should handle save failure")
        void shouldHandleSaveFailure() {
            // Given
            String newDescription = "New description";
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            RuntimeException saveException = new RuntimeException("Database error");
            when(savePort.save(any(Campaign.class))).thenThrow(saveException);

            // When & Then
            assertThatThrownBy(() -> changeCampaignDescriptionService.handle(campaignId, newDescription))
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
            String originalDescription = testCampaign.getDescription();
            String newDescription = "Modified description";
            
            when(loadPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(clock.instant()).thenReturn(fixedInstant);
            
            Campaign updatedCampaign = testCampaign.changeDescription(newDescription, fixedInstant);
            when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

            // When
            changeCampaignDescriptionService.handle(campaignId, newDescription);

            // Then - Original campaign should remain unchanged
            assertThat(testCampaign.getDescription()).isEqualTo(originalDescription);
            assertThat(testCampaign.getUpdatedAt()).isNotEqualTo(fixedInstant);
        }
    }

    @Test
    @DisplayName("Should verify method signature matches interface")
    void shouldVerifyMethodSignatureMatchesInterface() {
        // Verify the method exists with correct parameters
        var methods = changeCampaignDescriptionService.getClass().getDeclaredMethods();
        
        assertThat(methods)
            .anyMatch(method -> method.getName().equals("handle") 
                && method.getParameterCount() == 2
                && method.getParameterTypes()[0].equals(Long.class)
                && method.getParameterTypes()[1].equals(String.class));
    }

    @Test
    @DisplayName("Should work with various campaign configurations")
    void shouldWorkWithVariousCampaignConfigurations() {
        // Test with null dates
        Campaign campaignWithNullDates = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test"),
            "Original",
            null,
            null,
            CampaignStatus.draft,
            fixedInstant,
            fixedInstant
        );
        
        String newDescription = "Updated for null dates";
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(campaignWithNullDates));
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign updatedCampaign = campaignWithNullDates.changeDescription(newDescription, fixedInstant);
        when(savePort.save(any(Campaign.class))).thenReturn(updatedCampaign);

        // When
        Campaign result = changeCampaignDescriptionService.handle(campaignId, newDescription);

        // Then
        assertThat(result.getDescription()).isEqualTo(newDescription);
        assertThat(result.getStartDate()).isNull();
        assertThat(result.getEndDate()).isNull();
    }
}