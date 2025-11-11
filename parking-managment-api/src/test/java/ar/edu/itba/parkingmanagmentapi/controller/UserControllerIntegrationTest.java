package ar.edu.itba.parkingmanagmentapi.controller;

import ar.edu.itba.parkingmanagmentapi.BaseIntegrationTest;
import ar.edu.itba.parkingmanagmentapi.builder.TestDataBuilder;
import ar.edu.itba.parkingmanagmentapi.dto.*;
import ar.edu.itba.parkingmanagmentapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void testGetUser_shouldReturn200_andCorrectUser() {
        User user = TestDataBuilder.createUserComplete();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders(adminUser));
        ResponseEntity<ApiResponse<UserResponse>> getResponse = restTemplate.exchange(
                "/users/" + userId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        UserResponse userResponse = getResponse.getBody().getData();
        assertNotNull(userResponse);
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getFirstName(), userResponse.getFirstName());
        assertEquals(user.getLastName(), userResponse.getLastName());
        assertEquals(user.getImageUrl(), userResponse.getImageUrl());
        assertNotNull(userResponse.getUserDetail());
        assertEquals(user.getUserDetail().getPhone(), userResponse.getUserDetail().getPhone());
        assertEquals(user.getUserDetail().getAddress(), userResponse.getUserDetail().getAddress());
    }

    @Test
    void testGetUser_whenUserDoesNotExist_shouldReturn404() {
        long nonExistentId = 9999L;

        HttpEntity<Void> requestEntity = new HttpEntity<>(createAuthHeaders(adminUser));
        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                "/users/" + nonExistentId,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("user.not.found"));
    }

    @Test
    void testUpdateUser_shouldModifyFields_andPersistChanges() {
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("NuevoNombre");
        updateRequest.setLastName("NuevoApellido");
        updateRequest.setImageUrl("https://example.com/image2.jpg");
        updateRequest.setUserDetail(new UserDetailDTO("1234567890", "New Address", "en"));


        HttpEntity<UpdateUserRequest> requestEntity = new HttpEntity<>(updateRequest, createAuthHeaders(normalUser));
        ResponseEntity<ApiResponse<UserResponse>> updateResponse = restTemplate.exchange(
                "/users/" + normalUser.getId(),
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals(updateRequest.getFirstName(), updateResponse.getBody().getData().getFirstName());

        Optional<User> updatedUserOpt = userRepository.findById(normalUser.getId());
        assertTrue(updatedUserOpt.isPresent());
        User updatedUser = updatedUserOpt.get();
        assertEquals(updateRequest.getFirstName(), updatedUser.getFirstName());
        assertEquals(updateRequest.getLastName(), updatedUser.getLastName());
        assertEquals(updateRequest.getImageUrl(), updatedUser.getImageUrl());
    }
}

