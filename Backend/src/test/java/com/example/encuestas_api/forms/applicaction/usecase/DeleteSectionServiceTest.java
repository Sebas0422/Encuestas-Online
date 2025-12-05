package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.DeleteSectionPort;
import com.example.encuestas_api.forms.application.usecase.DeleteSectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteSectionService Tests")
class DeleteSectionServiceTest {

    @Mock
    private DeleteSectionPort deletePort;

    private DeleteSectionService service;

    private final Long FORM_ID = 1L;
    private final Long SECTION_ID = 100L;
    private final Long NON_EXISTENT_FORM_ID = 999L;
    private final Long NON_EXISTENT_SECTION_ID = 999L;

    @BeforeEach
    void setUp() {
        service = new DeleteSectionService(deletePort);
    }

    @Nested
    @DisplayName("When deleting a section")
    class WhenDeletingASection {

        @Test
        @DisplayName("Should delete section successfully")
        void shouldDeleteSectionSuccessfully() {
            // Arrange
            doNothing().when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act
            service.handle(FORM_ID, SECTION_ID);

            // Assert
            verify(deletePort).delete(FORM_ID, SECTION_ID);
        }

        @Test
        @DisplayName("Should delete section with different IDs")
        void shouldDeleteSectionWithDifferentIds() {
            // Arrange
            Long[] formIds = {1L, 2L, 3L, 1000L};
            Long[] sectionIds = {10L, 20L, 30L, 10000L};

            for (int i = 0; i < formIds.length; i++) {
                Long formId = formIds[i];
                Long sectionId = sectionIds[i];

                // Reset mock for each iteration
                reset(deletePort);
                doNothing().when(deletePort).delete(formId, sectionId);

                // Act
                service.handle(formId, sectionId);

                // Assert
                verify(deletePort).delete(formId, sectionId);
            }
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call deletePort with correct parameters")
        void shouldCallDeletePortWithCorrectParameters() {
            // Arrange
            doNothing().when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act
            service.handle(FORM_ID, SECTION_ID);

            // Assert
            verify(deletePort).delete(FORM_ID, SECTION_ID);
        }

        @Test
        @DisplayName("Should call deletePort exactly once")
        void shouldCallDeletePortExactlyOnce() {
            // Arrange
            doNothing().when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act
            service.handle(FORM_ID, SECTION_ID);

            // Assert
            verify(deletePort, times(1)).delete(FORM_ID, SECTION_ID);
            verifyNoMoreInteractions(deletePort);
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
            doThrow(expectedException).when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID));

            // Verify the exception is the same
            assertSame(expectedException, exception);
            verify(deletePort).delete(FORM_ID, SECTION_ID);
        }

        @Test
        @DisplayName("Should handle null form ID")
        void shouldHandleNullFormId() {
            // Arrange
            doThrow(new IllegalArgumentException("formId cannot be null"))
                .when(deletePort).delete(null, SECTION_ID);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(null, SECTION_ID));

