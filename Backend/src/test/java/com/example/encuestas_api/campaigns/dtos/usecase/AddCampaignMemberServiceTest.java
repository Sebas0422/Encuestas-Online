package com.example.encuestas_api.campaigns.dtos.usecase;

import com.example.encuestas_api.campaigns.application.port.in.AddCampaignMemberUseCase;
import com.example.encuestas_api.campaigns.application.port.out.CheckUserExistsPort;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignMemberPort;
import com.example.encuestas_api.campaigns.application.port.out.LoadCampaignPort;
import com.example.encuestas_api.campaigns.application.port.out.SaveCampaignMemberPort;
import com.example.encuestas_api.campaigns.application.usecase.AddCampaignMemberService;
import com.example.encuestas_api.campaigns.domain.exception.CampaignMemberAlreadyExistsException;
import com.example.encuestas_api.campaigns.domain.exception.CampaignNotFoundException;
import com.example.encuestas_api.campaigns.domain.model.Campaign;
import com.example.encuestas_api.campaigns.domain.model.CampaignMember;
import com.example.encuestas_api.campaigns.domain.model.CampaignMemberRole;
import com.example.encuestas_api.campaigns.domain.valueobject.CampaignName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddCampaignMemberService Tests")
class AddCampaignMemberServiceTest {

    @Mock
    private LoadCampaignPort loadCampaignPort;

    @Mock
    private LoadCampaignMemberPort loadMemberPort;

    @Mock
    private SaveCampaignMemberPort saveMemberPort;

    @Mock
    private CheckUserExistsPort checkUserExists;

    @Mock
    private Clock clock;

    @InjectMocks
    private AddCampaignMemberService addCampaignMemberService;

    private final Instant fixedInstant = Instant.parse("2024-01-01T10:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private final Long campaignId = 1L;
    private final Long userId = 100L;
    private final CampaignMemberRole role = CampaignMemberRole.ADMIN;

    private Campaign testCampaign;

    @BeforeEach
    void setUp() {
        testCampaign = Campaign.rehydrate(
            campaignId,
            CampaignName.of("Test Campaign"),
            "Test Description",
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(30),
            com.example.encuestas_api.campaigns.domain.model.CampaignStatus.draft,
            fixedInstant.minusSeconds(3600),
            fixedInstant.minusSeconds(3600)
        );
    }

    @Test
    @DisplayName("Should add new campaign member successfully")
    void shouldAddNewCampaignMemberSuccessfully() {
        // Given
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(fixedInstant);
        
        CampaignMember savedMember = CampaignMember.createNew(campaignId, userId, role, fixedInstant);
        when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

        // When
        CampaignMember result = addCampaignMemberService.handle(campaignId, userId, role);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCampaignId()).isEqualTo(campaignId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getCreatedAt()).isEqualTo(fixedInstant);

        verify(loadCampaignPort).loadById(campaignId);
        verify(checkUserExists).existsUserById(userId);
        verify(loadMemberPort).loadByCampaignIdAndUserId(campaignId, userId);
        verify(saveMemberPort).save(any(CampaignMember.class));
        verify(clock).instant();
    }

