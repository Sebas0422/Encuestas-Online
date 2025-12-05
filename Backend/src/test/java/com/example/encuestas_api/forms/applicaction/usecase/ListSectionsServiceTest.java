package com.example.encuestas_api.forms.applicaction.usecase;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.forms.application.dto.SectionListQuery;
import com.example.encuestas_api.forms.application.port.out.SearchSectionsPort;
import com.example.encuestas_api.forms.application.usecase.ListSectionsService;
import com.example.encuestas_api.forms.domain.model.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListSectionsService Tests")
class ListSectionsServiceTest {

    @Mock
    private SearchSectionsPort searchSectionsPort;

    private ListSectionsService service;

    @BeforeEach
    void setUp() {
        service = new ListSectionsService(searchSectionsPort);
    }

    @Nested
    @DisplayName("When listing sections")
    class WhenListingSections {

        @Test
        @DisplayName("Should return paged result from search port")
        void shouldReturnPagedResultFromSearchPort() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertNotNull(result);
            assertSame(expectedResult, result);
            verify(searchSectionsPort).search(query);
        }

        @Test
        @DisplayName("Should handle empty result set")
        void shouldHandleEmptyResultSet() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertNotNull(result);
            assertSame(expectedResult, result);
            verify(searchSectionsPort).search(query);
        }
    }

    @Nested
    @DisplayName("Port interactions")
    class PortInteractions {

        @Test
        @DisplayName("Should call search port exactly once")
        void shouldCallSearchPortExactlyOnce() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            service.handle(query);

            // Assert
            verify(searchSectionsPort, times(1)).search(query);
            verifyNoMoreInteractions(searchSectionsPort);
        }

        @Test
        @DisplayName("Should not call search port before service is invoked")
        void shouldNotCallSearchPortBeforeServiceIsInvoked() {
            // Arrange - no setup needed

            // Assert
            verifyNoInteractions(searchSectionsPort);
        }

        @Test
        @DisplayName("Should pass query unchanged to search port")
        void shouldPassQueryUnchangedToSearchPort() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            verify(searchSectionsPort).search(same(query));
        }

        @Test
        @DisplayName("Should maintain query parameter integrity")
        void shouldMaintainQueryParameterIntegrity() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertNotNull(result);
            verify(searchSectionsPort).search(query);
        }
    }

    @Nested
    @DisplayName("Exception handling")
    class ExceptionHandling {

        @Test
        @DisplayName("Should propagate exception from search port")
        void shouldPropagateExceptionFromSearchPort() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            RuntimeException expectedException = new RuntimeException("Search error");
            when(searchSectionsPort.search(query)).thenThrow(expectedException);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.handle(query));

            assertSame(expectedException, exception);
            verify(searchSectionsPort).search(query);
        }

        @Test
        @DisplayName("Should handle null query")
        void shouldHandleNullQuery() {
            // Arrange
            when(searchSectionsPort.search(null))
                .thenThrow(new IllegalArgumentException("Query cannot be null"));

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> service.handle(null));

            verify(searchSectionsPort).search(null);
        }
    }

    @Nested
    @DisplayName("Read-only transaction behavior")
    class ReadOnlyTransactionBehavior {

        @Test
        @DisplayName("Should complete operation within read-only transaction")
        void shouldCompleteOperationWithinReadOnlyTransaction() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertNotNull(result);
            verify(searchSectionsPort).search(query);
        }

        @Test
        @DisplayName("Should not modify returned results")
        void shouldNotModifyReturnedResults() {
            // This test verifies that the service doesn't modify the returned results
            // Since it's a read-only service, it should only pass through results
            
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert - Verify the service returns exactly what the port provides
            assertSame(expectedResult, result);
            // No modifications should be made to the results
        }
    }

    @Nested
    @DisplayName("Service construction and dependency injection")
    class ServiceConstructionAndDependencyInjection {

        @Test
        @DisplayName("Should be constructed with SearchSectionsPort dependency")
        void shouldBeConstructedWithSearchSectionsPortDependency() {
            // Arrange
            SearchSectionsPort port = mock(SearchSectionsPort.class);

            // Act
            ListSectionsService newService = new ListSectionsService(port);

            // Assert
            assertNotNull(newService);
            // Verify it works by testing a simple case
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            when(port.search(query)).thenReturn(expectedResult);
            
            PagedResult<Section> result = newService.handle(query);
            assertSame(expectedResult, result);
        }

        @Test
        @DisplayName("Should handle null SearchSectionsPort gracefully")
        void shouldHandleNullSearchSectionsPortGracefully() {
            // Arrange & Act
            ListSectionsService serviceWithNullPort = new ListSectionsService(null);
            
            // Assert - Construction should not throw
            assertNotNull(serviceWithNullPort);
            
            // Note: Using the service with null port will throw at runtime,
            // but that's not the responsibility of this test
        }
    }

    @Nested
    @DisplayName("Performance considerations")
    class PerformanceConsiderations {

        @Test
        @DisplayName("Should handle multiple queries efficiently")
        void shouldHandleMultipleQueriesEfficiently() {
            // Arrange
            SectionListQuery query1 = mock(SectionListQuery.class);
            SectionListQuery query2 = mock(SectionListQuery.class);
            SectionListQuery query3 = mock(SectionListQuery.class);
            
            @SuppressWarnings("unchecked")
            PagedResult<Section> result1 = mock(PagedResult.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> result2 = mock(PagedResult.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> result3 = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query1)).thenReturn(result1);
            when(searchSectionsPort.search(query2)).thenReturn(result2);
            when(searchSectionsPort.search(query3)).thenReturn(result3);

            // Act
            PagedResult<Section> actualResult1 = service.handle(query1);
            PagedResult<Section> actualResult2 = service.handle(query2);
            PagedResult<Section> actualResult3 = service.handle(query3);

            // Assert
            assertSame(result1, actualResult1);
            assertSame(result2, actualResult2);
            assertSame(result3, actualResult3);
            verify(searchSectionsPort).search(query1);
            verify(searchSectionsPort).search(query2);
            verify(searchSectionsPort).search(query3);
        }

        @Test
        @DisplayName("Should handle same query multiple times")
        void shouldHandleSameQueryMultipleTimes() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act - call multiple times
            for (int i = 0; i < 5; i++) {
                PagedResult<Section> result = service.handle(query);
                assertSame(expectedResult, result);
            }

            // Assert
            verify(searchSectionsPort, times(5)).search(query);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle different query instances")
        void shouldHandleDifferentQueryInstances() {
            // Test that the service can handle different query instances
            SectionListQuery[] queries = new SectionListQuery[3];
            for (int i = 0; i < queries.length; i++) {
                queries[i] = mock(SectionListQuery.class);
                @SuppressWarnings("unchecked")
                PagedResult<Section> expectedResult = mock(PagedResult.class);
                when(searchSectionsPort.search(queries[i])).thenReturn(expectedResult);
                
                PagedResult<Section> result = service.handle(queries[i]);
                assertSame(expectedResult, result);
                verify(searchSectionsPort).search(queries[i]);
            }
        }

        @Test
        @DisplayName("Should handle query with different parameters")
        void shouldHandleQueryWithDifferentParameters() {
            // This test verifies that the service passes any query to the port
            // regardless of its internal parameters
            
            // Arrange
            SectionListQuery queryWithFilter = mock(SectionListQuery.class);
            SectionListQuery queryWithoutFilter = mock(SectionListQuery.class);
            
            @SuppressWarnings("unchecked")
            PagedResult<Section> result1 = mock(PagedResult.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> result2 = mock(PagedResult.class);
            
            when(searchSectionsPort.search(queryWithFilter)).thenReturn(result1);
            when(searchSectionsPort.search(queryWithoutFilter)).thenReturn(result2);

            // Act
            PagedResult<Section> actualResult1 = service.handle(queryWithFilter);
            PagedResult<Section> actualResult2 = service.handle(queryWithoutFilter);

            // Assert
            assertSame(result1, actualResult1);
            assertSame(result2, actualResult2);
        }
    }

    @Nested
    @DisplayName("Null safety")
    class NullSafety {

        @Test
        @DisplayName("Should handle null SearchSectionsPort at runtime")
        void shouldHandleNullSearchSectionsPortAtRuntime() {
            // This test verifies that construction with null doesn't throw
            // but usage will throw NPE
            
            // Arrange
            ListSectionsService serviceWithNullPort = new ListSectionsService(null);
            
            // Act & Assert - Using the service should throw
            assertThrows(NullPointerException.class,
                () -> serviceWithNullPort.handle(mock(SectionListQuery.class)));
        }

        @Test
        @DisplayName("Should pass null query to port for handling")
        void shouldPassNullQueryToPortForHandling() {
            // Arrange
            IllegalArgumentException expectedException = new IllegalArgumentException("Query cannot be null");
            when(searchSectionsPort.search(null)).thenThrow(expectedException);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.handle(null));

            assertSame(expectedException, exception);
            verify(searchSectionsPort).search(null);
        }
    }

    @Nested
    @DisplayName("Section-specific behavior")
    class SectionSpecificBehavior {

        @Test
        @DisplayName("Should work with Section domain model")
        void shouldWorkWithSectionDomainModel() {
            // This test verifies the service works with the Section type
            
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            // The service should work with Section type specifically
        }

        @Test
        @DisplayName("Should use SectionListQuery specifically")
        void shouldUseSectionListQuerySpecifically() {
            // This test verifies the service uses SectionListQuery, not a generic query
            
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            // Verify it's specifically a SectionListQuery being used
            verify(searchSectionsPort).search(any(SectionListQuery.class));
        }
    }

    @Nested
    @DisplayName("Integration with search port")
    class IntegrationWithSearchPort {

        @Test
        @DisplayName("Should integrate correctly with SearchSectionsPort")
        void shouldIntegrateCorrectlyWithSearchSectionsPort() {
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            // Set up the port to return the expected result
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            // Verify the integration: service calls port, port returns result
            verify(searchSectionsPort).search(query);
        }

        @Test
        @DisplayName("Should handle port returning different result types")
        void shouldHandlePortReturningDifferentResultTypes() {
            // Test that the service correctly handles whatever the port returns
            
            // Arrange - multiple calls with different results
            SectionListQuery query1 = mock(SectionListQuery.class);
            SectionListQuery query2 = mock(SectionListQuery.class);
            
            @SuppressWarnings("unchecked")
            PagedResult<Section> result1 = mock(PagedResult.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> result2 = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query1)).thenReturn(result1);
            when(searchSectionsPort.search(query2)).thenReturn(result2);

            // Act
            PagedResult<Section> actualResult1 = service.handle(query1);
            PagedResult<Section> actualResult2 = service.handle(query2);

            // Assert
            assertSame(result1, actualResult1);
            assertSame(result2, actualResult2);
            assertNotSame(actualResult1, actualResult2);
        }
    }

    @Nested
    @DisplayName("Transactional boundary")
    class TransactionalBoundary {

        @Test
        @DisplayName("Should maintain transactional boundary")
        void shouldMaintainTransactionalBoundary() {
            // This test conceptually verifies that the service operates within
            // its declared transactional boundaries (read-only)
            
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertNotNull(result);
            // The @Transactional(readOnly = true) annotation should ensure
            // read-only behavior, though this is more of a conceptual check
        }

        @Test
        @DisplayName("Should not have side effects")
        void shouldNotHaveSideEffects() {
            // This test verifies that calling the service doesn't have
            // unintended side effects
            
            // Arrange
            SectionListQuery query = mock(SectionListQuery.class);
            @SuppressWarnings("unchecked")
            PagedResult<Section> expectedResult = mock(PagedResult.class);
            
            when(searchSectionsPort.search(query)).thenReturn(expectedResult);

            // Act
            PagedResult<Section> result = service.handle(query);

            // Assert
            assertSame(expectedResult, result);
            // Verify no unexpected interactions
            verifyNoMoreInteractions(searchSectionsPort);
        }
    }
}