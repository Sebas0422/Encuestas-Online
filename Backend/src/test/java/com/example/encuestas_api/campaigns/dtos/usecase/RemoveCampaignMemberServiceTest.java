package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.out.DeleteCampaignMemberPort;
import com.example.encuestas_api.campaigns.application.usecase.RemoveCampaignMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveCampaignMemberService Tests")
public class RemoveCampaignMemberServiceTest {
    
    @Mock
    private DeleteCampaignMemberPort deletePort;
    
    @InjectMocks
    private RemoveCampaignMemberService removeCampaignMemberService;
    
    private final Long CAMPAIGN_ID = 1L;
    private final Long USER_ID = 100L;
    private final Long NON_EXISTENT_CAMPAIGN_ID = 999L;
    private final Long NON_EXISTENT_USER_ID = 888L;
    
    @Nested
    @DisplayName("When removing campaign member")
    class WhenRemovingCampaignMember {
        
        @Test
        @DisplayName("Should remove member successfully")
        void shouldRemoveMemberSuccessfully() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
        
        @Test
        @DisplayName("Should remove member with different IDs")
        void shouldRemoveMemberWithDifferentIds() {
            // Arrange
            Long differentCampaignId = 2L;
            Long differentUserId = 200L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(differentCampaignId, differentUserId);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(differentCampaignId, differentUserId)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(differentCampaignId, differentUserId);
        }
    }
    
    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {
        
        @Test
        @DisplayName("Should handle null campaign ID by throwing exception")
        void shouldHandleNullCampaignIdByThrowingException() {
            // Act & Assert
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(null, USER_ID)
            ).isInstanceOf(NullPointerException.class);
            
            verify(deletePort, never()).deleteByCampaignIdAndUserId(anyLong(), anyLong());
        }
        
