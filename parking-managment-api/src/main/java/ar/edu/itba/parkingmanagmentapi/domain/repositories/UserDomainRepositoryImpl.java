package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.exceptions.NotFoundException;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.User;
import ar.edu.itba.parkingmanagmentapi.repository.ManagerRepository;
import ar.edu.itba.parkingmanagmentapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserDomainRepositoryImpl implements UserDomainRepository {

  private final UserRepository userRepository;
  private final ManagerRepository managerRepository;

  @Override
  public Optional<UserDomain> findUserById(Long id) {
    return userRepository.findById(id).map(UserDomain::fromUserEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDomain> findUserByEmail(String email) {
    return userRepository.findByEmail(email).map(UserDomain::fromUserEntity);
  }

  @Override
  public boolean existsUserByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  @Override
  public List<UserDomain> findUserByFirstNameOrLastNameContainingIgnoreCase(String searchTerm) {
    return userRepository.findByFirstNameOrLastNameContainingIgnoreCase(searchTerm)
        .stream()
        .map(UserDomain::fromUserEntity)
        .toList();
  }

  @Override
  public Optional<UserDomain> findManagerByUserId(Long userId) {
    return managerRepository.findById(userId).map(UserDomain::fromManagerEntity);
  }

  @Override
  public boolean existsManagerByUserId(Long userId) {
    return managerRepository.existsById(userId);
  }

  @Override
  public Optional<UserDomain> findManagerByUser(UserDomain user) {
    return managerRepository.findByUser(user.toEntityNoPass()).map(UserDomain::fromManagerEntity);
  }

  @Override
  public UserDomain updateUser(UserDomain user) {
    User userSaved = userRepository.findById(user.getId())
        .orElseThrow(() -> new NotFoundException("user.not.found"));

    userSaved.setFirstName(user.getFirstName());
    userSaved.setLastName(user.getLastName());
    userSaved.setImageUrl(user.getImageUrl());
    userSaved.getUserDetail().setPhone(user.getPhone());
    userSaved.getUserDetail().setAddress(user.getAddress());
    userSaved.getUserDetail().setLang(user.getLang());

    return UserDomain.fromUserEntity(userSaved);
  }

  @Override
  public void deleteUserById(Long id) {
    User userSaved = userRepository.findById(id).orElseThrow(() -> new NotFoundException("user.not.found"));

    userRepository.delete(userSaved);
  }

  @Override
  public void saveUser(UserDomain user) {
    var savedUser = UserDomain.fromUserEntity(userRepository.save(user.toEntity()));
    if (user.getType() == UserType.MANAGER) {
      Manager manager = new Manager(savedUser.toEntity());
      managerRepository.save(manager);
    }
  }
}
