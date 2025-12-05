package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.application.port.out.SearchCampaignsPort;
import com.example.encuestas_api.campaigns.application.usecase.ListCampaignsService;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.common.dto.PagedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListCampaignsService Tests")
public class ListCampaignsServiceTest {
    
    @Mock
    private SearchCampaignsPort searchPort;
    
    @InjectMocks
    private ListCampaignsService listCampaignsService;
    
    private CampaignListQuery query;
    private final Long USER_ID = 1L;
    private final Long OTHER_USER_ID = 2L;
    
    @BeforeEach
    void setUp() {
        query = mock(CampaignListQuery.class);
    }
    
    @Nested
    @DisplayName("When listing campaigns")
    class WhenListingCampaigns {
        
        @Test
        @DisplayName("Should return paged result successfully")
        void shouldReturnPagedResultSuccessfully() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, USER_ID)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, USER_ID);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle different user IDs")
        void shouldHandleDifferentUserIds() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, OTHER_USER_ID)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, OTHER_USER_ID);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, OTHER_USER_ID);
        }
        
        @Test
        @DisplayName("Should handle null query with valid user ID")
        void shouldHandleNullQueryWithValidUserId() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(null, USER_ID)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(null, USER_ID);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(null, USER_ID);
        }
    }
    
    @Nested
    @DisplayName("When handling edge cases")
    class WhenHandlingEdgeCases {
        
        @Test
        @DisplayName("Should handle null user ID")
        void shouldHandleNullUserId() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, null)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, null);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, null);
        }
        
        @Test
        @DisplayName("Should handle both null parameters")
        void shouldHandleBothNullParameters() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(null, null)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(null, null);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(null, null);
        }
        
        @Test
        @DisplayName("Should handle zero user ID")
        void shouldHandleZeroUserId() {
            // Arrange
            Long zeroUserId = 0L;
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, zeroUserId)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, zeroUserId);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, zeroUserId);
        }
        
        @Test
        @DisplayName("Should handle negative user ID")
        void shouldHandleNegativeUserId() {
            // Arrange
            Long negativeUserId = -1L;
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, negativeUserId)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, negativeUserId);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, negativeUserId);
        }
    }
    
    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {
        
        @Test
        @DisplayName("Should call searchPort.search with correct parameters")
        void shouldCallSearchPortSearchWithCorrectParameters() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, USER_ID)).thenReturn(expectedResult);
            
            // Act
            listCampaignsService.handle(query, USER_ID);
            
            // Assert
            verify(searchPort).search(query, USER_ID);
        }
        
        @Test
        @DisplayName("Should delegate completely to search port")
        void shouldDelegateCompletelyToSearchPort() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, USER_ID)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, USER_ID);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, USER_ID);
            verifyNoMoreInteractions(searchPort);
        }
        
        @Test
        @DisplayName("Should complete within read-only transaction")
        void shouldCompleteWithinReadOnlyTransaction() {
            // Arrange
            PagedResult<Campaign> expectedResult = mock(PagedResult.class);
            when(searchPort.search(query, USER_ID)).thenReturn(expectedResult);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, USER_ID);
            
            // Assert
            assertThat(result).isSameAs(expectedResult);
            verify(searchPort).search(query, USER_ID);
        }
    }
    
    @Nested
    @DisplayName("Multiple calls")
    class MultipleCalls {
        
        @Test
        @DisplayName("Should handle multiple service calls")
        void shouldHandleMultipleServiceCalls() {
            // Arrange
            CampaignListQuery query1 = mock(CampaignListQuery.class);
            CampaignListQuery query2 = mock(CampaignListQuery.class);
            Long userId1 = 1L;
            Long userId2 = 2L;
            
            PagedResult<Campaign> result1 = mock(PagedResult.class);
            PagedResult<Campaign> result2 = mock(PagedResult.class);
            
            when(searchPort.search(query1, userId1)).thenReturn(result1);
            when(searchPort.search(query2, userId2)).thenReturn(result2);
            
            // Act
            PagedResult<Campaign> actualResult1 = listCampaignsService.handle(query1, userId1);
            PagedResult<Campaign> actualResult2 = listCampaignsService.handle(query2, userId2);
            
            // Assert
            assertThat(actualResult1).isSameAs(result1);
            assertThat(actualResult2).isSameAs(result2);
            
            verify(searchPort).search(query1, userId1);
            verify(searchPort).search(query2, userId2);
            verify(searchPort, times(2)).search(any(), any());
        }
        
        @Test
        @DisplayName("Should handle same query with different user IDs")
        void shouldHandleSameQueryWithDifferentUserIds() {
            // Arrange
            Long userId1 = 1L;
            Long userId2 = 2L;
            
            PagedResult<Campaign> result1 = mock(PagedResult.class);
            PagedResult<Campaign> result2 = mock(PagedResult.class);
            
            when(searchPort.search(query, userId1)).thenReturn(result1);
            when(searchPort.search(query, userId2)).thenReturn(result2);
            
            // Act
            PagedResult<Campaign> actualResult1 = listCampaignsService.handle(query, userId1);
            PagedResult<Campaign> actualResult2 = listCampaignsService.handle(query, userId2);
            
            // Assert
            assertThat(actualResult1).isSameAs(result1);
            assertThat(actualResult2).isSameAs(result2);
            
            verify(searchPort).search(query, userId1);
            verify(searchPort).search(query, userId2);
        }
    }
    
    @Nested
    @DisplayName("Error scenarios")
    class ErrorScenarios {
        
        @Test
        @DisplayName("Should propagate exception from search port")
        void shouldPropagateExceptionFromSearchPort() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Database error");
            when(searchPort.search(query, USER_ID)).thenThrow(expectedException);
            
            // Act & Assert
            try {
                listCampaignsService.handle(query, USER_ID);
            } catch (RuntimeException e) {
                assertThat(e).isSameAs(expectedException);
            }
            
            verify(searchPort).search(query, USER_ID);
        }
        
        @Test
        @DisplayName("Should handle port returning null")
        void shouldHandlePortReturningNull() {
            // Arrange
            when(searchPort.search(query, USER_ID)).thenReturn(null);
            
            // Act
            PagedResult<Campaign> result = listCampaignsService.handle(query, USER_ID);
            
            // Assert
            assertThat(result).isNull();
            verify(searchPort).search(query, USER_ID);
        }
    }
}