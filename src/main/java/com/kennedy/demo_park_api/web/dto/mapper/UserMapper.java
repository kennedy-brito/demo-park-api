package com.kennedy.demo_park_api.web.dto.mapper;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.web.dto.UserCreateDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;

public class UserMapper {

    public static User toUser(UserCreateDto createDto){
        return new ModelMapper().map(createDto, User.class);
    }
    public static UserResponseDto toUserResponse(User user){
        String role = user.getRole().name().substring("ROLE_".length());

        PropertyMap<User, UserResponseDto> props = new PropertyMap<>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };
        ModelMapper mapper = new  ModelMapper();
        mapper.addMappings(props);
        return mapper.map(user, UserResponseDto.class);
    }

    public static List<UserResponseDto> toListDto(List<User> users){

        return users
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }
}
