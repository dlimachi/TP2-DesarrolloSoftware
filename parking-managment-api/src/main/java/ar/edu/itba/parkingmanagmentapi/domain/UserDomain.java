package ar.edu.itba.parkingmanagmentapi.domain;

import ar.edu.itba.parkingmanagmentapi.domain.enums.UserType;
import ar.edu.itba.parkingmanagmentapi.model.Manager;
import ar.edu.itba.parkingmanagmentapi.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDomain {

  private Long id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private String imageUrl;
  private String phone;
  private String address;
  private String lang;
  private String passwordHash;
  private final UserType type;

  public UserDomain(
      String firstName,
      String lastName,
      String imageUrl,
      String phone,
      String address,
      String lang) {
    this(null, firstName, lastName, null, imageUrl, phone, address, lang, null, null);
  }

  private User toEntity(String passwordHash) {
    User user = new User(firstName, lastName, email, passwordHash);
    user.setId(id);
    return user;
  }
  public User toEntity() {
    if (passwordHash == null) {
      throw new IllegalArgumentException("passwordHash should be defined");
    }
    return toEntity(passwordHash);
  }

  public User toEntityNoPass() {
    return toEntity(null);
  }

  // WARNING: no clue if this is right...
  public Manager toEntityManager() {
    return new Manager(toEntityNoPass());
  }

  private static UserDomain fromEntity(User user, UserType type) {
    // WARNING: I'm assuming UserDetail is never null in a User entity.
    if (user.getUserDetail() == null) {
      throw new RuntimeException("UserDetail was null... Idk had no time to handle this");
    }
    return new UserDomain(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getImageUrl(),
        user.getUserDetail().getPhone(),
        user.getUserDetail().getAddress(),
        user.getUserDetail().getLang(),
        user.getPasswordHash(),
        type);
  }

  public static UserDomain fromUserEntity(User user) {
    return UserDomain.fromEntity(user, UserType.USER);
  }

  public static UserDomain fromManagerEntity(Manager manager) {
    var user = manager.getUser();
    return UserDomain.fromEntity(user, UserType.MANAGER);
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
}
