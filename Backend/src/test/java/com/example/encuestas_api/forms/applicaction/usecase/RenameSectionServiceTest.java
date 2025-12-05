package com.example.encuestas_api.forms.applicaction.usecase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.encuestas_api.forms.application.port.out.LoadSectionPort;
import com.example.encuestas_api.forms.application.port.out.SaveSectionPort;
import com.example.encuestas_api.forms.application.usecase.RenameSectionService;
import com.example.encuestas_api.forms.domain.model.Section;

@ExtendWith(MockitoExtension.class)
@DisplayName("RenameSectionService Tests")
class RenameSectionServiceTest {

    @Mock
    private LoadSectionPort loadSectionPort;

    @Mock
    private SaveSectionPort saveSectionPort;

    private RenameSectionService service;

    private final Long FORM_ID = 1L;
    private final Long OTHER_FORM_ID = 2L;
    private final Long SECTION_ID = 100L;
    private final Long NON_EXISTENT_SECTION_ID = 999L;
    private final String NEW_TITLE = "Nueva Sección";
    private final int SECTION_POSITION = 1;

    @BeforeEach
    void setUp() {
        service = new RenameSectionService(loadSectionPort, saveSectionPort);
    }

    @Nested
    @DisplayName("When renaming a section")
    class WhenRenamingASection {

        @Test
        @DisplayName("Should rename section successfully")
        void shouldRenameSectionSuccessfully() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, SECTION_POSITION);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(saveSectionPort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, NEW_TITLE);

