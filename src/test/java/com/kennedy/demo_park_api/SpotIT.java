package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.web.dto.SpotCreateDto;
import com.kennedy.demo_park_api.web.dto.SpotResponseDto;
import com.kennedy.demo_park_api.web.exception.ErrorMessage;
import jakarta.validation.constraints.AssertTrue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/spots/spots-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/spots/spots-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SpotIT {

    @Autowired
    WebTestClient testClient;

    String base_url = "/api/v1";

    @Test
    public void createSpot_withValidData_ReturnLocationStatus201(){
        testClient.post()
            .uri(base_url + "/spots")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
            .bodyValue(new SpotCreateDto("A-05", "FREE"))
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void createSpot_withRegisteredCode_ReturnErrorMessageStatus409(){
        testClient.post()
            .uri(base_url + "/spots")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
            .bodyValue(new SpotCreateDto("A-01", "FREE"))
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("status").isEqualTo(HttpStatus.CONFLICT.value())
            .jsonPath("method").isEqualTo("POST")
            .jsonPath("path").isEqualTo(base_url + "/spots");

    }

    @Test
    public void createSpot_withInvalidData_ReturnErrorMessageStatus422(){
        testClient.post()
                .uri(base_url + "/spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .bodyValue(new SpotCreateDto("", ""))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("status").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo(base_url + "/spots");

        testClient.post()
                .uri(base_url + "/spots")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .bodyValue(new SpotCreateDto("A-000", "UNOCCUPIED"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("status").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo(base_url + "/spots");

    }

    @Test
    public void searchSpot_withExistingCode_ReturnSpotStatus200(){
        SpotResponseDto responseBody = testClient.get()
                .uri(base_url + "/spots/A-01")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SpotResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(10);
        Assertions.assertThat(responseBody.getCode()).isEqualTo("A-01");
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("FREE");

    }

    @Test
    public void searchSpot_withNonExistingCode_ReturnErrorMessageStatus404(){
        ErrorMessage responseBody = testClient.get()
                .uri(base_url + "/spots/A-00")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();;
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

    }

}
