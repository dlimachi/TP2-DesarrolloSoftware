package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
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

}
