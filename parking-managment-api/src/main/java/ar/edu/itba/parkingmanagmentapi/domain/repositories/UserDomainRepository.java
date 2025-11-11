package ar.edu.itba.parkingmanagmentapi.domain.repositories;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;

public interface UserDomainRepository {

  Optional<UserDomain> findUserById(Long id);

  /**
   * Busca un usuario por su email
   */
  Optional<UserDomain> findUserByEmail(String email);

  /**
   * Verifica si existe un usuario con el email especificado
   */
  boolean existsUserByEmail(String email);

  /**
   * Busca usuarios por nombre o apellido (búsqueda parcial)
   */
  List<UserDomain> findUserByFirstNameOrLastNameContainingIgnoreCase(String searchTerm);

  /**
   * Busca un manager por su user_id
   */
  Optional<UserDomain> findManagerByUserId(Long userId);

  /**
   * Verifica si existe un manager con el user_id especificado
   */
  boolean existsManagerByUserId(Long userId);

  Optional<UserDomain> findManagerByUser(UserDomain user);
}
