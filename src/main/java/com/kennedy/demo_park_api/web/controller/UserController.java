package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.servicies.UserService;
import com.kennedy.demo_park_api.web.dto.UserCreateDto;
import com.kennedy.demo_park_api.web.dto.UserPasswordDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import com.kennedy.demo_park_api.web.dto.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto createDto){
        User user = userService.save(
                UserMapper.toUser(createDto)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UserMapper.toUserResponse(user)
        );
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        User user = userService.findById(id);

        return ResponseEntity.ok(
                UserMapper.toUserResponse(user));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody UserPasswordDto dto){
        User user = userService.changePassword(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll(){
        List<User> users = userService.findAll();

        return ResponseEntity.ok(
                UserMapper.toListDto(users)
        );
    }
}
