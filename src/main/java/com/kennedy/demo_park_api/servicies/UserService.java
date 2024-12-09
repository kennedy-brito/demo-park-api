package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional
    public User changePassword(Long id, String password) {

        User user = findById(id);

        user.setPassword(password);

        return user;
    }

}
