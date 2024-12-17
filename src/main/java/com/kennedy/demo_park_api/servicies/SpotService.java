package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.exception.CodeUniqueViolationException;
import com.kennedy.demo_park_api.exception.EntityNotFoundException;
import com.kennedy.demo_park_api.repositories.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpotService {

    private final SpotRepository spotRepository;

    @Transactional
    public Spot save(Spot spot){
        try{
            return spotRepository.save(spot);
        }catch (DataIntegrityViolationException e){
            throw new CodeUniqueViolationException(
                    String.format("Spot with code '%s' already registered.", spot.getCode()));
        }
    }

    @Transactional(readOnly = true)
    public Spot findByCode(String code){
        return spotRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Spot with code '%s' wasn't found.", code))
        );
    }
}
