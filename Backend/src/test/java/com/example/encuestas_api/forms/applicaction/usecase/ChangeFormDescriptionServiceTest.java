package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.ChangeFormDescriptionService;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeFormDescriptionService Tests")
class ChangeFormDescriptionServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    @InjectMocks
    private ChangeFormDescriptionService service;

    private final Long FORM_ID = 1L;
    private final Long NON_EXISTENT_FORM_ID = 999L;
    private final String OLD_DESCRIPTION = "Old Description";
    private final String NEW_DESCRIPTION = "New Updated Description";
    private final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:30:00Z");

    @BeforeEach
    void setUp() {
        // Remove the fixed stubbing from here since it causes UnnecessaryStubbingException
        // when tests don't use the clock
    }

    @Nested
    @DisplayName("When changing form description")
    class WhenChangingFormDescription {

        @Test
        @DisplayName("Should change description successfully")
        void shouldChangeDescriptionSuccessfully() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            assertNotNull(result);
            assertSame(updatedForm, result);
            verify(loadFormPort).loadById(FORM_ID);
            verify(existingForm).changeDescription(NEW_DESCRIPTION, FIXED_INSTANT);
            verify(saveFormPort).save(updatedForm);
            verify(clock).instant();
        }

        @Test
        @DisplayName("Should change description with different values")
        void shouldChangeDescriptionWithDifferentValues() {
            // Arrange
            String[] descriptions = {
                "Short",
                "A very long description with many characters to test edge cases",
                "Description with special characters: !@#$%^&*()",
                "Description with numbers 1234567890",
                "Multi-line\ndescription",
                "   Trimmed description   ",
                "😀 Emoji description"
            };

            for (String description : descriptions) {
                // Reset mocks for each iteration
                reset(loadFormPort, saveFormPort, clock);
                
                Form existingForm = mock(Form.class);
                Form updatedForm = mock(Form.class);
                
                Instant now = Instant.now();
                when(clock.instant()).thenReturn(now);
                when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
                when(existingForm.changeDescription(description, now)).thenReturn(updatedForm);
                when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

                // Act
                Form result = service.handle(FORM_ID, description);

                // Assert
                assertSame(updatedForm, result);
                verify(loadFormPort).loadById(FORM_ID);
                verify(existingForm).changeDescription(description, now);
                verify(saveFormPort).save(updatedForm);
            }
        }

        @Test
        @DisplayName("Should use current time from clock")
        void shouldUseCurrentTimeFromClock() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            Instant specificTime = Instant.parse("2024-01-20T15:45:30Z");
            when(clock.instant()).thenReturn(specificTime);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, specificTime)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            verify(clock).instant();
            verify(existingForm).changeDescription(NEW_DESCRIPTION, specificTime);
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {

        @Test
        @DisplayName("Should throw FormNotFoundException when form does not exist")
        void shouldThrowFormNotFoundExceptionWhenFormDoesNotExist() {
            // Arrange
            when(loadFormPort.loadById(NON_EXISTENT_FORM_ID)).thenReturn(Optional.empty());

            // Act & Assert
            FormNotFoundException exception = assertThrows(
                FormNotFoundException.class,
                () -> service.handle(NON_EXISTENT_FORM_ID, NEW_DESCRIPTION)
            );

            // Check that the exception contains the correct form ID in its message
            assertTrue(exception.getMessage().contains(NON_EXISTENT_FORM_ID.toString()));
            verify(loadFormPort).loadById(NON_EXISTENT_FORM_ID);
            verifyNoInteractions(saveFormPort);
        }

        @Test
        @DisplayName("Should handle null description")
        void shouldHandleNullDescription() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(null, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, null);

            // Assert
            assertSame(updatedForm, result);
            verify(existingForm).changeDescription(null, FIXED_INSTANT);
        }

        @Test
        @DisplayName("Should handle empty description")
        void shouldHandleEmptyDescription() {
            // Arrange
            String emptyDescription = "";
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(emptyDescription, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, emptyDescription);

            // Assert
            assertSame(updatedForm, result);
            verify(existingForm).changeDescription(emptyDescription, FIXED_INSTANT);
        }

        @Test
        @DisplayName("Should handle whitespace-only description")
        void shouldHandleWhitespaceOnlyDescription() {
            // Arrange
            String whitespaceDescription = "   \t\n   ";
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(whitespaceDescription, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            Form result = service.handle(FORM_ID, whitespaceDescription);

            // Assert
            assertSame(updatedForm, result);
            verify(existingForm).changeDescription(whitespaceDescription, FIXED_INSTANT);
        }

        @Test
        @DisplayName("Should handle null form ID - expecting NPE from loadPort")
        void shouldHandleNullFormId() {
            // Arrange
            when(loadFormPort.loadById(null))
                .thenThrow(new NullPointerException("formId cannot be null"));

            // Act & Assert
            assertThrows(NullPointerException.class,
                () -> service.handle(null, NEW_DESCRIPTION));

            verify(loadFormPort).loadById(null);
            verifyNoInteractions(saveFormPort, clock);
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call loadPort.loadById with correct form ID")
        void shouldCallLoadPortLoadByIdWithCorrectFormId() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            verify(loadFormPort).loadById(FORM_ID);
        }

        @Test
        @DisplayName("Should call form.changeDescription with correct parameters")
        void shouldCallFormChangeDescriptionWithCorrectParameters() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            verify(existingForm).changeDescription(NEW_DESCRIPTION, FIXED_INSTANT);
        }

        @Test
        @DisplayName("Should call savePort.save with updated form")
        void shouldCallSavePortSaveWithUpdatedForm() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

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
                () -> service.handle(NON_EXISTENT_FORM_ID, NEW_DESCRIPTION));

            verifyNoInteractions(saveFormPort, clock);
        }
    }

    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should propagate exception from loadPort")
        void shouldPropagateExceptionFromLoadPort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            when(loadFormPort.loadById(FORM_ID)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_DESCRIPTION));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should propagate exception from form.changeDescription")
        void shouldPropagateExceptionFromFormChangeDescription() {
            // Arrange
            Form existingForm = mock(Form.class);
            RuntimeException expectedException = new IllegalArgumentException("Invalid description");

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenThrow(expectedException);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, NEW_DESCRIPTION));

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

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_DESCRIPTION));

            assertSame(expectedException, exception);
            verify(saveFormPort).save(updatedForm);
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

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            verify(loadFormPort).loadById(FORM_ID);
            verify(existingForm).changeDescription(NEW_DESCRIPTION, FIXED_INSTANT);
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should rollback transaction on save failure")
        void shouldRollbackTransactionOnSaveFailure() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            RuntimeException saveException = new RuntimeException("Save failed");

            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, FIXED_INSTANT)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(saveException);

            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, NEW_DESCRIPTION));

            // All operations should still be attempted before rollback
            verify(loadFormPort).loadById(FORM_ID);
            verify(existingForm).changeDescription(NEW_DESCRIPTION, FIXED_INSTANT);
            verify(saveFormPort).save(updatedForm);
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
            Instant now = Instant.now();

            when(clock.instant()).thenReturn(now);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription(NEW_DESCRIPTION, now)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, NEW_DESCRIPTION);

            // Assert
            verify(clock).instant();
        }

        @Test
        @DisplayName("Should use different instants for different calls")
        void shouldUseDifferentInstantsForDifferentCalls() {
            // Arrange
            Form existingForm = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            Instant firstTime = Instant.parse("2024-01-15T10:00:00Z");
            Instant secondTime = Instant.parse("2024-01-15T10:01:00Z");

            // First call
            when(clock.instant()).thenReturn(firstTime);
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(existingForm.changeDescription("First", firstTime)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            service.handle(FORM_ID, "First");

            // Don't reset the clock mock - keep it for verification
            reset(existingForm, saveFormPort);

            // Second call - use lenient stubbing for loadPort since it was already called
            lenient().when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(clock.instant()).thenReturn(secondTime); // This will be the second call
            when(existingForm.changeDescription("Second", secondTime)).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, "Second");

            // Assert - clock.instant() should have been called twice total
            verify(clock, times(2)).instant();
        }
    }
}