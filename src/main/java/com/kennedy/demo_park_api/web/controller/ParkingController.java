package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.servicies.ParkingService;
import com.kennedy.demo_park_api.web.dto.ParkingCreateDto;
import com.kennedy.demo_park_api.web.dto.ParkingResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.ClientSpotMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {

    private final ParkingService parkingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/check-in")
    public ResponseEntity<ParkingResponseDto> checkIn(@RequestBody @Valid ParkingCreateDto dto){

        ClientSpot clientSpot = ClientSpotMapper.toClientSpot(dto);


        parkingService.checkIn(clientSpot);


        ParkingResponseDto parkingResponseDto = ClientSpotMapper.toDto(clientSpot);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{receipt}")
                .buildAndExpand(clientSpot.getReceipt())
                .toUri();

        return ResponseEntity.ok().body(
                parkingResponseDto
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/check-in")
    public ResponseEntity<String > check(@RequestBody @Valid ParkingCreateDto dto){

        return ResponseEntity.ok().body("Entrou");
    }
}
