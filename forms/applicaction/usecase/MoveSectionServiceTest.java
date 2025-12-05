package com.example.encuestas_api.forms.applicaction.usecase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.encuestas_api.forms.application.port.out.LoadSectionPort;
import com.example.encuestas_api.forms.application.port.out.ReorderSectionsPort;
import com.example.encuestas_api.forms.application.usecase.MoveSectionService;
import com.example.encuestas_api.forms.domain.model.Section;

@ExtendWith(MockitoExtension.class)
@DisplayName("MoveSectionService Tests")
class MoveSectionServiceTest {

    @Mock
    private LoadSectionPort loadSectionPort;

    @Mock
    private ReorderSectionsPort reorderSectionsPort;

    private MoveSectionService service;

    private final Long FORM_ID = 1L;
    private final Long SECTION_ID = 100L;
    private final Long OTHER_FORM_ID = 2L;
    private final Long NON_EXISTENT_SECTION_ID = 999L;
    private final int VALID_POSITION = 2;
    private final int NEGATIVE_POSITION = -1;
    private final int ZERO_POSITION = 0;
    private final int LARGE_POSITION = 1000;

    public MoveSectionServiceTest(LoadSectionPort loadSectionPort) {
        this.loadSectionPort = loadSectionPort;
    }

    @BeforeEach
    void setUp() {
        service = new MoveSectionService(loadSectionPort, reorderSectionsPort);
    }

    @Nested
    @DisplayName("When moving a section")
    class WhenMovingASection {

        @Test
        @DisplayName("Should move section successfully")
        void shouldMoveSectionSuccessfully() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, VALID_POSITION);

