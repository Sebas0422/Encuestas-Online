package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.in.RenameCampaignUseCase;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.RenameCampaignService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RenameCampaignService Tests")
public class RenameCampaignServiceTest {
    
    @Mock
    private LoadCampaignPort loadCampaignPort;
    
    @Mock
    private SaveCampaignPort saveCampaignPort;
    
    private Clock fixedClock;
    private RenameCampaignService renameCampaignService;
    
    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2024-01-01T10:00:00Z"), ZoneId.of("UTC"));
        renameCampaignService = new RenameCampaignService(
            loadCampaignPort,
            saveCampaignPort,
            fixedClock
        );
    }
    
    // ... (el resto de las clases anidadas se mantienen igual hasta ValidationScenarios)
    
    @Nested
    @DisplayName("Validation scenarios")
    class ValidationScenarios {
        
        @Test
        @DisplayName("Should reject whitespace-only names")
        void shouldRejectWhitespaceOnlyNames() {
            // Arrange
            Long campaignId = 1L;
            String whitespaceName = "   \t\n   ";
            
            Campaign existingCampaign = mock(Campaign.class);
            
            when(loadCampaignPort.loadById(campaignId))
                .thenReturn(Optional.of(existingCampaign));
            
            // Act & Assert
            assertThatThrownBy(() -> 
                renameCampaignService.handle(campaignId, whitespaceName)
            ).isInstanceOf(IllegalArgumentException.class)
             .hasMessageContaining("El nombre de la campaign es requerido");
            
            verify(existingCampaign, never()).rename(any(CampaignName.class), any(Instant.class));
            verify(saveCampaignPort, never()).save(any(Campaign.class));
        }
        
        @Test
        @DisplayName("Should accept valid campaign names")
        void shouldAcceptValidCampaignNames() {
            // Arrange
            Long campaignId = 1L;
            String[] validNames = {
                "Campaign 2024",
                "Marketing Q1",
                "Product Launch",
                "A", // Single character
                "This is a very long campaign name that might have many words and descriptions" // Long name
            };
            
            for (String validName : validNames) {
                // Reset mocks for each iteration
                reset(loadCampaignPort, saveCampaignPort);
                
                Campaign existingCampaign = mock(Campaign.class);
                Campaign renamedCampaign = mock(Campaign.class);
                
                when(loadCampaignPort.loadById(campaignId))
                    .thenReturn(Optional.of(existingCampaign));
                when(existingCampaign.rename(any(CampaignName.class), any(Instant.class)))
                    .thenReturn(renamedCampaign);
                when(saveCampaignPort.save(renamedCampaign))
                    .thenReturn(renamedCampaign);
                
                // Act & Assert
                assertThatCode(() -> {
                    Campaign result = renameCampaignService.handle(campaignId, validName);
                    assertThat(result).isSameAs(renamedCampaign);
                }).doesNotThrowAnyException();
                
                verify(loadCampaignPort).loadById(campaignId);
                verify(existingCampaign).rename(any(CampaignName.class), any(Instant.class));
                verify(saveCampaignPort).save(renamedCampaign);
            }
        }
    }
}