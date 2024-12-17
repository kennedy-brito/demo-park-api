package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
