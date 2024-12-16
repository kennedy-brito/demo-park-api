package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.Client;
import com.kennedy.demo_park_api.exception.CpfUniqueViolationException;
import com.kennedy.demo_park_api.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client){
        try{

            return clientRepository.save(client);
        }catch (DataIntegrityViolationException e){
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' cannot be registered, it already exists in the system", client.getCpf())
            );
        }
    }
}