        @Test
        @DisplayName("Should handle null user ID by throwing exception")
        void shouldHandleNullUserIdByThrowingException() {
            // Act & Assert
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, null)
            ).isInstanceOf(NullPointerException.class);
            
            verify(deletePort, never()).deleteByCampaignIdAndUserId(anyLong(), anyLong());
        }
        
        @Test
        @DisplayName("Should handle both null IDs by throwing exception")
        void shouldHandleBothNullIdsByThrowingException() {
            // Act & Assert
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(null, null)
            ).isInstanceOf(NullPointerException.class);
            
            verify(deletePort, never()).deleteByCampaignIdAndUserId(anyLong(), anyLong());
        }
        
        @Test
        @DisplayName("Should handle zero campaign ID")
        void shouldHandleZeroCampaignId() {
            // Arrange
            Long zeroCampaignId = 0L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(zeroCampaignId, USER_ID);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(zeroCampaignId, USER_ID)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(zeroCampaignId, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle zero user ID")
        void shouldHandleZeroUserId() {
            // Arrange
            Long zeroUserId = 0L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, zeroUserId);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, zeroUserId)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, zeroUserId);
        }
        
        @Test
        @DisplayName("Should handle negative campaign ID")
        void shouldHandleNegativeCampaignId() {
            // Arrange
            Long negativeCampaignId = -1L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(negativeCampaignId, USER_ID);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(negativeCampaignId, USER_ID)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(negativeCampaignId, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle negative user ID")
        void shouldHandleNegativeUserId() {
            // Arrange
            Long negativeUserId = -1L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, negativeUserId);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, negativeUserId)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, negativeUserId);
        }
    }
    
    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {
        
        @Test
        @DisplayName("Should call deletePort.deleteByCampaignIdAndUserId with correct parameters")
        void shouldCallDeletePortDeleteByCampaignIdAndUserIdWithCorrectParameters() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID);
            
            // Assert
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
        
        @Test
        @DisplayName("Should not call deletePort when exception occurs before")
        void shouldNotCallDeletePortWhenExceptionOccursBefore() {
            // Act & Assert for null campaignId
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(null, USER_ID)
            ).isInstanceOf(NullPointerException.class);
            
            verify(deletePort, never()).deleteByCampaignIdAndUserId(anyLong(), anyLong());
        }
        
        @Test
        @DisplayName("Should complete within transaction boundaries")
        void shouldCompleteWithinTransactionBoundaries() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID);
            
            // Assert - Verificar que la operación se completa sin excepciones
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
    }
    
    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {
        
        @Test
        @DisplayName("Should propagate exception from deletePort")
        void shouldPropagateExceptionFromDeletePort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            doThrow(expectedException).when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act & Assert
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID)
            ).isSameAs(expectedException);
            
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle non-existent member deletion")
        void shouldHandleNonExistentMemberDeletion() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(NON_EXISTENT_CAMPAIGN_ID, NON_EXISTENT_USER_ID);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(NON_EXISTENT_CAMPAIGN_ID, NON_EXISTENT_USER_ID)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(NON_EXISTENT_CAMPAIGN_ID, NON_EXISTENT_USER_ID);
        }
        
        @Test
        @DisplayName("Should handle database constraint violations")
        void shouldHandleDatabaseConstraintViolations() {
            // Arrange
            doThrow(new RuntimeException("Foreign key constraint violation"))
                .when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act & Assert
            assertThatThrownBy(() -> 
                removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID)
            ).isInstanceOf(RuntimeException.class)
             .hasMessageContaining("constraint");
            
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
    }
    
    @Nested
    @DisplayName("Integration scenarios")
    class IntegrationScenarios {
        
        @Test
        @DisplayName("Should handle multiple member removals")
        void shouldHandleMultipleMemberRemovals() {
            // Arrange
            Long campaignId1 = 1L;
            Long userId1 = 100L;
            Long campaignId2 = 2L;
            Long userId2 = 200L;
            Long campaignId3 = 3L;
            Long userId3 = 300L;
            
            // Act
            removeCampaignMemberService.handle(campaignId1, userId1);
            removeCampaignMemberService.handle(campaignId2, userId2);
            removeCampaignMemberService.handle(campaignId3, userId3);
            
            // Assert
            verify(deletePort).deleteByCampaignIdAndUserId(campaignId1, userId1);
            verify(deletePort).deleteByCampaignIdAndUserId(campaignId2, userId2);
            verify(deletePort).deleteByCampaignIdAndUserId(campaignId3, userId3);
            verify(deletePort, times(3)).deleteByCampaignIdAndUserId(anyLong(), anyLong());
        }
        
        @Test
        @DisplayName("Should handle removal in transactional context")
        void shouldHandleRemovalInTransactionalContext() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID);
            
            // Assert
            verify(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
        
        @Test
        @DisplayName("Should verify no side effects")
        void shouldVerifyNoSideEffects() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID);
            
            // Assert
            verify(deletePort, only()).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
    }
    
    @Nested
    @DisplayName("Business rules")
    class BusinessRules {
        
        @Test
        @DisplayName("Should allow removing member from any campaign")
        void shouldAllowRemovingMemberFromAnyCampaign() {
            // Arrange
            Long anyCampaignId = 12345L;
            Long anyUserId = 54321L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(anyCampaignId, anyUserId);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(anyCampaignId, anyUserId)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(anyCampaignId, anyUserId);
        }
        
        @Test
        @DisplayName("Should handle same member removal multiple times")
        void shouldHandleSameMemberRemovalMultipleTimes() {
            // Arrange
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
            
            // Act
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID);
            removeCampaignMemberService.handle(CAMPAIGN_ID, USER_ID); // Second time
            
            // Assert
            verify(deletePort, times(2)).deleteByCampaignIdAndUserId(CAMPAIGN_ID, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle removing creator role member (if allowed)")
        void shouldHandleRemovingCreatorRoleMember() {
            // Arrange
            Long creatorCampaignId = 1L;
            Long creatorUserId = 999L;
            doNothing().when(deletePort).deleteByCampaignIdAndUserId(creatorCampaignId, creatorUserId);
            
            // Act & Assert
            assertThatCode(() -> 
                removeCampaignMemberService.handle(creatorCampaignId, creatorUserId)
            ).doesNotThrowAnyException();
            
            verify(deletePort).deleteByCampaignIdAndUserId(creatorCampaignId, creatorUserId);
        }
    }
}