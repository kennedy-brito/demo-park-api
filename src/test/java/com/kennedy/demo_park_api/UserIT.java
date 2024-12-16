package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.web.dto.UserCreateDto;
import com.kennedy.demo_park_api.web.dto.UserPasswordDto;
import com.kennedy.demo_park_api.web.dto.UserResponseDto;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

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

    @Test
    public void createUser_withInvalidUsername_ReturnErrorMessageWithStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@gmail", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void createUser_withInvalidPassword_ReturnErrorMessageWithStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@gmail.com", "123"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("potter@gmail.com", "12345678"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void createUser_withRepeatedUsername_ReturnErrorMessageWithStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());

    }

    @Test
    public void searchUser_withExistingId_ReturnUserWithStatusCode200(){

        // admin accessing his own data
        UserResponseDto responseBody = testClient
                .get()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("ana@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        //admin searching data of another user
        responseBody = testClient
                .get()
                .uri(base_url + "/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("bia@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");

        //client searching his own data
        responseBody = testClient
                .get()
                .uri(base_url + "/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("bia@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENT");
    }

    @Test
    public void searchUser_withNonExistingId_ReturnErrorMessageWithStatusCode404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(base_url + "/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void searchUser_withClientUserSearchingOtherUser_ReturnErrorMessageWithStatusCode403(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(base_url + "/users/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void updatePassword_withValidData_ReturnStatusCode204(){
        testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri(base_url + "/users/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_withDifferentUsers_ReturnErrorMessageWithStatusCode403(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(base_url + "/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "456789", "456789"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        responseBody = testClient
                .patch()
                .uri(base_url + "/users/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "456789", "456789"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void updatePassword_withInvalidData_ReturnErrorMessageWithStatus422(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("12345678", "12345678", "12345678"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void updatePassword_withInvalidPasswords_ReturnErrorMessageWithStatus400(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "123456", "000000"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        responseBody = testClient
                .patch()
                .uri(base_url + "/users/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123466", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void listUser_withUserWithPermission_ReturnUserListWithStatus200(){
        List<UserResponseDto> responseBody = testClient
                .get()
                .uri(base_url + "/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.size()).isEqualTo(3);

    }

    @Test
    public void listUser_withUserWithoutPermission_ReturnErrorMessageWithStatus403(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(base_url + "/users")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"bia@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }

}
