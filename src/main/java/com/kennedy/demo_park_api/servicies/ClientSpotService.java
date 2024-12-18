package com.kennedy.demo_park_api.servicies;


import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.exception.EntityNotFoundException;
import com.kennedy.demo_park_api.repositories.ClientSpotRepository;
import com.kennedy.demo_park_api.repositories.projection.ClientSpotProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@RequiredArgsConstructor
@Service
public class ClientSpotService {

    private final ClientSpotRepository clientSpotRepository;

    @Transactional
    public ClientSpot save(ClientSpot clientSpot){
        return clientSpotRepository.save(clientSpot);
    }


    @Transactional(readOnly = true)
    public ClientSpot findCheckInReceipt(String receipt) {
        return clientSpotRepository.findByReceiptAndExitDateIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Receipt '%s' not found in the system or check-out already done.", receipt)
                )
        );
    }

    public long getTotalParkingVisits(String cpf) {
        return clientSpotRepository.countByClientCpfAndEntryDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpotProjection> findAllByClientCpf(String cpf, Pageable pageable) {
        return clientSpotRepository.findAllByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpotProjection> findAllByUserId(Long id, Pageable pageable) {
        return clientSpotRepository.findAllByClientUserId(id, pageable);
    }
}
