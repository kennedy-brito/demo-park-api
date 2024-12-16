package com.kennedy.demo_park_api.repositories;

import com.kennedy.demo_park_api.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
