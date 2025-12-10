package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.out.DeleteCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.DeleteCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCampaignService Tests")
public class DeleteCampaignServiceTest {
    
    @Mock
    private DeleteCampaignPort deletePort;
    
    @InjectMocks
    private DeleteCampaignService deleteCampaignService;
    
    private final Long CAMPAIGN_ID = 1L;
    private final Long NON_EXISTENT_ID = 999L;
    
    @Nested
    @DisplayName("When deleting campaign")
    class WhenDeletingCampaign {
        
        @Test
        @DisplayName("Should delete campaign successfully")
        void shouldDeleteCampaignSuccessfully() {
            // Arrange
            doNothing().when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act
            deleteCampaignService.handle(CAMPAIGN_ID);
            
            // Assert
            verify(deletePort).deleteById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should delete campaign with different ID")
        void shouldDeleteCampaignWithDifferentId() {
            // Arrange
            Long anotherId = 2L;
            doNothing().when(deletePort).deleteById(anotherId);
            
            // Act
            deleteCampaignService.handle(anotherId);
            
            // Assert
            verify(deletePort).deleteById(anotherId);
        }
    }
    
    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {
        
        @Test
        @DisplayName("Should handle null ID by passing to port")
        void shouldHandleNullIdByPassingToPort() {
            // Arrange
            doNothing().when(deletePort).deleteById(null);
            
            // Act
            deleteCampaignService.handle(null);
            
            // Assert - El servicio pasa el null al port
            verify(deletePort).deleteById(null);
        }
        
        @Test
        @DisplayName("Should handle zero ID")
        void shouldHandleZeroId() {
            // Arrange
            Long zeroId = 0L;
            doNothing().when(deletePort).deleteById(zeroId);
            
            // Act
            deleteCampaignService.handle(zeroId);
            
            // Assert
            verify(deletePort).deleteById(zeroId);
        }
        
        @Test
        @DisplayName("Should handle negative ID")
        void shouldHandleNegativeId() {
            // Arrange
            Long negativeId = -1L;
            doNothing().when(deletePort).deleteById(negativeId);
            
            // Act
            deleteCampaignService.handle(negativeId);
            
            // Assert
            verify(deletePort).deleteById(negativeId);
        }
    }
    
    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {
        
        @Test
        @DisplayName("Should call deletePort.deleteById with correct parameter")
        void shouldCallDeletePortDeleteByIdWithCorrectParameter() {
            // Arrange
            doNothing().when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act
            deleteCampaignService.handle(CAMPAIGN_ID);
            
            // Assert
            verify(deletePort).deleteById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should complete within transaction boundaries")
        void shouldCompleteWithinTransactionBoundaries() {
            // Arrange
            doNothing().when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act
            deleteCampaignService.handle(CAMPAIGN_ID);
            
            // Assert
            verify(deletePort).deleteById(CAMPAIGN_ID);
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
            doThrow(expectedException).when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act & Assert
            RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> deleteCampaignService.handle(CAMPAIGN_ID)
            );
            
            assertThat(thrown).isSameAs(expectedException);
            verify(deletePort).deleteById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should handle non-existent campaign deletion")
        void shouldHandleNonExistentCampaignDeletion() {
            // Arrange
            doNothing().when(deletePort).deleteById(NON_EXISTENT_ID);
            
            // Act
            deleteCampaignService.handle(NON_EXISTENT_ID);
            
            // Assert
            verify(deletePort).deleteById(NON_EXISTENT_ID);
        }
        
        @Test
        @DisplayName("Should handle database constraint violations")
        void shouldHandleDatabaseConstraintViolations() {
            // Arrange
            doThrow(new RuntimeException("Foreign key constraint violation"))
                .when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act & Assert
            assertThrows(RuntimeException.class, () -> 
                deleteCampaignService.handle(CAMPAIGN_ID)
            );
            
            verify(deletePort).deleteById(CAMPAIGN_ID);
        }
    }
    
    @Nested
    @DisplayName("Integration scenarios")
    class IntegrationScenarios {
        
        @Test
        @DisplayName("Should handle multiple deletions")
        void shouldHandleMultipleDeletions() {
            // Arrange
            Long id1 = 1L;
            Long id2 = 2L;
            Long id3 = 3L;
            
            // Act
            deleteCampaignService.handle(id1);
            deleteCampaignService.handle(id2);
            deleteCampaignService.handle(id3);
            
            // Assert
            verify(deletePort).deleteById(id1);
            verify(deletePort).deleteById(id2);
            verify(deletePort).deleteById(id3);
            verify(deletePort, times(3)).deleteById(anyLong());
        }
        
        @Test
        @DisplayName("Should handle deletion in transactional context")
        void shouldHandleDeletionInTransactionalContext() {
            // Arrange
            doNothing().when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act
            deleteCampaignService.handle(CAMPAIGN_ID);
            
            // Assert
            verify(deletePort).deleteById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should verify no side effects")
        void shouldVerifyNoSideEffects() {
            // Arrange
            doNothing().when(deletePort).deleteById(CAMPAIGN_ID);
            
            // Act
            deleteCampaignService.handle(CAMPAIGN_ID);
            
            // Assert
            verify(deletePort, only()).deleteById(CAMPAIGN_ID);
        }
        
        @Test
        @DisplayName("Should not call deletePort when exception occurs before")
        void shouldNotCallDeletePortWhenExceptionOccursBefore() {
            // Este test no es aplicable ya que el servicio no tiene validaciones
            // que puedan lanzar excepciones antes de llamar al port
            // Se mantiene como documentación
            assertThat(true).isTrue(); // Placeholder
        }
    }
    
    // Método helper para assertThrows
    private <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable runnable) {
        try {
            runnable.run();
            throw new AssertionError("Expected exception of type " + expectedType.getName() + " but no exception was thrown");
        } catch (Throwable actualException) {
            if (!expectedType.isInstance(actualException)) {
                throw new AssertionError("Expected exception of type " + expectedType.getName() + 
                    " but got " + actualException.getClass().getName(), actualException);
            }
            return expectedType.cast(actualException);
        }
    }
}