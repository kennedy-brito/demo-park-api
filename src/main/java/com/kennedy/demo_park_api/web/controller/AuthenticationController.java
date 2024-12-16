package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.jwt.JwtToken;
import com.kennedy.demo_park_api.jwt.JwtUserDetailsService;
import com.kennedy.demo_park_api.web.dto.UserLoginDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication", description = "Resource to authenticate in the API"
)
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final JwtUserDetailsService detailsService;

    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Authenticate in the API", description = "Resource for authenticating a user",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Authenticated with success and return a Bearer token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                    @ApiResponse(
                            responseCode = "400", description = "invalid credentials",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),

                    @ApiResponse(
                            responseCode = "422", description = "invalid fields",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )

            })
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid UserLoginDto loginDto, HttpServletRequest request){
        log.info("Authenticating process by login {}", loginDto.getUsername());

        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            authenticationManager.authenticate(authenticationToken);

            JwtToken token = detailsService.getTokenAuthenticated(loginDto.getUsername());
            return ResponseEntity.ok(token);
        }catch (AuthenticationException e){
            log.warn("Bad credentials from username: {}", loginDto.getUsername());
        }

        return ResponseEntity
                .badRequest()
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Invalid credentials"));
    }
}