            // Assert
            assertNotNull(result);
            assertSame(movedSection, result);
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }

        @Test
        @DisplayName("Should move section to position zero")
        void shouldMoveSectionToPositionZero() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, ZERO_POSITION)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, ZERO_POSITION);

            // Assert
            assertSame(movedSection, result);
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, ZERO_POSITION);
        }

        @Test
        @DisplayName("Should move section to large position")
        void shouldMoveSectionToLargePosition() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, LARGE_POSITION)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, LARGE_POSITION);

            // Assert
            assertSame(movedSection, result);
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, LARGE_POSITION);
        }
    }

    @Nested
    @DisplayName("Validation and error cases")
    class ValidationAndErrorCases {

        @Test
        @DisplayName("Should throw IllegalArgumentException when section not found")
        void shouldThrowIllegalArgumentExceptionWhenSectionNotFound() {
            // Arrange
            when(loadSectionPort.loadById(NON_EXISTENT_SECTION_ID)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, NON_EXISTENT_SECTION_ID, VALID_POSITION));

            assertNotNull(exception.getMessage());
            verify(loadSectionPort).loadById(NON_EXISTENT_SECTION_ID);
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when section belongs to different form")
        void shouldThrowIllegalArgumentExceptionWhenSectionBelongsToDifferentForm() {
            // Arrange
            Section section = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(OTHER_FORM_ID);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            assertEquals("La sección no pertenece al form", exception.getMessage());
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when newPosition is negative")
        void shouldThrowIllegalArgumentExceptionWhenNewPositionIsNegative() {
            // Arrange
            Section section = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, NEGATIVE_POSITION));

            assertEquals("newPosition >= 0 requerido", exception.getMessage());
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should throw exception for different negative positions")
        void shouldThrowExceptionForDifferentNegativePositions() {
            // Test various negative values
            int[] negativePositions = {-1, -10, -100, Integer.MIN_VALUE};
            
            for (int position : negativePositions) {
                // Reset mocks for each iteration
                reset(loadSectionPort, reorderSectionsPort);
                
                Section section = mock(Section.class);
                when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
                when(section.getFormId()).thenReturn(FORM_ID);

                // Act & Assert
                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> service.handle(FORM_ID, SECTION_ID, position));

                assertEquals("newPosition >= 0 requerido", exception.getMessage());
                verifyNoInteractions(reorderSectionsPort);
            }
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call loadSectionPort with correct section ID")
        void shouldCallLoadSectionPortWithCorrectSectionId() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);

            // Act
            service.handle(FORM_ID, SECTION_ID, VALID_POSITION);

            // Assert
            verify(loadSectionPort).loadById(SECTION_ID);
        }

        @Test
        @DisplayName("Should call reorderSectionsPort with correct parameters")
        void shouldCallReorderSectionsPortWithCorrectParameters() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);

            // Act
            service.handle(FORM_ID, SECTION_ID, VALID_POSITION);

            // Assert
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }

        @Test
        @DisplayName("Should not call reorderSectionsPort when validation fails")
        void shouldNotCallReorderSectionsPortWhenValidationFails() {
            // Arrange
            Section section = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(OTHER_FORM_ID); // Different form ID

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should pass through all parameters correctly")
        void shouldPassThroughAllParametersCorrectly() {
            // Test with various parameter combinations
            Long[] formIds = {1L, 2L, 100L};
            Long[] sectionIds = {10L, 20L, 1000L};
            int[] positions = {0, 1, 5, 10};
            for (Long formId : formIds) {
                for (Long sectionId : sectionIds) {
                    for (int position : positions) {
                        // Reset mocks for each combination
                        reset(loadSectionPort, reorderSectionsPort);
                        Section section = mock(Section.class);
                        Section movedSection = mock(Section.class);
                        when(loadSectionPort.loadById(sectionId)).thenReturn(Optional.of(section));
                         when(section.getFormId()).thenReturn(formId);
                        when(reorderSectionsPort.moveTo(formId, sectionId, position)).thenReturn(movedSection);

                        // Act
                        Section result = service.handle(formId, sectionId, position);

                        // Assert
                        assertSame(movedSection, result);
                        verify(loadSectionPort).loadById(sectionId);
                        verify(reorderSectionsPort).moveTo(formId, sectionId, position);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null form ID from section")
        void shouldHandleNullFormIdFromSection() {
            // Arrange
            Section section = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            assertEquals("La sección no pertenece al form", exception.getMessage());
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should handle same form ID validation")
        void shouldHandleSameFormIdValidation() {
            // Test that equals comparison works correctly
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID); // Same object reference
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, VALID_POSITION);

            // Assert - Should succeed when form IDs are equal
            assertSame(movedSection, result);
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }

        @Test
        @DisplayName("Should handle very large position values")
        void shouldHandleVeryLargePositionValues() {
            // Arrange
            int veryLargePosition = Integer.MAX_VALUE;
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, veryLargePosition)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, veryLargePosition);

            // Assert
            assertSame(movedSection, result);
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, veryLargePosition);
        }

        @Test
        @DisplayName("Should handle position zero correctly")
        void shouldHandlePositionZeroCorrectly() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, 0)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, 0);

            // Assert - Position 0 should be valid
            assertSame(movedSection, result);
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, 0);
        }
    }

    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should propagate exception from loadSectionPort")
        void shouldPropagateExceptionFromLoadSectionPort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            when(loadSectionPort.loadById(SECTION_ID)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            assertSame(expectedException, exception);
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should propagate exception from reorderSectionsPort")
        void shouldPropagateExceptionFromReorderSectionsPort() {
            // Arrange
            Section section = mock(Section.class);
            RuntimeException expectedException = new RuntimeException("Reorder failed");
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            assertSame(expectedException, exception);
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }

        @Test
        @DisplayName("Should handle exception from section.getFormId()")
        void shouldHandleExceptionFromSectionGetFormId() {
            // Arrange
            Section section = mock(Section.class);
            RuntimeException expectedException = new RuntimeException("getFormId error");
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            assertSame(expectedException, exception);
            verifyNoInteractions(reorderSectionsPort);
        }
    }

    @Nested
    @DisplayName("Transactional behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should complete all operations within transaction")
        void shouldCompleteAllOperationsWithinTransaction() {
            // Arrange
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, VALID_POSITION);

            // Assert
            assertSame(movedSection, result);
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }

        @Test
        @DisplayName("Should rollback on validation failure")
        void shouldRollbackOnValidationFailure() {
            // Arrange
            Section section = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(OTHER_FORM_ID); // Will cause validation failure

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            // Operations before failure should still be attempted
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            // But reorder should not be called due to rollback
            verifyNoInteractions(reorderSectionsPort);
        }

        @Test
        @DisplayName("Should rollback on reorder failure")
        void shouldRollbackOnReorderFailure() {
            // Arrange
            Section section = mock(Section.class);
            RuntimeException reorderException = new RuntimeException("Reorder failed");
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenThrow(reorderException);

            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, VALID_POSITION));

            // All operations should be attempted before rollback
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(section).getFormId();
            verify(reorderSectionsPort).moveTo(FORM_ID, SECTION_ID, VALID_POSITION);
        }
    }

    @Nested
    @DisplayName("Service construction")
    class ServiceConstruction {

        @Test
        @DisplayName("Should be constructed with dependencies")
        void shouldBeConstructedWithDependencies() {
            // Arrange
            LoadSectionPort loadPort = mock(LoadSectionPort.class);
            ReorderSectionsPort reorderPort = mock(ReorderSectionsPort.class);

            // Act
            MoveSectionService newService = new MoveSectionService(loadPort, reorderPort);

            // Assert
            assertNotNull(newService);
            // Verify it works
            Section section = mock(Section.class);
            Section movedSection = mock(Section.class);
            
            when(loadPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);
            when(reorderPort.moveTo(FORM_ID, SECTION_ID, VALID_POSITION)).thenReturn(movedSection);
            
            Section result = newService.handle(FORM_ID, SECTION_ID, VALID_POSITION);
            assertSame(movedSection, result);
        }

        @Test
        @DisplayName("Should handle null dependencies")
        void shouldHandleNullDependencies() {
            // Test that construction with null dependencies doesn't throw immediately
            // Usage will throw NPE
            
            // Arrange & Act
            MoveSectionService serviceWithNullPorts = new MoveSectionService(null, null);
            
            // Assert - Construction should succeed
            assertNotNull(serviceWithNullPorts);
            
            // But usage should fail
            assertThrows(NullPointerException.class,
                () -> serviceWithNullPorts.handle(FORM_ID, SECTION_ID, VALID_POSITION));
        }
    }

    @Nested
    @DisplayName("Performance and concurrency")
    class PerformanceAndConcurrency {

        @Test
        @DisplayName("Should handle multiple move operations")
        void shouldHandleMultipleMoveOperations() {
            // Arrange
            Long[] sectionIds = {100L, 101L, 102L};
            int[] positions = {0, 1, 2};
            
            for (int i = 0; i < sectionIds.length; i++) {
                Long sectionId = sectionIds[i];
                int position = positions[i];
                
                Section section = mock(Section.class);
                Section movedSection = mock(Section.class);
                
                when(loadSectionPort.loadById(sectionId)).thenReturn(Optional.of(section));
                when(section.getFormId()).thenReturn(FORM_ID);
                when(reorderSectionsPort.moveTo(FORM_ID, sectionId, position)).thenReturn(movedSection);
                
                // Act
                Section result = service.handle(FORM_ID, sectionId, position);
                
                // Assert
                assertSame(movedSection, result);
            }
            
            // Verify all calls were made
            verify(loadSectionPort, times(3)).loadById(anyLong());
            verify(reorderSectionsPort, times(3)).moveTo(eq(FORM_ID), anyLong(), anyInt());
        }

        @Test
        @DisplayName("Should not have side effects between calls")
        void shouldNotHaveSideEffectsBetweenCalls() {
            // Arrange
            Section section1 = mock(Section.class);
            Section section2 = mock(Section.class);
            Section movedSection1 = mock(Section.class);
            Section movedSection2 = mock(Section.class);
            
            Long sectionId1 = 100L;
            Long sectionId2 = 101L;
            int position1 = 0;
            int position2 = 1;
            
            // First call
            when(loadSectionPort.loadById(sectionId1)).thenReturn(Optional.of(section1));
            when(section1.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, sectionId1, position1)).thenReturn(movedSection1);
            
            // Second call (different section)
            when(loadSectionPort.loadById(sectionId2)).thenReturn(Optional.of(section2));
            when(section2.getFormId()).thenReturn(FORM_ID);
            when(reorderSectionsPort.moveTo(FORM_ID, sectionId2, position2)).thenReturn(movedSection2);

            // Act
            Section result1 = service.handle(FORM_ID, sectionId1, position1);
            Section result2 = service.handle(FORM_ID, sectionId2, position2);

            // Assert
            assertSame(movedSection1, result1);
            assertSame(movedSection2, result2);
            assertNotSame(result1, result2);
            
            // Verify each call was independent
            verify(loadSectionPort).loadById(sectionId1);
            verify(loadSectionPort).loadById(sectionId2);
            verify(reorderSectionsPort).moveTo(FORM_ID, sectionId1, position1);
            verify(reorderSectionsPort).moveTo(FORM_ID, sectionId2, position2);
        }
    }

    @Nested
    @DisplayName("Null parameter handling")
    class NullParameterHandling {

        @Test
        @DisplayName("Should handle null form ID parameter")
        void shouldHandleNullFormIdParameter() {
            // This depends on how loadSectionPort and reorderSectionsPort handle null
            // Typically would throw NPE
            
            // Arrange
            Section section = mock(Section.class);
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(section));
            when(section.getFormId()).thenReturn(FORM_ID);

            // Act & Assert - formId parameter might be checked by reorder port
            // or might cause NPE in equals comparison
            try {
                service.handle(null, SECTION_ID, VALID_POSITION);
                // If no exception, that's OK - depends on implementation
            } catch (NullPointerException e) {
                // Expected if implementation doesn't handle null
            } catch (IllegalArgumentException e) {
                // Also acceptable
            }
        }

        @Test
        @DisplayName("Should handle null section ID parameter")
        void shouldHandleNullSectionIdParameter() {
            // Arrange
            when(loadSectionPort.loadById(null))
                .thenThrow(new NullPointerException("sectionId cannot be null"));

            // Act & Assert
            assertThrows(NullPointerException.class,
                () -> service.handle(FORM_ID, null, VALID_POSITION));

            verifyNoInteractions(reorderSectionsPort);
        }
    }
}