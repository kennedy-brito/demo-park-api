package com.kennedy.demo_park_api.servicies;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientSpotService clientSpotService;
    private final ClientService clientService;
    private final SpotService spotService;

}
