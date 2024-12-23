package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.jwt.JwtUserDetails;
import com.kennedy.demo_park_api.repositories.projection.ClientSpotProjection;
import com.kennedy.demo_park_api.servicies.ClientService;
import com.kennedy.demo_park_api.servicies.ClientSpotService;
import com.kennedy.demo_park_api.servicies.JasperService;
import com.kennedy.demo_park_api.servicies.ParkingService;
import com.kennedy.demo_park_api.web.dto.PageableDto;
import com.kennedy.demo_park_api.web.dto.ParkingCreateDto;
import com.kennedy.demo_park_api.web.dto.ParkingResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.ClientSpotMapper;
import com.kennedy.demo_park_api.web.dto.mapper.PageableMapper;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking")
public class ParkingController {

    private final ParkingService parkingService;
    private final ClientSpotService clientSpotService;
    private final ClientService clientService;
    private final JasperService jasperService;

    @Operation(summary = "Check-in in a parking spot",
            description = "Resource for check-in in a parking spot. " + "Request restricted to authenticated user. Access restricted to Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "check-in successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParkingResponseDto.class)),
                            headers = @Header(name = HttpHeaders.LOCATION, description = "Resource URL was created")),
                    @ApiResponse(
                            responseCode = "403", description = "CLIENT role doesn't have permission to access resource.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "404", description = "Possible causes: <br/>" +
                            "- Client CPF isn't registered; <br/>" +
                            "- A free spot wasn't found;",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "422", description = "resource not processed because entry data was invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )

            })
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

        return ResponseEntity.created(location).body(
                parkingResponseDto
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/check-in")
    public ResponseEntity<String > check(@RequestBody @Valid ParkingCreateDto dto){

        return ResponseEntity.ok().body("Entrou");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping("/check-in/{receipt}")
    public ResponseEntity<ParkingResponseDto> findByReceipt(@PathVariable String receipt){
        ClientSpot clientSpot = clientSpotService.findCheckInReceipt(receipt);

        return ResponseEntity.ok(
                ClientSpotMapper.toDto(clientSpot)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/check-out/{receipt}")
    public ResponseEntity<ParkingResponseDto> checkOut(@PathVariable String receipt){
        ClientSpot clientSpot = parkingService.checkOut(receipt);

        return ResponseEntity.ok(
                ClientSpotMapper.toDto(clientSpot)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PageableDto> findAllParkingByCpf(
            @PathVariable String cpf,
            @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable){

        Page<ClientSpotProjection> projection = clientSpotService.findAllByClientCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping()
    public ResponseEntity<PageableDto> findAllParkingOfClient(
            @AuthenticationPrincipal JwtUserDetails user,
            @PageableDefault(size = 5, sort = "entryDate", direction = Sort.Direction.ASC) Pageable pageable){

        Page<ClientSpotProjection> projection = clientSpotService.findAllByUserId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/report")
    public ResponseEntity<Void> getReport(
            HttpServletResponse response,
            @AuthenticationPrincipal JwtUserDetails user) throws IOException {

        String cpf = clientService.findByUserId(user.getId()).getCpf();
        jasperService.addParams("CPF", cpf);

        byte[] bytes = jasperService.generatePdf();

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();
    }
}
