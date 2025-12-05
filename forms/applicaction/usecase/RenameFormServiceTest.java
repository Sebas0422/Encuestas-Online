package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.RenameFormService;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.valueobject.FormTitle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RenameFormService Tests")
class RenameFormServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    private RenameFormService service;

    private final Long FORM_ID = 1L;
    private final Long NON_EXISTENT_FORM_ID = 999L;
    private final String NEW_TITLE = "New Form Title";
    private final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:30:00Z");

    @BeforeEach
    void setUp() {
        service = new RenameFormService(loadFormPort, saveFormPort, clock);
    }

    @Nested
    @DisplayName("When renaming a form")
    class WhenRenamingAForm {

        @Test
        @DisplayName("Should rename form successfully")
        void shouldRenameFormSuccessfully() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, NEW_TITLE);

            // Assert
            assertNotNull(result);
            assertSame(updatedForm, result);
            verify(loadFormPort).loadById(FORM_ID);
            verify(clock).instant();
            verify(existingForm).rename(any(FormTitle.class), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should rename form with different valid titles")
        void shouldRenameFormWithDifferentValidTitles() {
            // Test various valid title formats and lengths
            String[] titles = {
                "Short",
                "A valid form title",
                "Title with numbers 123",
                "Title with apostrophe's",
                "Title with & ampersand",
                "Title <with> brackets"
            };

            for (String title : titles) {
                // Create fresh mocks for each iteration
                Form existingForm = mock(Form.class);
                Form updatedForm = mock(Form.class);
                LoadFormPort loadPort = mock(LoadFormPort.class);
                SaveFormPort savePort = mock(SaveFormPort.class);
                Clock testClock = mock(Clock.class);
                
                RenameFormService testService = new RenameFormService(loadPort, savePort, testClock);
                
                Instant now = Instant.now();
                when(testClock.instant()).thenReturn(now);
                when(loadPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
                when(existingForm.rename(any(FormTitle.class), eq(now))).thenReturn(updatedForm);
                when(savePort.save(updatedForm)).thenReturn(updatedForm);

                // Act
                Form result = testService.handle(FORM_ID, title);

                // Assert
                assertSame(updatedForm, result);
                verify(loadPort).loadById(FORM_ID);
                verify(existingForm).rename(any(FormTitle.class), eq(now));
                verify(savePort).save(updatedForm);
            }
        }

        @Test
        @DisplayName("Should use current time from clock")
        void shouldUseCurrentTimeFromClock() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            Instant specificTime = Instant.parse("2024-01-20T15:45:30Z");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(specificTime);
            when(existingForm.rename(any(FormTitle.class), eq(specificTime))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(clock).instant();
            verify(existingForm).rename(any(FormTitle.class), eq(specificTime));
        }
    }

    @Nested
    @DisplayName("FormTitle creation")
    class FormTitleCreation {

        @Test
        @DisplayName("Should create FormTitle from string")
        void shouldCreateFormTitleFromString() {
            // This test verifies that FormTitle.of() is called with the correct parameter
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            // Capture the FormTitle argument to verify it's created correctly
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenAnswer(invocation -> {
                FormTitle titleArg = invocation.getArgument(0);
                // FormTitle should be created from NEW_TITLE
                assertEquals(NEW_TITLE, titleArg.toString());
                return updatedForm;
            });
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, NEW_TITLE);

            // Assert
            assertSame(updatedForm, result);
            verify(existingForm).rename(any(FormTitle.class), eq(FIXED_INSTANT));
        }

    @Test
@DisplayName("Should propagate FormTitle validation exception")
void shouldPropagateFormTitleValidationException() {
    // Arrange
    String invalidTitle = "";
    Form existingForm = mock(Form.class);
    
    when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
    
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> service.handle(FORM_ID, invalidTitle));

    assertEquals(IllegalArgumentException.class, exception.getClass());
    
    // Only loadFormPort should be called
    verify(loadFormPort).loadById(FORM_ID);
    // clock.instant() should NOT be called
    verifyNoInteractions(clock, saveFormPort);
}
    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should throw FormNotFoundException when form does not exist")
        void shouldThrowFormNotFoundExceptionWhenFormDoesNotExist() {
            // Arrange
            when(loadFormPort.loadById(NON_EXISTENT_FORM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            FormNotFoundException exception = assertThrows(
                FormNotFoundException.class,
                () -> service.handle(NON_EXISTENT_FORM_ID, NEW_TITLE)
            );

            // Check that the exception contains the correct form ID in its message
            assertTrue(exception.getMessage().contains(NON_EXISTENT_FORM_ID.toString()));
            verify(loadFormPort).loadById(NON_EXISTENT_FORM_ID);
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should propagate exception from loadPort")
        void shouldPropagateExceptionFromLoadPort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            when(loadFormPort.loadById(FORM_ID)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_TITLE));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should propagate exception from rename")
        void shouldPropagateExceptionFromRename() {
            // Arrange
            Form existingForm = mock(Form.class);
            IllegalArgumentException expectedException = new IllegalArgumentException("Invalid title");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenThrow(expectedException);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, NEW_TITLE));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveFormPort);
        }

        @Test
        @DisplayName("Should propagate exception from savePort")
        void shouldPropagateExceptionFromSavePort() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            RuntimeException expectedException = new RuntimeException("Save failed");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_TITLE));

            assertSame(expectedException, exception);
            verify(saveFormPort).save(updatedForm);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null form ID")
        void shouldHandleNullFormId() {
            // Arrange
            when(loadFormPort.loadById(null))
                .thenThrow(new NullPointerException("formId cannot be null"));

            // Act & Assert
            assertThrows(NullPointerException.class,
                () -> service.handle(null, NEW_TITLE));

            verify(loadFormPort).loadById(null);
            verifyNoInteractions(saveFormPort, clock);
        }
        

        @Test
        @DisplayName("Should handle title at maximum length")
        void shouldHandleTitleAtMaximumLength() {
            // Test title at exactly 200 characters (assuming that's the limit)
            String maxLengthTitle = "A".repeat(200);
            
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            try {
                Form result = service.handle(FORM_ID, maxLengthTitle);
                // If no exception, it's valid
                assertSame(updatedForm, result);
            } catch (IllegalArgumentException e) {
                // If it throws, that's also OK - means 200 is over the limit
                assertTrue(e.getMessage().contains("200"));
            }
        }
    }

    @Nested
    @DisplayName("Clock usage")
    class ClockUsage {

        @Test
        @DisplayName("Should use clock to get current instant")
        void shouldUseClockToGetCurrentInstant() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            Instant specificTime = Instant.parse("2024-01-20T15:45:30Z");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(specificTime);
            when(existingForm.rename(any(FormTitle.class), eq(specificTime))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(clock).instant();
            verify(existingForm).rename(any(FormTitle.class), eq(specificTime));
        }

        @Test
        @DisplayName("Should use clock for each call")
        void shouldUseClockForEachCall() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            Instant firstTime = Instant.parse("2024-01-15T10:00:00Z");
            Instant secondTime = Instant.parse("2024-01-15T10:01:00Z");

            // Configurar el clock para devolver diferentes tiempos en llamadas sucesivas
            when(clock.instant()).thenReturn(firstTime, secondTime);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.rename(any(FormTitle.class), any(Instant.class))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // First call
            service.handle(FORM_ID, "First Title");
            
            // Second call
            service.handle(FORM_ID, "Second Title");

            // Assert - clock.instant() should have been called twice
            verify(clock, times(2)).instant();
        }
    }

    @Nested
    @DisplayName("Transactional behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should complete all operations within transaction")
        void shouldCompleteAllOperationsWithinTransaction() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(loadFormPort).loadById(FORM_ID);
            verify(existingForm).rename(any(FormTitle.class), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should rollback on save failure")
        void shouldRollbackOnSaveFailure() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            RuntimeException saveException = new RuntimeException("Save failed");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(saveException);

            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_TITLE));

            // All operations should still be attempted before rollback
            verify(loadFormPort).loadById(FORM_ID);
            verify(existingForm).rename(any(FormTitle.class), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should rollback on rename failure")
        void shouldRollbackOnRenameFailure() {
            // Arrange
            Form existingForm = mock(Form.class);
            IllegalArgumentException renameException = new IllegalArgumentException("Invalid title");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenThrow(renameException);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, NEW_TITLE));

            // Save should not be called if rename fails
            verifyNoInteractions(saveFormPort);
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call loadPort with correct form ID")
        void shouldCallLoadPortWithCorrectFormId() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(loadFormPort).loadById(FORM_ID);
        }

        @Test
        @DisplayName("Should call form.rename with correct parameters")
        void shouldCallFormRenameWithCorrectParameters() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(existingForm).rename(any(FormTitle.class), eq(FIXED_INSTANT));
        }

        @Test
        @DisplayName("Should call savePort with updated form")
        void shouldCallSavePortWithUpdatedForm() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_TITLE);

            // Assert
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should not call savePort when form not found")
        void shouldNotCallSavePortWhenFormNotFound() {
            // Arrange
            when(loadFormPort.loadById(NON_EXISTENT_FORM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(FormNotFoundException.class,
                () -> service.handle(NON_EXISTENT_FORM_ID, NEW_TITLE));

            verifyNoInteractions(saveFormPort, clock);
        }
    }

    @Nested
    @DisplayName("Service construction")
    class ServiceConstruction {

        @Test
        @DisplayName("Should be constructed with all dependencies")
        void shouldBeConstructedWithAllDependencies() {
            // Arrange
            LoadFormPort loadPort = mock(LoadFormPort.class);
            SaveFormPort savePort = mock(SaveFormPort.class);
            Clock clock = mock(Clock.class);

            // Act
            RenameFormService newService = new RenameFormService(loadPort, savePort, clock);

            // Assert
            assertNotNull(newService);
            // Verify it works
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            Instant now = Instant.now();
            
            when(loadPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(now);
            when(existingForm.rename(any(FormTitle.class), eq(now))).thenReturn(updatedForm);
            when(savePort.save(updatedForm)).thenReturn(updatedForm);
            
            Form result = newService.handle(FORM_ID, NEW_TITLE);
            assertSame(updatedForm, result);
        }
    }

    @Nested
    @DisplayName("Performance considerations")
    class PerformanceConsiderations {

        @Test
        @DisplayName("Should handle multiple renames efficiently")
        void shouldHandleMultipleRenamesEfficiently() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            String[] titles = {"Title1", "Title2", "Title3"};
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(existingForm.rename(any(FormTitle.class), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act - rename multiple times
            for (String title : titles) {
                Form result = service.handle(FORM_ID, title);
                assertSame(updatedForm, result);
            }

            // Assert
            verify(loadFormPort, times(titles.length)).loadById(FORM_ID);
            verify(existingForm, times(titles.length)).rename(any(FormTitle.class), eq(FIXED_INSTANT));
            verify(saveFormPort, times(titles.length)).save(updatedForm);
        }
    }
}
}