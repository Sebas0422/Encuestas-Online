package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.application.port.out.SearchCampaignMembersPort;
import com.example.encuestas_api.campaigns.application.usecase.ListCampaignMembersService;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.common.dto.PagedResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListCampaignMembersServiceTest {
    
    @Mock
    private SearchCampaignMembersPort searchPort;
    
    @InjectMocks
    private ListCampaignMembersService listCampaignMembersService;
    
    @Test
    void shouldReturnPagedResultWhenQueryIsValid() {
        // Given
        MemberListQuery query = mock(MemberListQuery.class);
        CampaignMember member = mock(CampaignMember.class);
        PagedResult<CampaignMember> expectedResult = mock(PagedResult.class);
        
        when(searchPort.search(query)).thenReturn(expectedResult);
        
        // When
        PagedResult<CampaignMember> result = listCampaignMembersService.handle(query);
        
        // Then
        assertThat(result).isSameAs(expectedResult);
        verify(searchPort).search(query);
    }
    
    @Test
    void shouldHandleNullQuery() {
        // Given
        PagedResult<CampaignMember> emptyResult = mock(PagedResult.class);
        when(searchPort.search(null)).thenReturn(emptyResult);
        
        // When
        PagedResult<CampaignMember> result = listCampaignMembersService.handle(null);
        
        // Then
        assertThat(result).isSameAs(emptyResult);
        verify(searchPort).search(null);
    }
    
    @Test
    void shouldDelegateToPort() {
        // Given
        MemberListQuery query = mock(MemberListQuery.class);
        PagedResult<CampaignMember> expectedResult = mock(PagedResult.class);
        
        when(searchPort.search(query)).thenReturn(expectedResult);
        
        // When
        PagedResult<CampaignMember> result = listCampaignMembersService.handle(query);
        
        // Then
        assertThat(result).isSameAs(expectedResult);
        verify(searchPort).search(query);
        verifyNoMoreInteractions(searchPort);
    }
    
     @Test
    void shouldHandleEmptyResult() {
        // Given
        MemberListQuery query = mock(MemberListQuery.class);
        PagedResult<CampaignMember> emptyResult = mock(PagedResult.class);
        
        // No configuramos getters específicos ya que PagedResult no los tiene
        when(searchPort.search(query)).thenReturn(emptyResult);
        
        // When
        PagedResult<CampaignMember> result = listCampaignMembersService.handle(query);
        
        // Then
        assertThat(result).isSameAs(emptyResult);
        verify(searchPort).search(query);
    }
}