package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.repositories.UserDomainRepository;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserDomainRepository userRepository;

  /**
   * Creates a new user
   */
  // @Override
  // public UserResponse createUser(UserDomain userRequest, String
  // encodedPassword) {
  // if (userRepository.existsByEmail(userRequest.getEmail())) {
  // throw new BadRequestException("user.email.already_exists",
  // userRequest.getEmail());
  // }
  //
  // User user = new User();
  // user.setEmail(userRequest.getEmail());
  // user.setFirstName(userRequest.getFirstName());
  // user.setLastName(userRequest.getLastName());
  // user.setImageUrl(userRequest.getImageUrl());
  // user.setPasswordHash(encodedPassword);
  // user.setUserDetail(new UserDetail());
  //
  // return UserMapper.toUserResponse(userRepository.save(user));
  // }

  /**
   * Updates an existing user
   */
  @Override
  public UserDomain updateUser(UserDomain user) {
    return userRepository.updateUser(user);
  }

  /**
   * Finds a user by ID
   */
  @Override
  public UserDomain findById(Long id) {
    return userRepository.findUserById(id)
        .orElseThrow(() -> new NotFoundException("user.not.found"));
  }

  /**
   * Deletes a user
   */
  @Override
  public void deleteUser(Long id) {
    userRepository.deleteUserById(id);
  }

  // -------------------------- EXTENSIONS --------------------------

  /**
   * Finds a user by Email
   */
  @Override
  public UserDomain findByEmail(String email) {
    return userRepository.findUserByEmail(email)
        .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
  }
}
