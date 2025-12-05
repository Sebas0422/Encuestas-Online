package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.ChangeThemeService;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.Form;
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
class ChangeThemeServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    @InjectMocks
    private ChangeThemeService service;

    @Test
    void testBasicFunctionality() {
        // Arrange
        Long formId = 1L;
        String mode = "dark";
        String color = "#3B82F6";
        Instant now = Instant.now();
        
        Form existingForm = mock(Form.class);
        Form updatedForm = mock(Form.class);
        
        when(clock.instant()).thenReturn(now);
        when(loadFormPort.loadById(formId)).thenReturn(Optional.of(existingForm));
        when(existingForm.changeTheme(any(), eq(now))).thenReturn(updatedForm);
        when(saveFormPort.save(updatedForm)).thenReturn(updatedForm);

        // Act
        Form result = service.handle(formId, mode, color);

        // Assert
        assertSame(updatedForm, result);
        verify(loadFormPort).loadById(formId);
        verify(existingForm).changeTheme(any(), eq(now));
        verify(saveFormPort).save(updatedForm);
    }
}