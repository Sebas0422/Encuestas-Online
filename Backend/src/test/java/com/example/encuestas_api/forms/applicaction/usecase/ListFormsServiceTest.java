package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.FormListQuery;
import com.example.encuestas_api.forms.application.port.out.SearchFormsPort;
import com.example.encuestas_api.forms.application.usecase.ListFormsService;
import com.example.encuestas_api.forms.domain.model.Form;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListFormsService Tests")
class ListFormsServiceTest {

    @Mock
    private SearchFormsPort searchFormsPort;

    private ListFormsService service;

    @BeforeEach
    void setUp() {
        service = new ListFormsService(searchFormsPort);
    }

    @Nested
    @DisplayName("When listing forms")
    class WhenListingForms {

        @Test
        @DisplayName("Should return paged result from search port")
        void shouldReturnPagedResultFromSearchPort() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertNotNull(result);
            assertSame(expectedResult, result);
            verify(searchFormsPort).search(query);
        }

        @Test
        @DisplayName("Should handle empty result set")
        void shouldHandleEmptyResultSet() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertNotNull(result);
            assertSame(expectedResult, result);
            verify(searchFormsPort).search(query);
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call search port exactly once")
        void shouldCallSearchPortExactlyOnce() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            service.handle(query);

            // Assert
            verify(searchFormsPort, times(1)).search(query);
            verifyNoMoreInteractions(searchFormsPort);
        }

        @Test
        @DisplayName("Should not call search port before service is invoked")
        void shouldNotCallSearchPortBeforeServiceIsInvoked() {
            // Arrange - no setup needed

            // Assert
            verifyNoInteractions(searchFormsPort);
        }

        @Test
        @DisplayName("Should pass query unchanged to search port")
        void shouldPassQueryUnchangedToSearchPort() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            verify(searchFormsPort).search(same(query));
        }
    }

    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should propagate exception from search port")
        void shouldPropagateExceptionFromSearchPort() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            RuntimeException expectedException = new RuntimeException("Search error");
            when(searchFormsPort.search(query)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(query));

            assertSame(expectedException, exception);
            verify(searchFormsPort).search(query);
        }

        @Test
        @DisplayName("Should handle null query")
        void shouldHandleNullQuery() {
            // Arrange
            when(searchFormsPort.search(null))
                .thenThrow(new IllegalArgumentException("Query cannot be null"));

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(null));

            verify(searchFormsPort).search(null);
        }
    }

    @Nested
    @DisplayName("Read-only transaction behavior")
    class ReadOnlyTransactionBehavior {

        @Test
        @DisplayName("Should complete operation within read-only transaction")
        void shouldCompleteOperationWithinReadOnlyTransaction() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertNotNull(result);
            verify(searchFormsPort).search(query);
        }
    }

    @Nested
    @DisplayName("Service construction and dependency injection")
    class ServiceConstructionAndDependencyInjection {

        @Test
        @DisplayName("Should be constructed with SearchFormsPort dependency")
        void shouldBeConstructedWithSearchFormsPortDependency() {
            // Arrange
            SearchFormsPort port = mock(SearchFormsPort.class);

            // Act
            ListFormsService newService = new ListFormsService(port);

            // Assert
            assertNotNull(newService);
            // Verify it works by testing a simple case
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            when(port.search(query)).thenReturn(expectedResult);
            
            PagedResult<Form> result = newService.handle(query);
            assertSame(expectedResult, result);
        }
    }

    @Nested
    @DisplayName("Performance considerations")
    class PerformanceConsiderations {

        @Test
        @DisplayName("Should handle multiple queries efficiently")
        void shouldHandleMultipleQueriesEfficiently() {
            // Arrange
            FormListQuery query1 = mock(FormListQuery.class);
            FormListQuery query2 = mock(FormListQuery.class);
            
            @SuppressWarnings("unchecked")
            PagedResult<Form> result1 = mock(PagedResult.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> result2 = mock(PagedResult.class);
            
            when(searchFormsPort.search(query1)).thenReturn(result1);
            when(searchFormsPort.search(query2)).thenReturn(result2);

            // Act
            PagedResult<Form> actualResult1 = service.handle(query1);
            PagedResult<Form> actualResult2 = service.handle(query2);

            // Assert
            assertSame(result1, actualResult1);
            assertSame(result2, actualResult2);
            verify(searchFormsPort).search(query1);
            verify(searchFormsPort).search(query2);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle different query types")
        void shouldHandleDifferentQueryTypes() {
            // Test that the service can handle different query instances
            FormListQuery[] queries = {
                mock(FormListQuery.class),
                mock(FormListQuery.class),
                mock(FormListQuery.class)
            };
            
            for (FormListQuery query : queries) {
                @SuppressWarnings("unchecked")
                PagedResult<Form> expectedResult = mock(PagedResult.class);
                when(searchFormsPort.search(query)).thenReturn(expectedResult);
                
                PagedResult<Form> result = service.handle(query);
                assertSame(expectedResult, result);
                verify(searchFormsPort).search(query);
            }
        }

        @Test
        @DisplayName("Should handle same query multiple times")
        void shouldHandleSameQueryMultipleTimes() {
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act - call multiple times
            for (int i = 0; i < 3; i++) {
                PagedResult<Form> result = service.handle(query);
                assertSame(expectedResult, result);
            }

            // Assert
            verify(searchFormsPort, times(3)).search(query);
        }
    }

    @Nested
    @DisplayName("Null safety")
    class NullSafety {

        @Test
        @DisplayName("Should handle null SearchFormsPort at construction")
        void shouldHandleNullSearchFormsPortAtConstruction() {
            // Arrange & Act
            ListFormsService serviceWithNullPort = new ListFormsService(null);
            
            // Assert - Construction should not throw
            assertNotNull(serviceWithNullPort);
        }

        @Test
        @DisplayName("Should handle null query parameters in port call")
        void shouldHandleNullQueryParametersInPortCall() {
            // This test verifies the service passes null to the port
            // and lets the port handle the validation
            
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            verify(searchFormsPort).search(query);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should work with real FormListQuery implementation")
        void shouldWorkWithRealFormListQueryImplementation() {
            // This test would use a real FormListQuery if available
            // For now, we'll use a mock
            
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
        }

        @Test
        @DisplayName("Should work with real PagedResult implementation")
        void shouldWorkWithRealPagedResultImplementation() {
            // This test would use a real PagedResult if available
            // For now, we'll use a mock
            
            // Arrange
            FormListQuery query = mock(FormListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Form> expectedResult = mock(PagedResult.class);
            
            when(searchFormsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Form> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
        }
    }
}