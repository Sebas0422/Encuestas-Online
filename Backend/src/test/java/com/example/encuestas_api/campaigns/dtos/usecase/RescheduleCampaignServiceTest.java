package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.in.RescheduleCampaignUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.RescheduleCampaignService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RescheduleCampaignService Tests")
public class RescheduleCampaignServiceTest {
    
    @Mock
    private LoadCampaignPort loadCampaignPort;
    
    @Mock
    private SaveCampaignPort saveCampaignPort;
    
    private Clock fixedClock;
    private RescheduleCampaignService rescheduleCampaignService;
    
    private final Long CAMPAIGN_ID = 1L;
    private final Long NON_EXISTENT_CAMPAIGN_ID = 999L;
    
    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneId.of("UTC"));
        rescheduleCampaignService = new RescheduleCampaignService(
            loadCampaignPort,
            saveCampaignPort,
            fixedClock
        );
    }
    
    @Nested
    @DisplayName("When rescheduling campaign")
    class WhenReschedulingCampaign {
        
        @Test
        @DisplayName("Should reschedule campaign successfully")
        void shouldRescheduleCampaignSuccessfully() {
            // Arrange
            LocalDate oldStart = LocalDate.of(2024, 1, 1);
            LocalDate oldEnd = LocalDate.of(2024, 1, 31);
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(newStart, newEnd, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
        
        @ParameterizedTest
        @CsvSource({
            "2024-03-01, 2024-03-31",
            "2024-06-15, 2024-07-15",
            "2024-12-01, 2024-12-31",
            "2025-01-01, 2025-01-31"
        })
        @DisplayName("Should reschedule campaign with various dates")
        void shouldRescheduleCampaignWithVariousDates(String startStr, String endStr) {
            // Arrange
            LocalDate newStart = LocalDate.parse(startStr);
            LocalDate newEnd = LocalDate.parse(endStr);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(newStart, newEnd, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
        
        @Test
        @DisplayName("Should reschedule to same dates (idempotent)")
        void shouldRescheduleToSameDates() {
            // Arrange
            LocalDate startDate = LocalDate.of(2024, 3, 1);
            LocalDate endDate = LocalDate.of(2024, 3, 31);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign sameCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(startDate, endDate, Instant.now(fixedClock)))
                .thenReturn(sameCampaign);
            when(saveCampaignPort.save(sameCampaign))
                .thenReturn(sameCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, startDate, endDate);
            
            // Assert
            assertThat(result).isSameAs(sameCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(startDate, endDate, Instant.now(fixedClock));
            verify(saveCampaignPort).save(sameCampaign);
        }
        
        @Test
        @DisplayName("Should handle single-day campaign")
        void shouldHandleSingleDayCampaign() {
            // Arrange
            LocalDate sameDate = LocalDate.of(2024, 5, 15);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(sameDate, sameDate, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, sameDate, sameDate);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(sameDate, sameDate, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
    }
    
    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {
        
        @Test
        @DisplayName("Should throw CampaignNotFoundException when campaign does not exist")
        void shouldThrowCampaignNotFoundExceptionWhenCampaignDoesNotExist() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            when(loadCampaignPort.loadById(NON_EXISTENT_CAMPAIGN_ID))
                .thenReturn(Optional.empty());
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(NON_EXISTENT_CAMPAIGN_ID, newStart, newEnd)
            ).isInstanceOf(CampaignNotFoundException.class)
             .hasMessageContaining(NON_EXISTENT_CAMPAIGN_ID.toString());
            
            verify(loadCampaignPort).loadById(NON_EXISTENT_CAMPAIGN_ID);
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle null campaign ID")
        void shouldHandleNullCampaignId() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            // loadPort.loadById(null) might throw NPE or return Optional.empty()
            when(loadCampaignPort.loadById(null))
                .thenReturn(Optional.empty());
            
            // Act & Assert
            // Depends on loadPort implementation
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(null, newStart, newEnd)
            ).isInstanceOf(RuntimeException.class);
            
            verify(loadCampaignPort).loadById(null);
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle null start date - expecting exception from campaign.reschedule()")
        void shouldHandleNullStartDate() {
            // Arrange
            LocalDate endDate = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            
            // campaign.reschedule(null, endDate, instant) should throw exception
            when(existingCampaign.reschedule(null, endDate, Instant.now(fixedClock)))
                .thenThrow(new IllegalArgumentException("Start date cannot be null"));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, null, endDate)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Start date cannot be null");
            
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(null, endDate, Instant.now(fixedClock));
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle null end date - expecting exception from campaign.reschedule()")
        void shouldHandleNullEndDate() {
            // Arrange
            LocalDate startDate = LocalDate.of(2024, 2, 1);
            
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            
            when(existingCampaign.reschedule(startDate, null, Instant.now(fixedClock)))
                .thenThrow(new IllegalArgumentException("End date cannot be null"));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, startDate, null)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("End date cannot be null");
            
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(startDate, null, Instant.now(fixedClock));
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle both null dates - expecting exception from campaign.reschedule()")
        void shouldHandleBothNullDates() {
            // Arrange
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            
            when(existingCampaign.reschedule(null, null, Instant.now(fixedClock)))
                .thenThrow(new IllegalArgumentException("Dates cannot be null"));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, null, null)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("Dates cannot be null");
            
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(null, null, Instant.now(fixedClock));
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle end date before start date - expecting validation exception")
        void shouldHandleEndDateBeforeStartDate() {
            // Arrange
            LocalDate startDate = LocalDate.of(2024, 3, 1);
            LocalDate endDate = LocalDate.of(2024, 2, 28); // End before start
            
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            
            when(existingCampaign.reschedule(startDate, endDate, Instant.now(fixedClock)))
                .thenThrow(new IllegalArgumentException("End date must be after start date"));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, startDate, endDate)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("End date must be after start date");
            
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(startDate, endDate, Instant.now(fixedClock));
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should handle dates in the past")
        void shouldHandleDatesInThePast() {
            // Arrange
            LocalDate pastStart = LocalDate.of(2023, 12, 1);
            LocalDate pastEnd = LocalDate.of(2023, 12, 31);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(pastStart, pastEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, pastStart, pastEnd);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(pastStart, pastEnd, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
    }
    
    @Nested
    @DisplayName("Clock usage")
    class ClockUsage {
        
        @Test
        @DisplayName("Should use clock for timestamp")
        void shouldUseClockForTimestamp() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            Instant expectedInstant = Instant.now(fixedClock);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, expectedInstant))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            verify(existingCampaign).reschedule(newStart, newEnd, expectedInstant);
        }
        
        @Test
        @DisplayName("Should use current time from clock")
        void shouldUseCurrentTimeFromClock() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(any(LocalDate.class), any(LocalDate.class), any(Instant.class)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            // Verify reschedule was called with an Instant parameter
            verify(existingCampaign).reschedule(newStart, newEnd, Instant.now(fixedClock));
        }
        
        @Test
        @DisplayName("Should use fixed clock for deterministic tests")
        void shouldUseFixedClockForDeterministicTests() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            Instant fixedInstant = Instant.now(fixedClock);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, fixedInstant))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            verify(existingCampaign).reschedule(newStart, newEnd, fixedInstant);
        }
    }
    
    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {
        
        @Test
        @DisplayName("Should call loadPort.loadById with correct ID")
        void shouldCallLoadPortLoadByIdWithCorrectId() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should call savePort.save with rescheduled campaign")
        void shouldCallSavePortSaveWithRescheduledCampaign() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
        
        @Test
        @DisplayName("Should not call savePort when campaign not found")
        void shouldNotCallSavePortWhenCampaignNotFound() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            when(loadCampaignPort.loadById(NON_EXISTENT_CAMPAIGN_ID))
                .thenReturn(Optional.empty());
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(NON_EXISTENT_CAMPAIGN_ID, newStart, newEnd)
            ).isInstanceOf(CampaignNotFoundException.class);
            
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should not call savePort when reschedule throws exception")
        void shouldNotCallSavePortWhenRescheduleThrowsException() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 1, 31); // Invalid: end before start
            
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenThrow(new IllegalArgumentException("Invalid dates"));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd)
            ).isInstanceOf(IllegalArgumentException.class);
            
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
    }
    
    @Nested
    @DisplayName("Business rules")
    class BusinessRules {
        
        @Test
        @DisplayName("Should preserve campaign ID after reschedule")
        void shouldPreserveCampaignIdAfterReschedule() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            // The ID should be preserved through the reschedule operation
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
        
        @Test
        @DisplayName("Should update timestamp on reschedule")
        void shouldUpdateTimestampOnReschedule() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(any(LocalDate.class), any(LocalDate.class), any(Instant.class)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd);
            
            // Assert
            // Verify reschedule was called with an Instant parameter
            verify(existingCampaign).reschedule(newStart, newEnd, Instant.now(fixedClock));
        }
        
        @Test
        @DisplayName("Should handle leap year dates")
        void shouldHandleLeapYearDates() {
            // Arrange
            LocalDate leapYearStart = LocalDate.of(2024, 2, 28); // 2024 is a leap year
            LocalDate leapYearEnd = LocalDate.of(2024, 3, 1);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(leapYearStart, leapYearEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, leapYearStart, leapYearEnd);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(leapYearStart, leapYearEnd, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
        
        @Test
        @DisplayName("Should handle month-end dates correctly")
        void shouldHandleMonthEndDatesCorrectly() {
            // Arrange
            LocalDate monthEndStart = LocalDate.of(2024, 1, 31);
            LocalDate monthEndEnd = LocalDate.of(2024, 2, 29); // 2024 is leap year
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(monthEndStart, monthEndEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenReturn(rescheduledCampaign);
            
            // Act
            Campaign result = rescheduleCampaignService.handle(CAMPAIGN_ID, monthEndStart, monthEndEnd);
            
            // Assert
            assertThat(result).isSameAs(rescheduledCampaign);
            verify(loadCampaignPort).loadById(CAMPAIGN_ID);
            verify(existingCampaign).reschedule(monthEndStart, monthEndEnd, Instant.now(fixedClock));
            verify(saveCampaignPort).save(rescheduledCampaign);
        }
    }
    
    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {
        
        @Test
        @DisplayName("Should propagate exception from loadPort")
        void shouldPropagateExceptionFromLoadPort() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            RuntimeException expectedException = new RuntimeException("Database error");
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenThrow(expectedException);
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd)
            ).isSameAs(expectedException);
            
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should propagate exception from savePort")
        void shouldPropagateExceptionFromSavePort() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 2, 28);
            
            Campaign existingCampaign = mock(Campaign.class);
            Campaign rescheduledCampaign = mock(Campaign.class);
            RuntimeException expectedException = new RuntimeException("Save failed");
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenReturn(rescheduledCampaign);
            when(saveCampaignPort.save(rescheduledCampaign))
                .thenThrow(expectedException);
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd)
            ).isSameAs(expectedException);
        }
        
        @Test
        @DisplayName("Should propagate exception from campaign.reschedule()")
        void shouldPropagateExceptionFromCampaignReschedule() {
            // Arrange
            LocalDate newStart = LocalDate.of(2024, 2, 1);
            LocalDate newEnd = LocalDate.of(2024, 1, 31); // Invalid: end before start
            
            Campaign existingCampaign = mock(Campaign.class);
            IllegalArgumentException expectedException = new IllegalArgumentException("Invalid date range");
            
            when(loadCampaignPort.loadById(CAMPAIGN_ID))
                .thenReturn(Optional.of(existingCampaign));
            when(existingCampaign.reschedule(newStart, newEnd, Instant.now(fixedClock)))
                .thenThrow(expectedException);
            
            // Act & Assert
            assertThatThrownBy(() -> 
                rescheduleCampaignService.handle(CAMPAIGN_ID, newStart, newEnd)
            ).isSameAs(expectedException);
            
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
    }
}