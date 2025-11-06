package com.example.encuestas_api.users.infrastructure.adapter.in.rest;

import com.example.encuestas_api.common.dto.PagedResult;
import com.example.encuestas_api.users.application.dto.ListUsersQuery;
import com.example.encuestas_api.users.application.port.in.*;
import com.example.encuestas_api.users.domain.model.UserStatus;
import com.example.encuestas_api.users.infrastructure.adapter.in.rest.dto.*;
import com.example.encuestas_api.users.infrastructure.adapter.in.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final CreateUserUseCase createUC;
    private final GetUserByIdUseCase getByIdUC;
    private final GetUserByEmailUseCase getByEmailUC;
    private final RenameUserUseCase renameUC;
    private final ChangeUserStatusUseCase changeStatusUC;
    private final SetSystemAdminUseCase setAdminUC;
    private final DeleteUserUseCase deleteUC;
    private final ListUsersUseCase listUC;

    public UserController(CreateUserUseCase createUC,
                          GetUserByIdUseCase getByIdUC,
                          GetUserByEmailUseCase getByEmailUC,
                          RenameUserUseCase renameUC,
                          ChangeUserStatusUseCase changeStatusUC,
                          SetSystemAdminUseCase setAdminUC,
                          DeleteUserUseCase deleteUC,
                          ListUsersUseCase listUC) {
        this.createUC = createUC;
        this.getByIdUC = getByIdUC;
        this.getByEmailUC = getByEmailUC;
        this.renameUC = renameUC;
        this.changeStatusUC = changeStatusUC;
        this.setAdminUC = setAdminUC;
        this.deleteUC = deleteUC;
        this.listUC = listUC;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        var user = createUC.handle(req.email(), req.fullName(), Boolean.TRUE.equals(req.systemAdmin()));
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        var user = getByIdUC.handle(id);
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> getByEmail(@RequestParam String email) {
        var user = getByEmailUC.handle(email);
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserResponse> rename(@PathVariable Long id,
                                               @Valid @RequestBody RenameUserRequest req) {
        var user = renameUC.handle(id, req.fullName());
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeStatus(@PathVariable Long id,
                                                     @Valid @RequestBody ChangeStatusRequest req) {
        var user = changeStatusUC.handle(id, req.status());
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @PatchMapping("/{id}/system-admin")
    public ResponseEntity<UserResponse> setSystemAdmin(@PathVariable Long id,
                                                       @Valid @RequestBody SetSystemAdminRequest req) {
        var user = setAdminUC.handle(id, req.systemAdmin());
        return ResponseEntity.ok(UserRestMapper.toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUC.handle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResult<UserResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Boolean systemAdmin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var result = listUC.handle(new ListUsersQuery(search, status, systemAdmin, page, size));
        var mapped = new PagedResult<>(
                result.items().stream().map(UserRestMapper::toResponse).toList(),
                result.total(),
                result.page(),
                result.size()
        );
        return ResponseEntity.ok(mapped);
    }
}
