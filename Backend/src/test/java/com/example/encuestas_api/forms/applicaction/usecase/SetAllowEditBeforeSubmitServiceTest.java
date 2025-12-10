package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.LoadFormPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.domain.exception.FormNotFoundException;
import com.example.encuestas_api.forms.domain.model.AccessMode;
import com.example.encuestas_api.forms.domain.model.Form;
import com.example.encuestas_api.forms.domain.model.FormStatus;
import com.example.encuestas_api.forms.domain.model.Theme;
import com.example.encuestas_api.forms.domain.valueobject.FormTitle;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.encuestas_api.forms.application.usecase.SetAllowEditBeforeSubmitService;

@ExtendWith(MockitoExtension.class)
public class SetAllowEditBeforeSubmitServiceTest {

    @Mock
    private LoadFormPort loadPort;

    @Mock
    private SaveFormPort savePort;

    @Mock
    private Clock clock;
    
    @InjectMocks
    private SetAllowEditBeforeSubmitService setAllowEditBeforeSubmitService;

    private final Long FORM_ID = 1L;
    private final Instant FIXED_INSTANT = Instant.parse("2023-01-01T10:00:00Z");
    private Form existingForm;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(FIXED_INSTANT);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        
        // Create a Form with minimal required fields
        // Adjust based on your actual Form constructor
        existingForm = new Form(
            FORM_ID,                    // Long id
            1L,                         // Long ownerId
            FormTitle.of("Test Form"),  // FormTitle title
            "Test Description",         // String description
            null,                       // String welcomeMessage
            null,                       // String thankYouMessage
            Theme.defaultTheme(),       // Theme theme
            AccessMode.PRIVATE,         // AccessMode accessMode
            null,                       // AvailabilityWindow availabilityWindow
            null,                       // ResponseLimitPolicy responseLimitPolicy
            false,                      // boolean allowEditBeforeSubmit
            false,                      // boolean allowMultipleResponses
            false,                      // boolean isAnonymous
            null,                       // PresentationOptions presentationOptions
            FormStatus.DRAFT,           // FormStatus status
            Instant.parse("2023-01-01T09:00:00Z"), // Instant createdAt
            null                        // Instant updatedAt
        );
    }

    @Test
    void shouldSetAllowEditBeforeSubmitSuccessfully() {
        // Arrange
        boolean newValue = true;

        when(loadPort.loadById(FORM_ID)).thenReturn(Optional.of(existingForm));
        when(savePort.save(any(Form.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Form result = setAllowEditBeforeSubmitService.handle(FORM_ID, newValue);

        // Assert
        verify(loadPort).loadById(FORM_ID);
        verify(savePort).save(any(Form.class));
        
        assertThat(result).isNotNull();
        // Assuming your Form has a getAllowEditBeforeSubmit() method
        // You might need to check the actual method name
        assertThat(result.getAllowEditBeforeSubmit()).isEqualTo(newValue);
    }
    
    @Test
    void shouldThrowExceptionWhenFormNotFound() {
        // Arrange
        boolean newValue = true;
        
        when(loadPort.loadById(FORM_ID)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> setAllowEditBeforeSubmitService.handle(FORM_ID, newValue))
                .isInstanceOf(FormNotFoundException.class)
                .hasMessageContaining(FORM_ID.toString());
        
        verify(loadPort).loadById(FORM_ID);
        verify(savePort, org.mockito.Mockito.never()).save(any());
    }
}