package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.ClientSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientSpotRepository extends JpaRepository<ClientSpot, Long> {
    Optional<ClientSpot> findByReceiptAndExitDateIsNull(String receipt);

    long countByClientCpfAndEntryDateIsNotNull(String cpf);

}
