package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.web.dto.UserCreateDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient testClient;
    String base_url = "/api/v1";
    @Test
    public void createUser_withValidUsernameAndPassword_ReturnCreatedUserWithStatus201(){

        UserResponseDto responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("potter@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

}
