package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.User;

public record UserDomain(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserType type
) {

  // FIXME: we probably need imageUrl and details, or think of another way to get them to generate
  // the UserResponse in the controller.

  public User toEntity(String passwordHash) {
    return new User(firstName, lastName, email, passwordHash);
  }

  public User toEntityNoPass() {
    return new User(firstName, lastName, email, null);
  }

  public static UserDomain fromUserEntity(User user) {
    return new UserDomain(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            UserType.USER
    );
  }

  public static UserDomain fromManagerEntity(Manager manager) {
    var user = manager.getUser();
    return new UserDomain(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            UserType.MANAGER
    );
  }

  // WARNING: no clue if this is right...
  public Manager toEntityManager() {
    return new Manager(toEntityNoPass());
  }
}
