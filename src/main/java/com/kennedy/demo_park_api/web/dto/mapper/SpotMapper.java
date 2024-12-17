package com.kennedy.demo_park_api.web.dto.mapper;

import com.kennedy.demo_park_api.entities.Spot;
import com.kennedy.demo_park_api.web.dto.SpotCreateDto;
import com.kennedy.demo_park_api.web.dto.SpotResponseDto;
import lombok.*;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotMapper {


    public static Spot toSpot(SpotCreateDto dto){
        return new ModelMapper().map(dto, Spot.class);
    }

    public static SpotResponseDto toDto(Spot spot){
        return new ModelMapper().map(spot, SpotResponseDto.class);
    }
}
