package com.kennedy.demo_park_api.servicies;


import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.repositories.ClientSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientSpotService {

    private final ClientSpotRepository clientSpotRepository;

    @Transactional
    public ClientSpot save(ClientSpot clientSpot){
        return clientSpotRepository.save(clientSpot);
    }
}
