package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.jwt.JwtToken;
import com.kennedy.demo_park_api.web.dto.UserLoginDto;

import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthenticationIT {

    @Autowired
    WebTestClient testClient;
    String base_url = "/api/v1";

    @Test
    public void authenticate_withValidCredentials_ReturnTokenWithStatus200(){
        JwtToken responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
    }

    @Test
    public void authenticate_withInvalidCredentials_ReturnTokenWithStatus400(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("ana@gmail.com", "000000"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("invalid@gmail.com", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void authenticate_withInvalidUsername_ReturnTokenWithStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
    @Test
    public void authenticate_withInvalidPassword_ReturnTokenWithStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("ana@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("ana@gmail.com", "123"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto("ana@gmail.com", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

    }
}