    @ParameterizedTest
    @EnumSource(CampaignMemberRole.class)
    @DisplayName("Should add campaign member with all role types")
    void shouldAddCampaignMemberWithAllRoleTypes(CampaignMemberRole testRole) {
        // Given
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(fixedInstant);
        
        CampaignMember savedMember = CampaignMember.createNew(campaignId, userId, testRole, fixedInstant);
        when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

        // When
        CampaignMember result = addCampaignMemberService.handle(campaignId, userId, testRole);

        // Then
        assertThat(result.getRole()).isEqualTo(testRole);
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundExceptionWhenCampaignDoesNotExist() {
        // Given
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
            .isInstanceOf(CampaignNotFoundException.class)
            .hasMessageContaining("no encontrada");

        verify(loadCampaignPort).loadById(campaignId);
        verifyNoInteractions(checkUserExists, loadMemberPort, saveMemberPort, clock);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when user does not exist")
    void shouldThrowIllegalArgumentExceptionWhenUserDoesNotExist() {
        // Given
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Usuario " + userId + " no existe");

        verify(loadCampaignPort).loadById(campaignId);
        verify(checkUserExists).existsUserById(userId);
        verifyNoInteractions(loadMemberPort, saveMemberPort, clock);
    }

    @Test
    @DisplayName("Should throw CampaignMemberAlreadyExistsException when member already exists")
    void shouldThrowCampaignMemberAlreadyExistsExceptionWhenMemberAlreadyExists() {
        // Given
        CampaignMember existingMember = CampaignMember.createNew(campaignId, userId, CampaignMemberRole.READER, fixedInstant);
        
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.of(existingMember));

        // When & Then
        assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
            .isInstanceOf(CampaignMemberAlreadyExistsException.class)
            .hasMessageContaining("ya es miembro");

        verify(loadCampaignPort).loadById(campaignId);
        verify(checkUserExists).existsUserById(userId);
        verify(loadMemberPort).loadByCampaignIdAndUserId(campaignId, userId);
        verifyNoInteractions(saveMemberPort, clock);
    }

    @Test
    @DisplayName("Should handle null checkUserExists port gracefully")
    void shouldHandleNullCheckUserExistsPortGracefully() {
        // Given - Recreate service without checkUserExists
        AddCampaignMemberService serviceWithoutUserCheck = new AddCampaignMemberService(
            loadCampaignPort,
            loadMemberPort,
            saveMemberPort,
            null, // null checkUserExists
            fixedClock
        );
        
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        
        CampaignMember savedMember = CampaignMember.createNew(campaignId, userId, role, fixedInstant);
        when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

        // When
        CampaignMember result = serviceWithoutUserCheck.handle(campaignId, userId, role);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCampaignId()).isEqualTo(campaignId);
        assertThat(result.getUserId()).isEqualTo(userId);

        // Should not throw when checkUserExists is null
        verify(loadCampaignPort).loadById(campaignId);
        verify(loadMemberPort).loadByCampaignIdAndUserId(campaignId, userId);
        verify(saveMemberPort).save(any(CampaignMember.class));
    }

    @Test
    @DisplayName("Should handle multiple users for same campaign")
    void shouldHandleMultipleUsersForSameCampaign() {
        // Given
        Long userId2 = 200L;
        CampaignMemberRole role2 = CampaignMemberRole.READER;
        
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(checkUserExists.existsUserById(userId2)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId2)).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(fixedInstant);
        
        CampaignMember savedMember1 = CampaignMember.createNew(campaignId, userId, role, fixedInstant);
        CampaignMember savedMember2 = CampaignMember.createNew(campaignId, userId2, role2, fixedInstant);
        when(saveMemberPort.save(any(CampaignMember.class)))
            .thenReturn(savedMember1)
            .thenReturn(savedMember2);

        // When
        CampaignMember result1 = addCampaignMemberService.handle(campaignId, userId, role);
        CampaignMember result2 = addCampaignMemberService.handle(campaignId, userId2, role2);

