package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.web.dto.ClientCreateDto;
import com.kennedy.demo_park_api.web.dto.ClientResponseDto;
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
@Sql(scripts = "/sql/clients/clients-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClientIT {

    @Autowired
    WebTestClient testClient;
    String base_url = "/api/v1";

    @Test
    public void createClient_withValidData_ReturnClientWithStatus201(){
        ClientResponseDto responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"toby@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobias Ferreira", "91809057060"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getName()).isEqualTo("Tobias Ferreira");
        Assertions.assertThat(responseBody.getCpf()).isEqualTo("91809057060");
    }

    @Test
    public void createClient_withRegisteredCpf_ReturnErrorMessageWithStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"toby@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobias Ferreira", "02268984079"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createClient_withInvalidData_ReturnErrorMessageWithStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"toby@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"toby@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("toby", "01234567891"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"toby@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("toby", "918.090.570-60"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

    }

    @Test
    public void createClient_withUserWithoutPermission_ReturnErrorMessageWithStatus403(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(base_url + "/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient,"ana@gmail.com", "123456"))
                .bodyValue(new ClientCreateDto("Tobias Ferreira", "91809057060"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }
}
