package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.out.ExistsCampaignByNamePort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.AddCampaignMemberService;
import com.example.encuestas_api.campaigns.application.usecase.CreateCampaignService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignAlreadyExistsException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCampaignServiceTest {
    
    @Mock
    private ExistsCampaignByNamePort existsByNamePort;
    
    @Mock
    private SaveCampaignPort saveCampaignPort;
    
    @Mock
    private Clock clock;
    
    @Mock
    private AddCampaignMemberService addCampaignMemberService;
    
    @InjectMocks
    private CreateCampaignService createCampaignService;
    
    private final Instant fixedInstant = Instant.parse("2024-01-01T10:00:00Z");
    private final LocalDate startDate = LocalDate.of(2024, 1, 2);
    private final LocalDate endDate = LocalDate.of(2024, 1, 31);

    @Test
    void shouldCreateCampaignSuccessfully() {
        // Given
        String name = "Test Campaign";
        String description = "Test Description";
        Long userId = 1L;
        
        when(existsByNamePort.existsByNameIgnoreCase(name)).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        
        Campaign savedCampaign = Campaign.createNew(
            CampaignName.of(name), 
            description, 
            startDate, 
            endDate, 
            fixedInstant
        );
        
        when(saveCampaignPort.save(any(Campaign.class))).thenReturn(savedCampaign);
        
        // When
        Campaign result = createCampaignService.handle(
            name, description, startDate, endDate, userId
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName().getValue()).isEqualTo(name);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getStartDate()).isEqualTo(startDate);
        assertThat(result.getEndDate()).isEqualTo(endDate);
        assertThat(result.getStatus()).isEqualTo(CampaignStatus.draft);
        
        verify(existsByNamePort).existsByNameIgnoreCase(name);
        verify(saveCampaignPort).save(any(Campaign.class));
        verify(addCampaignMemberService).handle(any(), eq(userId), eq(CampaignMemberRole.CREATOR));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNameAlreadyExists() {
        // Given
        String name = "Existing Campaign";
        
        when(existsByNamePort.existsByNameIgnoreCase(name)).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> 
            createCampaignService.handle(name, "Desc", startDate, endDate, 1L)
        ).isInstanceOf(CampaignAlreadyExistsException.class);
        
        verify(saveCampaignPort, never()).save(any());
        verify(addCampaignMemberService, never()).handle(any(), any(), any());
    }

    @Test
    void shouldTrimCampaignName() {
        // Given
        String nameWithSpaces = "  Test Campaign  ";
        String trimmedName = "Test Campaign";
        
        when(existsByNamePort.existsByNameIgnoreCase(trimmedName)).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        when(saveCampaignPort.save(any(Campaign.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)
        );
        
        // When
        Campaign result = createCampaignService.handle(
            nameWithSpaces, "Desc", startDate, endDate, 1L
        );
        
        // Then
        assertThat(result.getName().getValue()).isEqualTo(trimmedName);
    }

    @Test
    void shouldHandleNullNameByThrowingException() {
        // Given - El servicio convierte null a "" 
        when(existsByNamePort.existsByNameIgnoreCase("")).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        
        // Cuando CampaignName.of("") lanza IllegalArgumentException dentro de Campaign.createNew()
        // saveCampaignPort.save() nunca se llamará
        
        // When & Then
        assertThatThrownBy(() -> 
            createCampaignService.handle(null, "Desc", startDate, endDate, 1L)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("El nombre de la campaign es requerido");
        
        verify(existsByNamePort).existsByNameIgnoreCase("");
        verify(saveCampaignPort, never()).save(any(Campaign.class));
        verify(addCampaignMemberService, never()).handle(any(), any(), any());
    }

    @Test
    void shouldHandleEmptyNameByThrowingException() {
        // Given
        when(existsByNamePort.existsByNameIgnoreCase("")).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        
        // When & Then
        assertThatThrownBy(() -> 
            createCampaignService.handle("", "Desc", startDate, endDate, 1L)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("El nombre de la campaign es requerido");
        
        verify(existsByNamePort).existsByNameIgnoreCase("");
        verify(saveCampaignPort, never()).save(any());
        verify(addCampaignMemberService, never()).handle(any(), any(), any());
    }

    @Test
    void shouldHandleBlankNameByThrowingException() {
        // Given
        when(existsByNamePort.existsByNameIgnoreCase("")).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        
        // When & Then
        assertThatThrownBy(() -> 
            createCampaignService.handle("   ", "Desc", startDate, endDate, 1L)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("El nombre de la campaign es requerido");
        
        verify(existsByNamePort).existsByNameIgnoreCase("");
        verify(saveCampaignPort, never()).save(any());
        verify(addCampaignMemberService, never()).handle(any(), any(), any());
    }

    @Test
    void shouldHandleNullUserIdByThrowingException() {
        // Given
        String name = "Test Campaign";
        
        when(existsByNamePort.existsByNameIgnoreCase(name)).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        
        // Creamos una campaña mock para retornar
        Campaign mockCampaign = mock(Campaign.class);
        when(mockCampaign.getId()).thenReturn(1L);
        when(saveCampaignPort.save(any(Campaign.class))).thenReturn(mockCampaign);
        
        // Configurar que addCampaignMemberService lance excepción cuando userId es null
        doThrow(new NullPointerException("userId cannot be null"))
            .when(addCampaignMemberService).handle(any(), eq(null), any());
        
        // When & Then
        assertThatThrownBy(() -> 
            createCampaignService.handle(name, "Description", startDate, endDate, null)
        ).isInstanceOf(NullPointerException.class);
        
        verify(existsByNamePort).existsByNameIgnoreCase(name);
        verify(saveCampaignPort).save(any(Campaign.class));
        verify(addCampaignMemberService).handle(eq(1L), eq(null), eq(CampaignMemberRole.CREATOR));
    }

    @Test
    void shouldHandleNullDescription() {
        // Given
        String name = "Test Campaign";
        
        when(existsByNamePort.existsByNameIgnoreCase(name)).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        when(saveCampaignPort.save(any(Campaign.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)
        );
        
        // When
        Campaign result = createCampaignService.handle(
            name, null, startDate, endDate, 1L
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName().getValue()).isEqualTo(name);
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void shouldHandleNullDates() {
        // Given
        String name = "Test Campaign";
        
        when(existsByNamePort.existsByNameIgnoreCase(name)).thenReturn(false);
        when(clock.instant()).thenReturn(fixedInstant);
        when(saveCampaignPort.save(any(Campaign.class))).thenAnswer(invocation -> 
            invocation.getArgument(0)
        );
        
        // When
        Campaign result = createCampaignService.handle(
            name, "Description", null, null, 1L
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName().getValue()).isEqualTo(name);
        assertThat(result.getStartDate()).isNull();
        assertThat(result.getEndDate()).isNull();
    }
}