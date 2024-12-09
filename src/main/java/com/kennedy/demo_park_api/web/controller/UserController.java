package com.kennedy.demo_park_api.web.controller;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.servicies.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user){
        user = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id){
        User user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

}
