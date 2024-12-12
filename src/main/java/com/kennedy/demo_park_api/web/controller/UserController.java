package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.servicies.UserService;
import com.kennedy.demo_park_api.web.dto.UserCreateDto;
import com.kennedy.demo_park_api.web.dto.UserPasswordDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.UserMapper;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Contains all operations related to resources for creating, deleting, and editing users.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Resource for creating a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "resource created with success",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(
                            responseCode = "409", description = "user e-mail already in the system",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(
                            responseCode = "422", description = "resource not processed because entry data was invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )

            })
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto createDto) {
        User user = userService.save(
                UserMapper.toUser(createDto)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserMapper.toUserResponse(user)
        );
    }

    @Operation(summary = "Search user by id", description = "Search an existing user using his id",
    responses = {
            @ApiResponse(
                    responseCode = "200", description = "resource located",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "resource not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = userService.findById(id);

        return ResponseEntity.ok(
                UserMapper.toUserResponse(user));
    }

    @Operation(summary = "Update password", description = "Update password",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Password successfully updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Password doesn't match",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "422", description = "resource not processed because entry data was invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDto dto) {
        User user = userService.changePassword(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List all users", description = "List all users",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Found users",
                            content = @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))
                    )
            })
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        List<User> users = userService.findAll();

        return ResponseEntity.ok(
                UserMapper.toListDto(users)
        );
    }
}