        // Then
        assertThat(result1.getUserId()).isEqualTo(userId);
        assertThat(result1.getRole()).isEqualTo(role);
        assertThat(result2.getUserId()).isEqualTo(userId2);
        assertThat(result2.getRole()).isEqualTo(role2);
    }

    @Test
    @DisplayName("Should not allow duplicate user in same campaign")
    void shouldNotAllowDuplicateUserInSameCampaign() {
        // Given
        CampaignMember existingMember = CampaignMember.createNew(campaignId, userId, CampaignMemberRole.CREATOR, fixedInstant);
        
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.of(existingMember));

        // When & Then - Try to add same user with different role
        assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, CampaignMemberRole.ADMIN))
            .isInstanceOf(CampaignMemberAlreadyExistsException.class)
            .hasMessageContaining("ya es miembro");

        verify(loadMemberPort).loadByCampaignIdAndUserId(campaignId, userId);
        verifyNoInteractions(saveMemberPort);
    }

    @Test
        @DisplayName("Should handle concurrent addition attempts (simulated)")
        void shouldHandleConcurrentAdditionAttempts() {
            when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(checkUserExists.existsUserById(userId)).thenReturn(true);
            
            when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId))
                .thenReturn(Optional.empty());
            
            when(clock.instant()).thenReturn(fixedInstant);
            
            when(saveMemberPort.save(any(CampaignMember.class)))
                .thenThrow(new RuntimeException("Constraint violation - duplicate entry"));
            
            assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation");
        }
    @Test
    @DisplayName("Should use current timestamp from clock")
    void shouldUseCurrentTimestampFromClock() {
        // Given
        Instant now = Instant.now();
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(now);
        
        CampaignMember savedMember = CampaignMember.createNew(campaignId, userId, role, now);
        when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

        // When
        CampaignMember result = addCampaignMemberService.handle(campaignId, userId, role);

        // Then
        assertThat(result.getCreatedAt()).isEqualTo(now);
        verify(clock).instant();
    }

    @Test
    @DisplayName("Should preserve member data when saving")
    void shouldPreserveMemberDataWhenSaving() {
        // Given
        CampaignMemberRole testRole = CampaignMemberRole.CREATOR;
        when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
        when(checkUserExists.existsUserById(userId)).thenReturn(true);
        when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
        when(clock.instant()).thenReturn(fixedInstant);
        
        // Use ArgumentCaptor instead of lambda with mutable variable
        ArgumentCaptor<CampaignMember> memberCaptor = ArgumentCaptor.forClass(CampaignMember.class);
        CampaignMember savedMember = CampaignMember.createNew(campaignId, userId, testRole, fixedInstant);
        when(saveMemberPort.save(memberCaptor.capture())).thenReturn(savedMember);

        // When
        CampaignMember result = addCampaignMemberService.handle(campaignId, userId, testRole);

        // Then
        CampaignMember capturedMember = memberCaptor.getValue();
        assertThat(capturedMember).isNotNull();
        assertThat(capturedMember.getCampaignId()).isEqualTo(campaignId);
        assertThat(capturedMember.getUserId()).isEqualTo(userId);
        assertThat(capturedMember.getRole()).isEqualTo(testRole);
        assertThat(capturedMember.getCreatedAt()).isEqualTo(fixedInstant);
        assertThat(result).isEqualTo(savedMember);
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should handle very large campaign and user IDs")
        void shouldHandleVeryLargeCampaignAndUserIDs() {
            // Given
            Long largeCampaignId = Long.MAX_VALUE;
            Long largeUserId = Long.MAX_VALUE - 1;
            
            Campaign largeCampaign = Campaign.rehydrate(
                largeCampaignId,
                CampaignName.of("Large Campaign"),
                "Description",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                com.example.encuestas_api.campaigns.domain.model.CampaignStatus.active,
                fixedInstant,
                fixedInstant
            );
            
            when(loadCampaignPort.loadById(largeCampaignId)).thenReturn(Optional.of(largeCampaign));
            when(checkUserExists.existsUserById(largeUserId)).thenReturn(true);
            when(loadMemberPort.loadByCampaignIdAndUserId(largeCampaignId, largeUserId)).thenReturn(Optional.empty());
            when(clock.instant()).thenReturn(fixedInstant);
            
            CampaignMember savedMember = CampaignMember.createNew(largeCampaignId, largeUserId, role, fixedInstant);
            when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

            // When
            CampaignMember result = addCampaignMemberService.handle(largeCampaignId, largeUserId, role);

            // Then
            assertThat(result.getCampaignId()).isEqualTo(largeCampaignId);
            assertThat(result.getUserId()).isEqualTo(largeUserId);
        }

        @Test
        @DisplayName("Should handle zero values for IDs")
        void shouldHandleZeroValuesForIDs() {
            Long zeroCampaignId = 0L;
            Long zeroUserId = 0L;
            
            Campaign zeroCampaign = Campaign.rehydrate(
                zeroCampaignId,
                CampaignName.of("Zero Campaign"),
                "Description",
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                com.example.encuestas_api.campaigns.domain.model.CampaignStatus.draft,
                fixedInstant,
                fixedInstant
            );
            
            when(loadCampaignPort.loadById(zeroCampaignId)).thenReturn(Optional.of(zeroCampaign));
            when(checkUserExists.existsUserById(zeroUserId)).thenReturn(true);
            when(loadMemberPort.loadByCampaignIdAndUserId(zeroCampaignId, zeroUserId)).thenReturn(Optional.empty());
            when(clock.instant()).thenReturn(fixedInstant);
            
            CampaignMember savedMember = CampaignMember.createNew(zeroCampaignId, zeroUserId, role, fixedInstant);
            when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

            // When
            CampaignMember result = addCampaignMemberService.handle(zeroCampaignId, zeroUserId, role);

            // Then
            assertThat(result.getCampaignId()).isEqualTo(zeroCampaignId);
            assertThat(result.getUserId()).isEqualTo(zeroUserId);
        }

        @Test
        @DisplayName("Should handle negative user ID when user check is disabled")
        void shouldHandleNegativeUserIdWhenUserCheckIsDisabled() {
            // Given
            Long negativeUserId = -1L;
            AddCampaignMemberService serviceWithoutUserCheck = new AddCampaignMemberService(
                loadCampaignPort,
                loadMemberPort,
                saveMemberPort,
                null, // null checkUserExists
                fixedClock
            );
            
            when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, negativeUserId)).thenReturn(Optional.empty());
            
            CampaignMember savedMember = CampaignMember.createNew(campaignId, negativeUserId, role, fixedInstant);
            when(saveMemberPort.save(any(CampaignMember.class))).thenReturn(savedMember);

            // When
            CampaignMember result = serviceWithoutUserCheck.handle(campaignId, negativeUserId, role);

            // Then
            assertThat(result.getUserId()).isEqualTo(negativeUserId);
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {
        
        @Test
        @DisplayName("Should handle concurrent addition attempts (simulated)")
        void shouldHandleConcurrentAdditionAttempts() {
            when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(checkUserExists.existsUserById(userId)).thenReturn(true);
            
            when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId))
                .thenReturn(Optional.empty());
            
            when(clock.instant()).thenReturn(fixedInstant);
            
            when(saveMemberPort.save(any(CampaignMember.class)))
                .thenThrow(new RuntimeException("Constraint violation - duplicate entry"));
            
            
            assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Constraint violation");
        }

        @Test
        @DisplayName("Should maintain transaction boundaries")
        void shouldMaintainTransactionBoundaries() {
            when(loadCampaignPort.loadById(campaignId)).thenReturn(Optional.of(testCampaign));
            when(checkUserExists.existsUserById(userId)).thenReturn(true);
            when(loadMemberPort.loadByCampaignIdAndUserId(campaignId, userId)).thenReturn(Optional.empty());
            when(clock.instant()).thenReturn(fixedInstant);
            
            RuntimeException saveException = new RuntimeException("Database connection failed");
            when(saveMemberPort.save(any(CampaignMember.class))).thenThrow(saveException);

            assertThatThrownBy(() -> addCampaignMemberService.handle(campaignId, userId, role))
                .isEqualTo(saveException);

            verify(loadCampaignPort).loadById(campaignId);
            verify(checkUserExists).existsUserById(userId);
            verify(loadMemberPort).loadByCampaignIdAndUserId(campaignId, userId);
            verify(clock).instant();
            verify(saveMemberPort).save(any(CampaignMember.class));
        }
    }

    @Test
    @DisplayName("Should verify method signature matches interface")
    void shouldVerifyMethodSignatureMatchesInterface() {
        assertThat(addCampaignMemberService)
            .isInstanceOf(AddCampaignMemberUseCase.class);
        
        assertThat(addCampaignMemberService.getClass().getDeclaredMethods())
            .anyMatch(method -> method.getName().equals("handle") 
                && method.getParameterCount() == 3
                && method.getParameterTypes()[0].equals(Long.class)
                && method.getParameterTypes()[1].equals(Long.class)
                && method.getParameterTypes()[2].equals(CampaignMemberRole.class));
    }
}