package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Optional<Spot> findByCode(String code);

    Optional<Spot> findFirstByStatus(Spot.SpotStatus free);
}
