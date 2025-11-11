package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String imageUrl;
  private UserDetailDTO userDetail;

  public static UserResponse fromDomain(UserDomain user) {
    // FIXME: we're missing properties in UserDomain to be able to create the
    // response...
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getImageUrl(),
        null);
  }
}
