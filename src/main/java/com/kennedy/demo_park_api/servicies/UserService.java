package com.kennedy.demo_park_api.servicies;

import com.kennedy.demo_park_api.entities.User;
import com.kennedy.demo_park_api.repositories.UserRepository;
import com.kennedy.demo_park_api.exception.UsernameUniqueViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User save(User user) {
        try{
            return userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new UsernameUniqueViolationException(
                    String.format("Username {%s} already exists.", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }

    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {

        if(! newPassword.equals(confirmPassword)){
            throw new RuntimeException("new Password is not equal to confirmation Password");
        }

        User user = findById(id);

        if (! user.getPassword().equals(currentPassword)){
            throw new RuntimeException("Your password is wrong");
        }

        user.setPassword(newPassword);

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
