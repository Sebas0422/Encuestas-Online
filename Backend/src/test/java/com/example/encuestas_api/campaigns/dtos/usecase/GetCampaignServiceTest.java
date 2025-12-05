package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.usecase.GetCampaignService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCampaignServiceTest {
    
    @Mock
    private LoadCampaignPort loadPort;
    
    @InjectMocks
    private GetCampaignService getCampaignService;
    
    @Test
    void shouldReturnCampaignWhenExists() {
        // Given
        Long campaignId = 1L;
        Campaign expectedCampaign = mock(Campaign.class);
        when(loadPort.loadById(campaignId)).thenReturn(Optional.of(expectedCampaign));
        
        // When
        Campaign result = getCampaignService.handle(campaignId);
        
        // Then
        assertThat(result).isSameAs(expectedCampaign);
        verify(loadPort).loadById(campaignId);
    }
    
    @Test
    void shouldThrowCampaignNotFoundExceptionWhenNotExists() {
        // Given
        Long nonExistentId = 999L;
        when(loadPort.loadById(nonExistentId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> 
            getCampaignService.handle(nonExistentId)
        ).isInstanceOf(CampaignNotFoundException.class)
         .hasMessageContaining(nonExistentId.toString());
        
        verify(loadPort).loadById(nonExistentId);
    }
    
    @Test
    void shouldHandleNullId() {
        // Given
        when(loadPort.loadById(null)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> 
            getCampaignService.handle(null)
        ).isInstanceOf(CampaignNotFoundException.class)
         .hasMessageContaining("null");
        
        verify(loadPort).loadById(null);
    }
}