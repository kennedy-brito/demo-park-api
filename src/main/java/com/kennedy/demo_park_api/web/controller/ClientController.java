package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.Client;
import com.kennedy.demo_park_api.jwt.JwtUserDetails;
import com.kennedy.demo_park_api.servicies.ClientService;
import com.kennedy.demo_park_api.servicies.UserService;
import com.kennedy.demo_park_api.web.dto.ClientCreateDto;
import com.kennedy.demo_park_api.web.dto.ClientResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.ClientMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;

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
}
