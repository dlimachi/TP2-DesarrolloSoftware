package ar.edu.itba.parkingmanagmentapi.service;

import ar.edu.itba.parkingmanagmentapi.domain.UserDomain;

public interface UserService {

    /**
     * Creates a new user
     */
    // UserResponse createUser(UserDomain user, String encodedPassword);

    /**
     * Updates an existing user
     */
    UserDomain updateUser(UserDomain user);

    /**
     * Finds a user by ID
     */
    UserDomain findById(Long id);

    /**
     * Deletes a user
     */
    void deleteUser(Long id);

    // -------------------------- EXTENSIONS --------------------------

    /**
     * Finds a user by Email
     */
    UserDomain findByEmail(String email);

    // -------------------------- RAW EXTENSIONS --------------------------
}