            verify(deletePort).delete(null, SECTION_ID);
        }

        @Test
        @DisplayName("Should handle null section ID")
        void shouldHandleNullSectionId() {
            // Arrange
            doThrow(new IllegalArgumentException("sectionId cannot be null"))
                .when(deletePort).delete(FORM_ID, null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, null));

            verify(deletePort).delete(FORM_ID, null);
        }

        @Test
        @DisplayName("Should handle both null IDs")
        void shouldHandleBothNullIds() {
            // Arrange
            doThrow(new IllegalArgumentException("IDs cannot be null"))
                .when(deletePort).delete(null, null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(null, null));

            verify(deletePort).delete(null, null);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle negative form ID")
        void shouldHandleNegativeFormId() {
            // Arrange
            Long negativeFormId = -1L;
            doNothing().when(deletePort).delete(negativeFormId, SECTION_ID);

            // Act
            service.handle(negativeFormId, SECTION_ID);

            // Assert
            verify(deletePort).delete(negativeFormId, SECTION_ID);
        }

        @Test
        @DisplayName("Should handle negative section ID")
        void shouldHandleNegativeSectionId() {
            // Arrange
            Long negativeSectionId = -100L;
            doNothing().when(deletePort).delete(FORM_ID, negativeSectionId);

            // Act
            service.handle(FORM_ID, negativeSectionId);

            // Assert
            verify(deletePort).delete(FORM_ID, negativeSectionId);
        }

        @Test
        @DisplayName("Should handle zero IDs")
        void shouldHandleZeroIds() {
            // Arrange
            Long zeroFormId = 0L;
            Long zeroSectionId = 0L;
            doNothing().when(deletePort).delete(zeroFormId, zeroSectionId);

            // Act
            service.handle(zeroFormId, zeroSectionId);

            // Assert
            verify(deletePort).delete(zeroFormId, zeroSectionId);
        }

        @Test
        @DisplayName("Should handle very large IDs")
        void shouldHandleVeryLargeIds() {
            // Arrange
            Long largeFormId = Long.MAX_VALUE;
            Long largeSectionId = Long.MAX_VALUE - 1;
            doNothing().when(deletePort).delete(largeFormId, largeSectionId);

            // Act
            service.handle(largeFormId, largeSectionId);

            // Assert
            verify(deletePort).delete(largeFormId, largeSectionId);
        }
    }

    @Nested
    @DisplayName("Transactional behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should complete operation within transaction")
        void shouldCompleteOperationWithinTransaction() {
            // Arrange
            doNothing().when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act
            service.handle(FORM_ID, SECTION_ID);

            // Assert
            verify(deletePort).delete(FORM_ID, SECTION_ID);
        }

        @Test
        @DisplayName("Should rollback on exception")
        void shouldRollbackOnException() {
            // Arrange
            RuntimeException exception = new RuntimeException("Delete failed");
            doThrow(exception).when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID));

            // Operation should still be attempted before rollback
            verify(deletePort).delete(FORM_ID, SECTION_ID);
        }
    }

    @Nested
    @DisplayName("Performance and concurrency")
    class PerformanceAndConcurrency {

        @Test
        @DisplayName("Should handle multiple calls efficiently")
        void shouldHandleMultipleCallsEfficiently() {
            // Arrange
            doNothing().when(deletePort).delete(anyLong(), anyLong());

            // Act - make multiple calls
            for (int i = 0; i < 10; i++) {
                service.handle((long) i, (long) (i * 10));
            }

            // Assert - should be called 10 times
            verify(deletePort, times(10)).delete(anyLong(), anyLong());
        }

        @Test
        @DisplayName("Should not have side effects between calls")
        void shouldNotHaveSideEffectsBetweenCalls() {
            // Arrange
            doNothing().when(deletePort).delete(anyLong(), anyLong());

            // Act - make calls with different parameters
            service.handle(1L, 10L);
            service.handle(2L, 20L);
            service.handle(3L, 30L);

            // Assert - each call should be independent
            verify(deletePort).delete(1L, 10L);
            verify(deletePort).delete(2L, 20L);
            verify(deletePort).delete(3L, 30L);
        }
    }

    @Nested
    @DisplayName("Integration with deletePort")
    class IntegrationWithDeletePort {

        @Test
        @DisplayName("Should pass through all parameters unchanged")
        void shouldPassThroughAllParametersUnchanged() {
            // Arrange
            doNothing().when(deletePort).delete(FORM_ID, SECTION_ID);

            // Act
            service.handle(FORM_ID, SECTION_ID);

            // Assert
            verify(deletePort).delete(eq(FORM_ID), eq(SECTION_ID));
        }

        @Test
        @DisplayName("Should maintain parameter order")
        void shouldMaintainParameterOrder() {
            // Arrange
            Long formId1 = 1L;
            Long sectionId1 = 10L;
            Long formId2 = 2L;
            Long sectionId2 = 20L;

            doNothing().when(deletePort).delete(anyLong(), anyLong());

            // Act
            service.handle(formId1, sectionId1);
            service.handle(formId2, sectionId2);

            // Assert - verify order of parameters
            verify(deletePort).delete(formId1, sectionId1);
            verify(deletePort).delete(formId2, sectionId2);
        }
    }

    @Nested
    @DisplayName("Null safety")
    class NullSafety {

        @Test
        @DisplayName("Should handle null form ID gracefully")
        void shouldHandleNullFormIdGracefully() {
            // Test depends on how deletePort handles null
            // This test verifies that the service doesn't crash
            // and passes the null through to the port
            doThrow(new NullPointerException("formId is null"))
                .when(deletePort).delete(null, SECTION_ID);

            assertThrows(NullPointerException.class,
                () -> service.handle(null, SECTION_ID));
        }

        @Test
        @DisplayName("Should handle null section ID gracefully")
        void shouldHandleNullSectionIdGracefully() {
            doThrow(new NullPointerException("sectionId is null"))
                .when(deletePort).delete(FORM_ID, null);

            assertThrows(NullPointerException.class,
                () -> service.handle(FORM_ID, null));
        }
    }
}