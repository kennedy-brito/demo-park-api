package com.kennedy.demo_park_api.servicies;


import com.kennedy.demo_park_api.entities.Client;
import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.util.ParkingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ClientSpotService clientSpotService;
    private final ClientService clientService;
    private final SpotService spotService;

    @Transactional
    public ClientSpot checkIn(ClientSpot clientSpot){
        Client client = clientService.findByCpf(clientSpot.getClient().getCpf());
        clientSpot.setClient(client);

        Spot spot = spotService.searchForFreeSpot();
        spot.setStatus(Spot.SpotStatus.OCCUPIED);

        clientSpot.setSpot(spot);

        clientSpot.setEntryDate(LocalDateTime.now());
        clientSpot.setReceipt(
                ParkingUtils.generateReceipt()
        );

        return clientSpotService.save(clientSpot);
    }

    @Transactional
    public ClientSpot checkOut(String receipt) {
        ClientSpot clientSpot = clientSpotService.findCheckInReceipt(receipt);

        LocalDateTime entryTime = clientSpot.getEntryDate();
        LocalDateTime exitTime = LocalDateTime.now();
        clientSpot.setExitDate(exitTime);

        BigDecimal price = ParkingUtils.calculatePrice(entryTime, exitTime);
        clientSpot.setPrice(price);

        long numberOfTimes = clientSpotService.getTotalParkingVisits(clientSpot.getClient().getCpf());
        BigDecimal discount = ParkingUtils.calculateDiscount(price, numberOfTimes);
        clientSpot.setDiscount(discount);

        clientSpot.getSpot().setStatus(Spot.SpotStatus.FREE);

        return clientSpotService.save(clientSpot);
    }
}
