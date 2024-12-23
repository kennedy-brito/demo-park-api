package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.repositories.projection.ClientSpotProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ClientSpotRepository extends JpaRepository<ClientSpot, Long> {
    Optional<ClientSpot> findByReceiptAndExitDateIsNull(String receipt);

    long countByClientCpfAndEntryDateIsNotNull(String cpf);

    Page<ClientSpotProjection> findAllByClientCpf(String cpf, Pageable pageable);

    Page<ClientSpotProjection> findAllByClientUserId(Long id, Pageable pageable);
}
