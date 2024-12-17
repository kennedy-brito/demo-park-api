package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.Client;
import com.kennedy.demo_park_api.exception.CpfUniqueViolationException;
import com.kennedy.demo_park_api.exception.EntityNotFoundException;
import com.kennedy.demo_park_api.repositories.ClientRepository;
import com.kennedy.demo_park_api.repositories.projection.ClientProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Client with id='%s' not found in the system.", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<ClientProjection> findAll(Pageable pageable) {
        return clientRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Client findByUserId(Long id) {
        return clientRepository.findByUserId(id);
    }

    public Client findByCpf(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Client with cpf='%s' not found in the system.", cpf))
        );
    }
}
