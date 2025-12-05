package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.forms.application.port.out.CheckCampaignExistsPort;
import com.example.encuestas_api.forms.application.port.out.ExistsFormByTitleInCampaignPort;
import com.example.encuestas_api.forms.application.port.out.SaveFormPort;
import com.example.encuestas_api.forms.application.usecase.CreateFormService;
import com.example.encuestas_api.forms.domain.exception.FormAlreadyExistsException;
import com.example.encuestas_api.forms.domain.model.AccessMode;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFormService Tests")
class CreateFormServiceTest {

    @Mock
    private CheckCampaignExistsPort campaignExistsPort;

    @Mock
    private ExistsFormByTitleInCampaignPort existsTitlePort;

    @Mock
    private SaveFormPort saveFormPort;

    @Mock
    private Clock clock;

    @InjectMocks
    private CreateFormService service;

    private final Long CAMPAIGN_ID = 1L;
    private final Long NON_EXISTENT_CAMPAIGN_ID = 999L;
    private final String TITLE = "Nuevo Formulario";
    private final String DESCRIPTION = "Descripción del formulario";
    private final String COVER_URL = "https://example.com/cover.jpg";
    private final Instant NOW = Instant.parse("2024-01-15T10:30:00Z");
    private final Instant OPEN_AT = Instant.parse("2024-01-16T09:00:00Z");
    private final Instant CLOSE_AT = Instant.parse("2024-01-31T23:59:59Z");

    @Test
    @DisplayName("Should create form with basic parameters")
    void shouldCreateFormWithBasicParameters() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(campaignExistsPort).existsById(CAMPAIGN_ID);
        verify(existsTitlePort).exists(CAMPAIGN_ID, TITLE);
        verify(saveFormPort).save(any(Form.class));
        verify(clock).instant();
    }

    @Test
    @DisplayName("Should create form with all boolean flags true")
    void shouldCreateFormWithAllBooleanFlagsTrue() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "light", "#FF0000",
            AccessMode.PRIVATE, OPEN_AT, CLOSE_AT,
            "ONE_PER_USER", null,
            true, true, true,
            true, true, true, true
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should create form with LIMITED_N response limit")
    void shouldCreateFormWithLimitedNResponseLimit() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);
        Integer limitedN = 5;

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "LIMITED_N", limitedN,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }


    @Test
    @DisplayName("Should throw exception for non-existent campaign")
    void shouldThrowExceptionForNonExistentCampaign() {
        // Arrange
        when(campaignExistsPort.existsById(NON_EXISTENT_CAMPAIGN_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> service.handle(
            NON_EXISTENT_CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        )).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("campaignId inválido");

        verifyNoInteractions(existsTitlePort);
        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should throw FormAlreadyExistsException for duplicate title")
    void shouldThrowFormAlreadyExistsExceptionForDuplicateTitle() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        )).isInstanceOf(FormAlreadyExistsException.class)
          .hasMessageContaining(TITLE);

        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should trim title before checking existence")
    void shouldTrimTitleBeforeCheckingExistence() {
        // Arrange
        String titleWithSpaces = "  " + TITLE + "  ";
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, titleWithSpaces, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(existsTitlePort).exists(CAMPAIGN_ID, TITLE);
    }

    @Test
    @DisplayName("Should handle null theme mode with default theme")
    void shouldHandleNullThemeModeWithDefaultTheme() {
        // Arrange
        String nullThemeMode = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            nullThemeMode, "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle null access mode with default PUBLIC")
    void shouldHandleNullAccessModeWithDefaultPublic() {
        // Arrange
        AccessMode nullAccessMode = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            nullAccessMode, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle null response limit mode with default UNLIMITED")
    void shouldHandleNullResponseLimitModeWithDefaultUnlimited() {
        // Arrange
        String nullResponseLimitMode = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            nullResponseLimitMode, null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle empty response limit mode with default UNLIMITED")
    void shouldHandleEmptyResponseLimitModeWithDefaultUnlimited() {
        // Arrange
        String emptyResponseLimitMode = "";
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            emptyResponseLimitMode, null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle LIMITED_N with null limitedN parameter")
    void shouldHandleLimitedNWithNullLimitedNParameter() {
        // Arrange
        Integer nullLimitedN = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "LIMITED_N", nullLimitedN,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle null openAt")
    void shouldHandleNullOpenAt() {
        // Arrange
        Instant nullOpenAt = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, nullOpenAt, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle null closeAt")
    void shouldHandleNullCloseAt() {
        // Arrange
        Instant nullCloseAt = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, nullCloseAt,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should handle both null openAt and closeAt")
    void shouldHandleBothNullOpenAtAndCloseAt() {
        // Arrange
        Instant nullOpenAt = null;
        Instant nullCloseAt = null;
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        Form result = service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, nullOpenAt, nullCloseAt,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        assertThat(result).isSameAs(savedForm);
        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should propagate exception from campaignExistsPort")
    void shouldPropagateExceptionFromCampaignExistsPort() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Database error");
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        )).isSameAs(expectedException);

        verifyNoInteractions(existsTitlePort);
        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should propagate exception from existsTitlePort")
    void shouldPropagateExceptionFromExistsTitlePort() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Database error");
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        )).isSameAs(expectedException);

        verifyNoInteractions(saveFormPort);
    }

    @Test
    @DisplayName("Should propagate exception from saveFormPort")
    void shouldPropagateExceptionFromSaveFormPort() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        RuntimeException expectedException = new RuntimeException("Save failed");
        when(saveFormPort.save(any(Form.class))).thenThrow(expectedException);

        // Act & Assert
        assertThatThrownBy(() -> service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        )).isSameAs(expectedException);

        verify(saveFormPort).save(any(Form.class));
    }

    @Test
    @DisplayName("Should use clock for current timestamp")
    void shouldUseClockForCurrentTimestamp() {
        // Arrange
        when(campaignExistsPort.existsById(CAMPAIGN_ID)).thenReturn(true);
        when(existsTitlePort.exists(CAMPAIGN_ID, TITLE)).thenReturn(false);
        when(clock.instant()).thenReturn(NOW);
        Form savedForm = mock(Form.class);
        when(saveFormPort.save(any(Form.class))).thenReturn(savedForm);

        // Act
        service.handle(
            CAMPAIGN_ID, TITLE, DESCRIPTION, COVER_URL,
            "dark", "#3B82F6",
            AccessMode.PUBLIC, OPEN_AT, CLOSE_AT,
            "UNLIMITED", null,
            false, false, false,
            false, false, false, false
        );

        // Assert
        verify(clock).instant();
    }
}