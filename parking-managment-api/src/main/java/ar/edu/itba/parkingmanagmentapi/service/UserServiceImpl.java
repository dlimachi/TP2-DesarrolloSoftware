package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.dto.CreateUserRequest;
import ar.edu.itba.parkingmanagmentapi.dto.UpdateUserRequest;
import ar.edu.itba.parkingmanagmentapi.dto.UserResponse;
import ar.edu.itba.parkingmanagmentapi.exceptions.BadRequestException;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.model.UserDetail;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import ar.edu.itba.parkingmanagmentapi.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Creates a new user
     */
    @Override
    public UserResponse createUser(CreateUserRequest userRequest, String encodedPassword) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("user.email.already_exists", userRequest.getEmail());
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setImageUrl(userRequest.getImageUrl());
        user.setPasswordHash(encodedPassword);
        user.setUserDetail(new UserDetail());

        return UserMapper.toUserResponse(userRepository.save(user));
    }

    /**
     * Updates an existing user
     */
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest user) {
        User userSaved = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user.not.found"));

        userSaved.setFirstName(user.getFirstName());
        userSaved.setLastName(user.getLastName());
        userSaved.setImageUrl(user.getImageUrl());
        userSaved.getUserDetail().setPhone(user.getUserDetail().getPhone());
        userSaved.getUserDetail().setAddress(user.getUserDetail().getAddress());
        userSaved.getUserDetail().setLang(user.getUserDetail().getLang());

        return UserMapper.toUserResponse(userRepository.save(userSaved));
    }

    /**
     * Finds a user by ID
     */
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user.not.found"));
    }

    /**
     * Deletes a user
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }

    // -------------------------- EXTENSIONS --------------------------

    /**
     * Finds a user by Email
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toUserResponse)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    // -------------------------- RAW ENTITIES --------------------------

    /**
     * Finds a raw user by Email
     */
    @Override
    public Optional<User> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}