package com.example.encuestas_api.forms.applicaction.usecase;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.RescheduleFormService;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.valueobject.AvailabilityWindow;

@ExtendWith(MockitoExtension.class)
@DisplayName("RescheduleFormService Tests")
class RescheduleFormServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    @Mock
    private Form mockForm;

    @Mock
    private Form mockUpdatedForm;

    private RescheduleFormService service;
    
    private final Long FORM_ID = 1L;
    private final Long NON_EXISTENT_FORM_ID = 999L;
    private final Instant CURRENT_TIME = Instant.parse("2024-01-15T10:00:00Z");
    private final Instant OPEN_AT = Instant.parse("2024-01-16T09:00:00Z");
    private final Instant CLOSE_AT = Instant.parse("2024-01-20T18:00:00Z");

    @BeforeEach
    void setUp() {
        service = new RescheduleFormService(loadFormPort, saveFormPort, clock);
    }

    @Test
    @DisplayName("Should reschedule form successfully")
    void shouldRescheduleFormSuccessfully() {
        // Arrange
        when(clock.instant()).thenReturn(CURRENT_TIME);
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        when(mockForm.reschedule(any(AvailabilityWindow.class), any(Instant.class))).thenReturn(mockUpdatedForm);
        when(saveFormPort.save(mockUpdatedForm)).thenReturn(mockUpdatedForm);

        // Act
        Form result = service.handle(FORM_ID, OPEN_AT, CLOSE_AT);

        // Assert
        assertNotNull(result);
        assertSame(mockUpdatedForm, result);
        verify(loadFormPort).loadById(FORM_ID);
        verify(mockForm).reschedule(any(AvailabilityWindow.class), eq(CURRENT_TIME));
        verify(saveFormPort).save(mockUpdatedForm);
    }

    @Test
    @DisplayName("Should throw FormNotFoundException when form does not exist")
    void shouldThrowFormNotFoundExceptionWhenFormDoesNotExist() {
        // Arrange
        when(loadFormPort.loadById(NON_EXISTENT_FORM_ID)).thenReturn(Optional.empty());

        // Act & Assert
        FormNotFoundException exception = assertThrows(FormNotFoundException.class,
            () -> service.handle(NON_EXISTENT_FORM_ID, OPEN_AT, CLOSE_AT));

        // Usar el mensaje real que aparece en el error
        assertEquals("Form id=999 no encontrado", exception.getMessage());
        verify(loadFormPort).loadById(NON_EXISTENT_FORM_ID);
        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should use current time from clock")
    void shouldUseCurrentTimeFromClock() {
        // Arrange
        when(clock.instant()).thenReturn(CURRENT_TIME);
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        when(mockForm.reschedule(any(AvailabilityWindow.class), any(Instant.class))).thenReturn(mockUpdatedForm);
        when(saveFormPort.save(mockUpdatedForm)).thenReturn(mockUpdatedForm);

        // Act
        service.handle(FORM_ID, OPEN_AT, CLOSE_AT);

        // Assert
        verify(clock).instant(); // Verificar que se usa el clock
        verify(mockForm).reschedule(any(AvailabilityWindow.class), eq(CURRENT_TIME));
    }

    @Test
    @DisplayName("Should propagate exception from loadPort")
    void shouldPropagateExceptionFromLoadPort() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Database error");
        when(loadFormPort.loadById(FORM_ID)).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.handle(FORM_ID, OPEN_AT, CLOSE_AT));

        assertSame(expectedException, exception);
        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should propagate exception from savePort")
    void shouldPropagateExceptionFromSavePort() {
        // Arrange
        when(clock.instant()).thenReturn(CURRENT_TIME);
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        when(mockForm.reschedule(any(AvailabilityWindow.class), any(Instant.class))).thenReturn(mockUpdatedForm);
        
        RuntimeException expectedException = new RuntimeException("Save failed");
        when(saveFormPort.save(mockUpdatedForm)).thenThrow(expectedException);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> service.handle(FORM_ID, OPEN_AT, CLOSE_AT));

        assertSame(expectedException, exception);
        verify(loadFormPort).loadById(FORM_ID);
        verify(mockForm).reschedule(any(AvailabilityWindow.class), any(Instant.class));
    }

    @Test
    @DisplayName("Should create AvailabilityWindow with provided dates")
    void shouldCreateAvailabilityWindowWithProvidedDates() {
        // Arrange
        when(clock.instant()).thenReturn(CURRENT_TIME);
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        when(mockForm.reschedule(any(AvailabilityWindow.class), any(Instant.class))).thenReturn(mockUpdatedForm);
        when(saveFormPort.save(mockUpdatedForm)).thenReturn(mockUpdatedForm);

        // Act
        service.handle(FORM_ID, OPEN_AT, CLOSE_AT);

        // Assert
        verify(mockForm).reschedule(argThat(window -> 
            window != null && 
            window.openAt().equals(OPEN_AT) && 
            window.closeAt().equals(CLOSE_AT)
        ), eq(CURRENT_TIME));
    }

    @Test
    @DisplayName("Should handle null form ID")
    void shouldHandleNullFormId() {
        // Act & Assert - El servicio debe lanzar NullPointerException
        assertThrows(NullPointerException.class,
            () -> service.handle(null, OPEN_AT, CLOSE_AT));
    }

    @Test
    @DisplayName("Should handle null openAt")
    void shouldHandleNullOpenAt() {
        // Arrange
        // NO hacer mock de reschedule aquí - dejar que el flujo natural ocurra
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        
        // Act & Assert - El servicio debe lanzar NullPointerException
        // Nota: No se necesita verificar el mock de reschedule
        assertThrows(NullPointerException.class,
            () -> service.handle(FORM_ID, null, CLOSE_AT));
        
        verify(loadFormPort).loadById(FORM_ID);
    }

    @Test
    @DisplayName("Should handle null closeAt")
    void shouldHandleNullCloseAt() {
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(mockForm));
        
        assertThrows(NullPointerException.class,
            () -> service.handle(FORM_ID, OPEN_AT, null));
        
        verify(loadFormPort).loadById(FORM_ID);
    }
}