            // Assert
            assertNotNull(result);
            assertEquals(NEW_TITLE, result.getTitle());
            assertEquals(SECTION_ID, result.getId());
            assertEquals(FORM_ID, result.getFormId());
            assertEquals(SECTION_POSITION, result.getPosition());
            
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(saveSectionPort).save(expectedUpdatedSection);
        }

        @Test
        @DisplayName("Should rename section with different valid titles")
        void shouldRenameSectionWithDifferentValidTitles() {
            // Arrange
            String[] titles = {
                "Sección 1",
                "Sección con espacios",
                "Sección con números 123",
                "Sección con caracteres especiales: áéíóú",
                "A".repeat(100) // título largo pero válido
            };

            for (String title : titles) {
                // Reset mocks for each iteration
                reset(loadSectionPort, saveSectionPort);
                
                Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
                Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, title, SECTION_POSITION);

                when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
                when(saveSectionPort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

                // Act
                Section result = service.handle(FORM_ID, SECTION_ID, title);

                // Assert
                assertEquals(title, result.getTitle());
                verify(loadSectionPort).loadById(SECTION_ID);
                verify(saveSectionPort).save(expectedUpdatedSection);
            }
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should throw exception when section does not exist")
        void shouldThrowExceptionWhenSectionDoesNotExist() {
            // Arrange
            when(loadSectionPort.loadById(NON_EXISTENT_SECTION_ID)).thenReturn(Optional.empty());

            // Act & Assert
            Exception exception = assertThrows(Exception.class,
                () -> service.handle(FORM_ID, NON_EXISTENT_SECTION_ID, NEW_TITLE));

            // Puede ser NoSuchElementException (de orElseThrow) o una excepción personalizada
            assertNotNull(exception);
            verify(loadSectionPort).loadById(NON_EXISTENT_SECTION_ID);
            verifyNoInteractions(saveSectionPort);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when section belongs to different form")
        void shouldThrowIllegalArgumentExceptionWhenSectionBelongsToDifferentForm() {
            // Arrange
            Section sectionFromOtherForm = Section.rehydrate(SECTION_ID, OTHER_FORM_ID, "Título Antiguo", SECTION_POSITION);
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(sectionFromOtherForm));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, NEW_TITLE));

            assertEquals("La sección no pertenece al form", exception.getMessage());
            verify(loadSectionPort).loadById(SECTION_ID);
            verifyNoInteractions(saveSectionPort);
        }

        @Test
        @DisplayName("Should accept null title (no validation in service)")
        void shouldAcceptNullTitle() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            
            // Capturar la sección guardada para verificar que acepta null
            Section[] savedSection = new Section[1];
            when(saveSectionPort.save(any(Section.class))).thenAnswer(invocation -> {
                savedSection[0] = invocation.getArgument(0);
                return savedSection[0];
            });

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, null);

            // Assert
            assertNotNull(result);
            // El servicio actual no valida títulos nulos, así que debería aceptarlo
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(saveSectionPort).save(any(Section.class));
        }

        @Test
        @DisplayName("Should accept empty title (no validation in service)")
        void shouldAcceptEmptyTitle() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            
            // Capturar la sección guardada
            Section[] savedSection = new Section[1];
            when(saveSectionPort.save(any(Section.class))).thenAnswer(invocation -> {
                savedSection[0] = invocation.getArgument(0);
                return savedSection[0];
            });

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, "");

            // Assert
            assertNotNull(result);
            // El servicio actual no valida títulos vacíos
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(saveSectionPort).save(any(Section.class));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null form ID")
        void shouldHandleNullFormId() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));

            // Act & Assert
            // Si formId es null, la comparación !s.getFormId().equals(formId) podría lanzar NullPointerException
            assertThrows(Exception.class,
                () -> service.handle(null, SECTION_ID, NEW_TITLE));
            
            verify(loadSectionPort).loadById(SECTION_ID);
            verifyNoInteractions(saveSectionPort);
        }

        @Test
        @DisplayName("Should handle null section ID")
        void shouldHandleNullSectionId() {
            // Act & Assert
            // loadSectionPort.loadById(null) podría lanzar excepción
            assertThrows(Exception.class,
                () -> service.handle(FORM_ID, null, NEW_TITLE));
            
            // No debería llamar a saveSectionPort
            verifyNoInteractions(saveSectionPort);
        }

        @Test
        @DisplayName("Should preserve section position when renaming")
        void shouldPreserveSectionPositionWhenRenaming() {
            // Arrange
            int originalPosition = 3;
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", originalPosition);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, originalPosition);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(saveSectionPort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

            // Act
            Section result = service.handle(FORM_ID, SECTION_ID, NEW_TITLE);

            // Assert
            assertEquals(originalPosition, result.getPosition());
            verify(loadSectionPort).loadById(SECTION_ID);
            verify(saveSectionPort).save(expectedUpdatedSection);
        }
    }

    @Nested
    @DisplayName("Transactional behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should propagate exception from loadPort")
        void shouldPropagateExceptionFromLoadPort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            when(loadSectionPort.loadById(SECTION_ID)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, NEW_TITLE));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveSectionPort);
        }

        @Test
        @DisplayName("Should propagate exception from savePort")
        void shouldPropagateExceptionFromSavePort() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, SECTION_POSITION);
            
            RuntimeException expectedException = new RuntimeException("Save failed");
            
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(saveSectionPort.save(expectedUpdatedSection)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, SECTION_ID, NEW_TITLE));

            assertSame(expectedException, exception);
            verify(loadSectionPort).loadById(SECTION_ID);
        }

        @Test
        @DisplayName("Should not save when validation fails")
        void shouldNotSaveWhenValidationFails() {
            // Arrange - Section belongs to different form
            Section sectionFromOtherForm = Section.rehydrate(SECTION_ID, OTHER_FORM_ID, "Título Antiguo", SECTION_POSITION);
            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(sectionFromOtherForm));

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, SECTION_ID, NEW_TITLE));

            verify(loadSectionPort).loadById(SECTION_ID);
            verifyNoInteractions(saveSectionPort);
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call loadPort with correct section ID")
        void shouldCallLoadPortWithCorrectSectionId() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, SECTION_POSITION);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(saveSectionPort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

            // Act
            service.handle(FORM_ID, SECTION_ID, NEW_TITLE);

            // Assert
            verify(loadSectionPort).loadById(SECTION_ID);
        }

        @Test
        @DisplayName("Should call savePort with updated section")
        void shouldCallSavePortWithUpdatedSection() {
            // Arrange
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, SECTION_POSITION);

            when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(saveSectionPort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

            // Act
            service.handle(FORM_ID, SECTION_ID, NEW_TITLE);

            // Assert
            verify(saveSectionPort).save(expectedUpdatedSection);
        }

        @Test
        @DisplayName("Should not call savePort when section not found")
        void shouldNotCallSavePortWhenSectionNotFound() {
            // Arrange
            when(loadSectionPort.loadById(NON_EXISTENT_SECTION_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(Exception.class,
                () -> service.handle(FORM_ID, NON_EXISTENT_SECTION_ID, NEW_TITLE));

            verifyNoInteractions(saveSectionPort);
        }
    }

    @Nested
    @DisplayName("Service construction")
    class ServiceConstruction {

        @Test
        @DisplayName("Should be constructed with all dependencies")
        void shouldBeConstructedWithAllDependencies() {
            // Arrange
            LoadSectionPort loadPort = mock(LoadSectionPort.class);
            SaveSectionPort savePort = mock(SaveSectionPort.class);

            // Act
            RenameSectionService newService = new RenameSectionService(loadPort, savePort);

            // Assert
            assertNotNull(newService);
            // Verify it works
            Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
            Section expectedUpdatedSection = Section.rehydrate(SECTION_ID, FORM_ID, NEW_TITLE, SECTION_POSITION);

            when(loadPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
            when(savePort.save(expectedUpdatedSection)).thenReturn(expectedUpdatedSection);

            Section result = newService.handle(FORM_ID, SECTION_ID, NEW_TITLE);
            assertEquals(NEW_TITLE, result.getTitle());
        }
    }

    @Test
    @DisplayName("Should rehydrate section with same ID, formId and position")
    void shouldRehydrateSectionWithSameIdFormIdAndPosition() {
        // Arrange
        Section existingSection = Section.rehydrate(SECTION_ID, FORM_ID, "Título Antiguo", SECTION_POSITION);
        
        when(loadSectionPort.loadById(SECTION_ID)).thenReturn(Optional.of(existingSection));
        
        // Capturar la sección que se pasa a savePort
        Section[] savedSection = new Section[1];
        when(saveSectionPort.save(any(Section.class))).thenAnswer(invocation -> {
            savedSection[0] = invocation.getArgument(0);
            return savedSection[0];
        });

        // Act
        service.handle(FORM_ID, SECTION_ID, NEW_TITLE);

        // Assert
        assertNotNull(savedSection[0]);
        assertEquals(SECTION_ID, savedSection[0].getId());
        assertEquals(FORM_ID, savedSection[0].getFormId());
        assertEquals(SECTION_POSITION, savedSection[0].getPosition());
        assertEquals(NEW_TITLE, savedSection[0].getTitle());
    }
}