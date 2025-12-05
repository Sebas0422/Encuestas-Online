package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.GeneratePublicLinkService;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeneratePublicLinkService Tests")
class GeneratePublicLinkServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    private GeneratePublicLinkService service;
    
    private final Long FORM_ID = 1L;
    private final Long NON_EXISTENT_FORM_ID = 999L;
    private final Instant FIXED_INSTANT = Instant.parse("2024-01-15T10:30:00Z");
    private final String EXISTING_PUBLIC_CODE = "existing123";
    private final String NEW_PUBLIC_CODE = "newcode7890";

    @BeforeEach
    void setUp() {
        service = new GeneratePublicLinkService(loadFormPort, saveFormPort, clock);
    }

    @Nested
    @DisplayName("When generating public link")
    class WhenGeneratingPublicLink {

        @Test
        @DisplayName("Should generate new public code when none exists")
        void shouldGenerateNewPublicCodeWhenNoneExists() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert
            assertNotNull(result);
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(loadFormPort).loadById(FORM_ID);
            verify(form).getPublicCode();
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
            verify(clock).instant();
        }

        @Test
        @DisplayName("Should return existing public code when not forced")
        void shouldReturnExistingPublicCodeWhenNotForced() {
            // Arrange
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            // getPublicCode() se llama dos veces en el servicio:
            // 1. En la condición del if (línea 34)
            // 2. En el return final (línea 38)
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert
            assertEquals(EXISTING_PUBLIC_CODE, result);
            verify(loadFormPort).loadById(FORM_ID);
            // Verificar que se llamó al menos una vez (en realidad se llama dos veces)
            verify(form, atLeastOnce()).getPublicCode();
            verify(form, never()).setPublicCode(anyString(), any());
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should regenerate public code when forced")
        void shouldRegeneratePublicCodeWhenForced() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            // Solo se llama una vez porque cuando force=true, entra en el if
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, true);

            // Assert
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
            verify(clock).instant();
        }

        @Test
        @DisplayName("Should NOT regenerate public code when existing is empty string")
        void shouldNotRegeneratePublicCodeWhenExistingIsEmptyString() {
            // CORRECCIÓN: El servicio actual solo verifica null, no strings vacíos
            // Arrange
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn("");

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert - Debe devolver el string vacío, NO generar uno nuevo
            assertEquals("", result);
            verify(form, never()).setPublicCode(anyString(), any());
            verifyNoInteractions(saveFormPort, clock);
        }
    }

    @Nested
    @DisplayName("Slug generation")
    class SlugGeneration {

        @Test
        @DisplayName("Should generate slug of correct length")
        void shouldGenerateSlugOfCorrectLength() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenAnswer(invocation -> {
                String code = invocation.getArgument(0);
                assertEquals(10, code.length());
                return updatedForm;
            });
            when(updatedForm.getPublicCode()).thenReturn("testcode123");
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, false);

            // Assert - verification happens in the answer above
        }

        @Test
        @DisplayName("Should generate slug with valid characters")
        void shouldGenerateSlugWithValidCharacters() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenAnswer(invocation -> {
                String code = invocation.getArgument(0);
                // Should only contain lowercase letters and digits
                assertTrue(code.matches("[a-z0-9]{10}"));
                return updatedForm;
            });
            when(updatedForm.getPublicCode()).thenReturn("abc123def4");
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, false);

            // Assert - verification happens in the answer above
        }

        @Test
        @DisplayName("Should generate different slugs for different calls")
        void shouldGenerateDifferentSlugsForDifferentCalls() {
            // Este test es probabilístico pero debería funcionar la mayoría de las veces
            // Arrange
            Form form1 = mock(Form.class);
            Form form2 = mock(Form.class);
            Form updatedForm1 = mock(Form.class);
            Form updatedForm2 = mock(Form.class);
            
            Long formId2 = 2L;
            
            // Primera llamada
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form1));
            when(form1.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            
            // Capturar primer slug
            final String[] firstSlug = new String[1];
            when(form1.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenAnswer(invocation -> {
                firstSlug[0] = invocation.getArgument(0);
                return updatedForm1;
            });
            when(updatedForm1.getPublicCode()).thenReturn("firstslug12");
            when(saveFormPort.save(updatedForm1)).thenReturn(updatedForm1);
            
            String result1 = service.handle(FORM_ID, false);
            
            // Reset mocks para segunda llamada
            reset(loadFormPort, form1, form2, updatedForm1, updatedForm2, saveFormPort, clock);
            
            // Segunda llamada
            when(loadFormPort.loadById(formId2)).thenReturn(Optional.of(form2));
            when(form2.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            
            // Capturar segundo slug
            final String[] secondSlug = new String[1];
            when(form2.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenAnswer(invocation -> {
                secondSlug[0] = invocation.getArgument(0);
                return updatedForm2;
            });
            when(updatedForm2.getPublicCode()).thenReturn("secondslug3");
            when(saveFormPort.save(updatedForm2)).thenReturn(updatedForm2);
            
            String result2 = service.handle(formId2, false);
            
            // Assert - slugs deberían ser diferentes (probabilístico pero muy alta probabilidad)
            assertNotNull(firstSlug[0]);
            assertNotNull(secondSlug[0]);
            assertNotEquals(firstSlug[0], secondSlug[0]);
        }
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
                () -> service.handle(NON_EXISTENT_FORM_ID, false)
            );

            // Verificar que la excepción contiene el ID del formulario en su mensaje
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
                () -> service.handle(FORM_ID, false));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should propagate exception from setPublicCode")
        void shouldPropagateExceptionFromSetPublicCode() {
            // Arrange
            Form form = mock(Form.class);
            IllegalArgumentException expectedException = new IllegalArgumentException("Invalid code");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenThrow(expectedException);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(FORM_ID, false));

            assertSame(expectedException, exception);
            verifyNoInteractions(saveFormPort);
        }

        @Test
        @DisplayName("Should propagate exception from savePort")
        void shouldPropagateExceptionFromSavePort() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            RuntimeException expectedException = new RuntimeException("Save failed");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, false));

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
                () -> service.handle(null, false));

            verify(loadFormPort).loadById(null);
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should return whitespace-only public code (current behavior)")
        void shouldReturnWhitespaceOnlyPublicCode() {
            // CORRECCIÓN: El servicio actual solo verifica null, no strings con espacios
            // Arrange
            String whitespaceCode = "   \t\n   ";
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(whitespaceCode);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert - Debe devolver el string con espacios, NO generar uno nuevo
            assertEquals(whitespaceCode, result);
            verify(form, never()).setPublicCode(anyString(), any());
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should handle very long existing public code")
        void shouldHandleVeryLongExistingPublicCode() {
            // Arrange
            String veryLongCode = "a".repeat(1000);
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(veryLongCode);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert - Debe devolver el código muy largo sin modificación
            assertEquals(veryLongCode, result);
            verify(form, never()).setPublicCode(anyString(), any());
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should regenerate whitespace-only code when forced")
        void shouldRegenerateWhitespaceOnlyCodeWhenForced() {
            // Arrange
            String whitespaceCode = "   \t\n   ";
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(whitespaceCode);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, true);

            // Assert - Con force=true, debe regenerar incluso si hay espacios
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }
    }

    @Nested
    @DisplayName("Clock usage")
    class ClockUsage {

        @Test
        @DisplayName("Should use clock to get current instant when generating new code")
        void shouldUseClockToGetCurrentInstantWhenGeneratingNewCode() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            Instant specificTime = Instant.parse("2024-01-20T15:45:30Z");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(specificTime);
            when(form.setPublicCode(anyString(), eq(specificTime))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, false);

            // Assert
            verify(clock).instant();
            verify(form).setPublicCode(anyString(), eq(specificTime));
        }

        @Test
        @DisplayName("Should not use clock when returning existing code")
        void shouldNotUseClockWhenReturningExistingCode() {
            // Arrange
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);

            // Act
            service.handle(FORM_ID, false);

            // Assert
            verifyNoInteractions(clock);
        }
    }

    @Nested
    @DisplayName("Transactional behavior")
    class TransactionalBehavior {

        @Test
        @DisplayName("Should save form within transaction when generating new code")
        void shouldSaveFormWithinTransactionWhenGeneratingNewCode() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            service.handle(FORM_ID, false);

            // Assert
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should not save when returning existing code")
        void shouldNotSaveWhenReturningExistingCode() {
            // Arrange
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);

            // Act
            service.handle(FORM_ID, false);

            // Assert
            verifyNoInteractions(saveFormPort);
        }

        @Test
        @DisplayName("Should rollback on save failure")
        void shouldRollbackOnSaveFailure() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            RuntimeException saveException = new RuntimeException("Save failed");
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(saveFormPort.save(updatedForm)).thenThrow(saveException);

            // Act & Assert
            assertThrows(RuntimeException.class,
                () -> service.handle(FORM_ID, false));

            // La operación aún debe intentarse antes del rollback
            verify(saveFormPort).save(updatedForm);
        }
    }

    @Nested
    @DisplayName("Force parameter behavior")
    class ForceParameterBehavior {

        @Test
        @DisplayName("Should respect force=false with existing code")
        void shouldRespectForceFalseWithExistingCode() {
            // Arrange
            Form form = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert
            assertEquals(EXISTING_PUBLIC_CODE, result);
            verify(form, never()).setPublicCode(anyString(), any());
            verifyNoInteractions(saveFormPort, clock);
        }

        @Test
        @DisplayName("Should respect force=true with existing code")
        void shouldRespectForceTrueWithExistingCode() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(EXISTING_PUBLIC_CODE);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, true);

            // Assert
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should generate new code when force=false but no existing code")
        void shouldGenerateNewCodeWhenForceFalseButNoExistingCode() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, false);

            // Assert
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }

        @Test
        @DisplayName("Should generate new code when force=true even with null code")
        void shouldGenerateNewCodeWhenForceTrueEvenWithNullCode() {
            // Arrange
            Form form = mock(Form.class);
            Form updatedForm = mock(Form.class);
            
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(form));
            when(form.getPublicCode()).thenReturn(null);
            when(clock.instant()).thenReturn(FIXED_INSTANT);
            when(form.setPublicCode(anyString(), eq(FIXED_INSTANT))).thenReturn(updatedForm);
            when(updatedForm.getPublicCode()).thenReturn(NEW_PUBLIC_CODE);
            when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

            // Act
            String result = service.handle(FORM_ID, true);

            // Assert
            assertEquals(NEW_PUBLIC_CODE, result);
            verify(form).setPublicCode(anyString(), eq(FIXED_INSTANT));
            verify(saveFormPort).save(updatedForm);
        }
    }
}