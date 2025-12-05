package com.example.encuestas_api.forms.applicaction.usecase;


import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.Form;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.encuestas_api.forms.application.usecase.SetAccessModeService;

@ExtendWith(MockitoExtension.class)
public class SetAccessModeServiceTest {

    @Mock
    private LoadFormPort loadFormPort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;
    @InjectMocks
    private SetAccessModeService setAccessModeService;

    private final Long FORM_ID = 1L;
    private final Instant FIXED_INSTANT = Instant.parse("2023-01-01T10:00:00Z");
    private Form existingForm;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_INSTANT);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        // Create a mock form
        existingForm = Form.builder()
                .id(FORM_ID)
                .title("Test Form")
                .description("Test Description")
                .accessMode(AccessMode.PRIVATE)
                .createdAt(Instant.parse("2023-01-01T09:00:00Z"))
                .build();
    }

    @Test
    void shouldSetAccessModeSuccessfully() {
        // Arrange
        AccessMode newMode = AccessMode.PUBLIC;

        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
        when(saveFormPort.save(any(Form.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Form result = setAccessModeService.handle(FORM_ID, newMode);

        // Assert
        verify(loadFormPort).loadById(FORM_ID);
        verify(saveFormPort).save(any(Form.class));
        
        assertThat(result).isNotNull();
        assertThat(result.getAccessMode()).isEqualTo(newMode);
        assertThat(result.getId()).isEqualTo(FORM_ID);
    }
    
    @Test
    void shouldUpdateAccessModeAndTimestamp() {
        // Arrange
        AccessMode newMode = AccessMode.PUBLIC;
        
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
        when(saveFormPort.save(any(Form.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Form result = setAccessModeService.handle(FORM_ID, newMode);
        
        // Assert
        assertThat(result.getAccessMode()).isEqualTo(newMode);
    }

    @Test
    void shouldThrowExceptionWhenFormNotFound() {
        // Arrange
        AccessMode newMode = AccessMode.PUBLIC;
        
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> setAccessModeService.handle(FORM_ID, newMode))
                .isInstanceOf(FormNotFoundException.class)
                .hasMessageContaining(FORM_ID.toString());
        
        verify(loadFormPort).loadById(FORM_ID);
        verify(saveFormPort, org.mockito.Mockito.never()).save(any());
    }
    
    @Test
    void shouldHandleAllAccessModes() {
        // Test all possible access modes
        AccessMode[] allModes = AccessMode.values();
        
        for (AccessMode mode : allModes) {
            // Arrange
            when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
            when(saveFormPort.save(any(Form.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // Act
            Form result = setAccessModeService.handle(FORM_ID, mode);
            
            // Assert
            assertThat(result.getAccessMode()).isEqualTo(mode);
            
            // Reset mocks for next iteration
            org.mockito.Mockito.reset(loadFormPort, saveFormPort);
        }
    }
    
    @Test
    void shouldUseProvidedInstantForUpdate() {
        AccessMode newMode = AccessMode.PUBLIC;
        
        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
        when(saveFormPort.save(any(Form.class))).thenAnswer(invocation -> {
            Form savedForm = invocation.getArgument(0);
            return savedForm;
        });
        
        setAccessModeService.handle(FORM_ID, newMode);
        verify(clock).instant();
    }
    
    @Test
    void shouldReturnUpdatedFormFromSavePort() {
            AccessMode newMode = AccessMode.PUBLIC;
        Form updatedForm = Form.builder()
                .id(FORM_ID)
                .title("Updated Form")
                .accessMode(newMode)
                .build();

        when(loadFormPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
        when(saveFormPort.save(any(Form.class))).thenReturn(updatedForm);
        
        // Act
        Form result = setAccessModeService.handle(FORM_ID, newMode);
        
        // Assert
        assertThat(result).isSameAs(updatedForm);
    }
}