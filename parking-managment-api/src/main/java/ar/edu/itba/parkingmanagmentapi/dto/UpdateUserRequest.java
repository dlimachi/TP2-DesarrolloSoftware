package ar.edu.itba.parkingmanagmentapi.dto;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

  private String firstName;
  private String lastName;
  private String imageUrl;
  private UserDetailDTO userDetail;

  public UserDomain toDomain() {
    return new UserDomain(
        firstName,
        lastName,
        imageUrl,
        userDetail.getPhone(),
        userDetail.getAddress(),
        userDetail.getLang());
  }
}
