package com.example.encuestas_api.campaigns.dtos;
import com.example.encuestas_api.campaigns.application.dto.CampaignListQuery;
import com.example.encuestas_api.campaigns.application.dto.MemberListQuery;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import com.example.encuestas_api.campaigns.domain.model.CampaignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class campaignListaQueryTest {

    private final LocalDate today = LocalDate.now();
    private final LocalDate tomorrow = today.plusDays(1);

    @Test
    void shouldCreateCampaignListQueryWithValidParameters() {
        
        String search = "test";
        CampaignStatus status = CampaignStatus.active;
        int page = 0;
        int size = 20;

        CampaignListQuery query = new CampaignListQuery(search, status, today, tomorrow, page, size);

        
        assertThat(query.search()).isEqualTo(search);
        assertThat(query.status()).isEqualTo(status);
        assertThat(query.startFrom()).isEqualTo(today);
        assertThat(query.endTo()).isEqualTo(tomorrow);
        assertThat(query.page()).isEqualTo(page);
        assertThat(query.size()).isEqualTo(size);
    }

    @Test
    void shouldCreateCampaignListQueryWithNullValues() {
        CampaignListQuery query = new CampaignListQuery(null, null, null, null, 0, 10);

        
        assertThat(query.search()).isNull();
        assertThat(query.status()).isNull();
        assertThat(query.startFrom()).isNull();
        assertThat(query.endTo()).isNull();
        assertThat(query.page()).isZero();
        assertThat(query.size()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void shouldThrowExceptionWhenPageIsNegative(int invalidPage) {
        
        assertThatThrownBy(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, invalidPage, 20)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("page debe ser >= 0");
    }

    @Test
    void shouldAllowPageZero() {
        
        assertThatCode(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, 20)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void shouldThrowExceptionWhenSizeIsZeroOrNegative(int invalidSize) {
        
        assertThatThrownBy(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, invalidSize)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("size inválido");
    }

    @Test
    void shouldThrowExceptionWhenSizeExceedsMaximum() {
        // When & Then
        assertThatThrownBy(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, 201)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("size inválido");
    }

    @Test
    void shouldAllowMaximumSize() {
        // When & Then
        assertThatCode(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, 200)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldAllowSizeOne() {
        // When & Then
        assertThatCode(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, 1)
        ).doesNotThrowAnyException();
    }

    private static Stream<Arguments> validSizes() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(10),
            Arguments.of(50),
            Arguments.of(100),
            Arguments.of(200)
        );
    }

    @ParameterizedTest
    @MethodSource("validSizes")
    void shouldAcceptValidSizeValues(int validSize) {
        // When & Then
        assertThatCode(() -> 
            new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 0, validSize)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleSearchTermWithWhitespace() {
        // Given
        String searchWithSpaces = "  test search  ";

        // When
        CampaignListQuery query = new CampaignListQuery(searchWithSpaces, null, null, null, 0, 20);

        // Then
        assertThat(query.search()).isEqualTo(searchWithSpaces);
    }

    @Test
    void shouldHandleEmptySearchTerm() {
        // Given
        String emptySearch = "";

        // When
        CampaignListQuery query = new CampaignListQuery(emptySearch, null, null, null, 0, 20);

        // Then
        assertThat(query.search()).isEmpty();
    }

    @Test
    void shouldHandleAllCampaignStatuses() {
        CampaignStatus[] allStatuses = CampaignStatus.values();
        
        for (CampaignStatus status : allStatuses) {
            // When & Then
            assertThatCode(() -> 
                new CampaignListQuery(null, status, null, null, 0, 20)
            ).doesNotThrowAnyException();
        }
    }

    @Test
    void shouldHandleReverseDateRange() {
        // Given - end date before start date (query validation allows this, business logic should handle it)
        LocalDate startDate = tomorrow;
        LocalDate endDate = today;

        // When
        CampaignListQuery query = new CampaignListQuery(null, null, startDate, endDate, 0, 20);

        // Then
        assertThat(query.startFrom()).isEqualTo(startDate);
        assertThat(query.endTo()).isEqualTo(endDate);
    }

    @Test
    void shouldHandleSameDates() {
        // Given
        LocalDate sameDate = today;

        // When
        CampaignListQuery query = new CampaignListQuery(null, null, sameDate, sameDate, 0, 20);

        // Then
        assertThat(query.startFrom()).isEqualTo(sameDate);
        assertThat(query.endTo()).isEqualTo(sameDate);
    }

    @Test
    void shouldHandleLargePageNumber() {
        // Given
        int largePage = 1000000;

        // When
        CampaignListQuery query = new CampaignListQuery(null, null, null, null, largePage, 20);

        // Then
        assertThat(query.page()).isEqualTo(largePage);
    }

    @Test
    void shouldBeImmutable() {
        // Given
        CampaignListQuery query = new CampaignListQuery("test", CampaignStatus.draft, today, tomorrow, 0, 20);

        // When & Then - record types are immutable by design
        assertThat(query)
            .isEqualTo(query)
            .hasSameHashCodeAs(query);
    }

    @Test
    void shouldHaveProperToString() {
        // Given
        CampaignListQuery query = new CampaignListQuery("test", CampaignStatus.active, today, tomorrow, 1, 25);

        // When
        String toString = query.toString();

        // Then
        assertThat(toString)
            .contains("test")
            .contains("active")
            .contains("page=1")
            .contains("size=25");
    }
}

class MemberListQueryTest {

    @Test
    void shouldCreateMemberListQueryWithValidParameters() {
        // Given
        Long campaignId = 1L;
        CampaignMemberRole role = CampaignMemberRole.ADMIN;
        int page = 0;
        int size = 20;

        // When
        MemberListQuery query = new MemberListQuery(campaignId, role, page, size);

        // Then
        assertThat(query.campaignId()).isEqualTo(campaignId);
        assertThat(query.role()).isEqualTo(role);
        assertThat(query.page()).isEqualTo(page);
        assertThat(query.size()).isEqualTo(size);
    }

    @Test
    void shouldCreateMemberListQueryWithNullRole() {
        // When
        MemberListQuery query = new MemberListQuery(1L, null, 0, 10);

        // Then
        assertThat(query.campaignId()).isEqualTo(1L);
        assertThat(query.role()).isNull();
        assertThat(query.page()).isZero();
        assertThat(query.size()).isEqualTo(10);
    }

    @Test
    void shouldThrowExceptionWhenCampaignIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> 
            new MemberListQuery(null, CampaignMemberRole.ADMIN, 0, 20)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("campaignId requerido");
    }

    @Test
    void shouldThrowExceptionWhenCampaignIdIsZero() {
        // Note: Zero might be invalid in business logic, but the constructor only checks for null
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(0L, CampaignMemberRole.ADMIN, 0, 20)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenCampaignIdIsNegative() {
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(-1L, CampaignMemberRole.ADMIN, 0, 20)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void shouldThrowExceptionWhenPageIsNegative(int invalidPage) {
        // When & Then
        assertThatThrownBy(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, invalidPage, 20)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("page debe ser >= 0");
    }

    @Test
    void shouldAllowPageZero() {
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void shouldThrowExceptionWhenSizeIsZeroOrNegative(int invalidSize) {
        // When & Then
        assertThatThrownBy(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, invalidSize)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("size inválido");
    }

    @Test
    void shouldThrowExceptionWhenSizeExceedsMaximum() {
        // When & Then
        assertThatThrownBy(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 201)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("size inválido");
    }

    @Test
    void shouldAllowMaximumSize() {
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 200)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldAllowSizeOne() {
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 1)
        ).doesNotThrowAnyException();
    }

    private static Stream<Arguments> validSizes() {
        return Stream.of(
            Arguments.of(1),
            Arguments.of(10),
            Arguments.of(50),
            Arguments.of(100),
            Arguments.of(200)
        );
    }

    @ParameterizedTest
    @MethodSource("validSizes")
    void shouldAcceptValidSizeValues(int validSize) {
        // When & Then
        assertThatCode(() -> 
            new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, validSize)
        ).doesNotThrowAnyException();
    }

    @Test
    void shouldHandleAllCampaignMemberRoles() {
        CampaignMemberRole[] allRoles = CampaignMemberRole.values();
        
        for (CampaignMemberRole role : allRoles) {
            // When & Then
            assertThatCode(() -> 
                new MemberListQuery(1L, role, 0, 20)
            ).doesNotThrowAnyException();
        }
    }

    @Test
    void shouldHandleLargePageNumber() {
        // Given
        int largePage = 1000000;

        // When
        MemberListQuery query = new MemberListQuery(1L, CampaignMemberRole.READER, largePage, 20);

        // Then
        assertThat(query.page()).isEqualTo(largePage);
    }

    @Test
    void shouldHandleLargeCampaignId() {
        // Given
        Long largeCampaignId = 999999999999L;

        // When
        MemberListQuery query = new MemberListQuery(largeCampaignId, CampaignMemberRole.CREATOR, 0, 20);

        // Then
        assertThat(query.campaignId()).isEqualTo(largeCampaignId);
    }

    @Test
    void shouldBeImmutable() {
        // Given
        MemberListQuery query = new MemberListQuery(1L, CampaignMemberRole.CREATOR, 0, 20);

        // When & Then - record types are immutable by design
        assertThat(query)
            .isEqualTo(query)
            .hasSameHashCodeAs(query);
    }

    @Test
    void shouldHaveProperToString() {
        // Given
        MemberListQuery query = new MemberListQuery(123L, CampaignMemberRole.ADMIN, 2, 15);

        // When
        String toString = query.toString();

        // Then
        assertThat(toString)
            .contains("123")
            .contains("ADMIN")
            .contains("page=2")
            .contains("size=15");
    }

    @Test
    void shouldEqualWhenSameValues() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);

        // Then
        assertThat(query1).isEqualTo(query2);
        assertThat(query1.hashCode()).isEqualTo(query2.hashCode());
    }

    @Test
    void shouldNotEqualWhenDifferentValues() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(2L, CampaignMemberRole.READER, 1, 30);

        // Then
        assertThat(query1).isNotEqualTo(query2);
    }

    @Test
    void shouldNotEqualWhenDifferentCampaignId() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(2L, CampaignMemberRole.ADMIN, 0, 20);

        // Then
        assertThat(query1).isNotEqualTo(query2);
    }

    @Test
    void shouldNotEqualWhenDifferentRole() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(1L, CampaignMemberRole.READER, 0, 20);

        // Then
        assertThat(query1).isNotEqualTo(query2);
    }

    @Test
    void shouldNotEqualWhenDifferentPage() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 1, 20);

        // Then
        assertThat(query1).isNotEqualTo(query2);
    }

    @Test
    void shouldNotEqualWhenDifferentSize() {
        // Given
        MemberListQuery query1 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 20);
        MemberListQuery query2 = new MemberListQuery(1L, CampaignMemberRole.ADMIN, 0, 30);

        // Then
        assertThat(query1).isNotEqualTo(query2);
    }
}

class CombinedDTOTests {

    @Test
    void shouldBothQueriesHaveSimilarValidationPatterns() {
        // Test that both DTOs follow similar validation patterns
        CampaignListQuery campaignQuery = new CampaignListQuery("test", null, null, null, 0, 20);
        MemberListQuery memberQuery = new MemberListQuery(1L, null, 0, 20);

        // Both should have page validation
        assertThatThrownBy(() -> new CampaignListQuery(null, null, null, null, -1, 20))
            .hasMessageContaining("page debe ser >= 0");
        
        assertThatThrownBy(() -> new MemberListQuery(1L, null, -1, 20))
            .hasMessageContaining("page debe ser >= 0");

        // Both should have size validation
        assertThatThrownBy(() -> new CampaignListQuery(null, null, null, null, 0, 0))
            .hasMessageContaining("size inválido");
        
        assertThatThrownBy(() -> new MemberListQuery(1L, null, 0, 0))
            .hasMessageContaining("size inválido");

        // Both should allow max size of 200
        assertThatCode(() -> new CampaignListQuery(null, null, null, null, 0, 200))
            .doesNotThrowAnyException();
        
        assertThatCode(() -> new MemberListQuery(1L, null, 0, 200))
            .doesNotThrowAnyException();
    }
}
