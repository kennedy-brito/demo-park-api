package com.kennedy.demo_park_api.web.dto.mapper;


import com.kennedy.demo_park_api.entities.ClientSpot;
import com.kennedy.demo_park_api.web.dto.ParkingCreateDto;
import com.kennedy.demo_park_api.web.dto.ParkingResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientSpotMapper {

    public static ClientSpot toClientSpot(ParkingCreateDto dto){
        return new ModelMapper().map(dto, ClientSpot.class);
    }

    public static ParkingResponseDto toDto(ClientSpot client){
        return new ModelMapper().map(client, ParkingResponseDto.class);
    }
}
