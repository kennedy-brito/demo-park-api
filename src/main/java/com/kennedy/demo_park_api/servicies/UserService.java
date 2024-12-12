package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.exception.EntityNotFoundException;
import com.kennedy.demo_park_api.exception.PasswordInvalidException;
import com.kennedy.demo_park_api.repositories.UserRepository;
import com.kennedy.demo_park_api.exception.UsernameUniqueViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameUniqueViolationException(
                    String.format("Username {%s} already exists.", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("User id=%s not found", id))
        );
    }

    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("new Password is not equal to confirmation Password");
        }

        User user = findById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("Your password is wrong");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Username not found '%s' not found", username))
        );
    }

    @Transactional(readOnly = true)
    public User.Role getRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);
    }
}