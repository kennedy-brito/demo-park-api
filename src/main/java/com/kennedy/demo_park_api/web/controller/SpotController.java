package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.servicies.SpotService;
import com.kennedy.demo_park_api.web.dto.SpotCreateDto;
import com.kennedy.demo_park_api.web.dto.SpotResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.SpotMapper;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Spots", description = "Contains all operations related to the spot resource.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/spots")
public class SpotController {

    public final SpotService spotService;
    @Operation(summary = "Create a new spot",
            description = "Resource for registering a new spot. " + "Request restricted to authenticated user. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "resource created with success",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "Resource URL was created")),
                    @ApiResponse(
                            responseCode = "403", description = "user doesn't have permission to access resource.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "409", description = "spot code already in the system",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "422", description = "resource not processed because entry data was invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )

            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid SpotCreateDto dto){
        Spot spot = SpotMapper.toSpot(dto);
        spotService.save(spot);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{code}")
                .buildAndExpand(spot.getCode())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Search spot by code", description = "Requires a Bearer token. Access restricted to ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "resource located",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "user doesn't have permission to access resource.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "404", description = "spot not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{code}")
    public ResponseEntity<SpotResponseDto> findByCode(@PathVariable String code){
        Spot spot = spotService.findByCode(code);

        return ResponseEntity.ok(
                SpotMapper.toDto(spot)
        );
    }


}
