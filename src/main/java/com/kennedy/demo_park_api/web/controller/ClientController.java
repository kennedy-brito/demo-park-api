package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.Client;
import com.kennedy.demo_park_api.jwt.JwtUserDetails;
import com.kennedy.demo_park_api.repositories.projection.ClientProjection;
import com.kennedy.demo_park_api.servicies.ClientService;
import com.kennedy.demo_park_api.servicies.UserService;
import com.kennedy.demo_park_api.web.dto.ClientCreateDto;
import com.kennedy.demo_park_api.web.dto.ClientResponseDto;
import com.kennedy.demo_park_api.web.dto.PageableDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.ClientMapper;
import com.kennedy.demo_park_api.web.dto.mapper.PageableMapper;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clients", description = "Contains all operations related to resources for creating, deleting, and editing users.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

    @Operation(summary = "Create a new client",
            description = "Resource for creating a new client. " + "Request restricted to authenticated user. Access restricted to Role='CLIENT'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "resource created with success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(
                            responseCode = "403", description = "resource not accessible to ADMIN profile",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "409", description = "client cpf already in the system",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "422", description = "resource not processed because entry data was invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )

            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDto> create(
            @RequestBody @Valid ClientCreateDto dto,
            @AuthenticationPrincipal JwtUserDetails userDetails){
        Client client = ClientMapper.toClient(dto);
        client.setUser(userService.findById(userDetails.getId()));
        clientService.save(client);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ClientMapper.toDto(client)
        );
    }

    @Operation(summary = "Search user by id", description = "Requires a Bearer token. Access restricted to ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "resource located",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "client not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "resource not permitted to CLIENT profile",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getById(@PathVariable Long id){
        Client client = clientService.findById(id);

        return ResponseEntity.ok(
                ClientMapper.toDto(client)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"name"}) Pageable pageable){
        Page<ClientProjection> clients = clientService.findAll(pageable);

        return ResponseEntity.ok(
                PageableMapper.toDto(clients)
        );
    }
}
