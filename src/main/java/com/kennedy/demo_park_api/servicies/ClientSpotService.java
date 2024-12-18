package com.kennedy.demo_park_api.servicies;


import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.exception.EntityNotFoundException;
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
}
