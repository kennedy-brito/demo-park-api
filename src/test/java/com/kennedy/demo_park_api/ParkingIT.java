package com.kennedy.demo_park_api;

import com.kennedy.demo_park_api.web.dto.ParkingCreateDto;
import com.kennedy.demo_park_api.web.dto.ParkingResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Sql(scripts = "/sql/parking/parking-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ParkingIT {
    @Autowired
    WebTestClient testClient;

    String base_url = "/api/v1";

    @Test
    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createCheckIn_WithValidData_ReturnCreatedAndLocation(){
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("AZUL").clientCpf("09191773016")
                .build();

        testClient.post()
                .uri(base_url + "/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("plate").isEqualTo("WER-1111")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO 1.0")
                .jsonPath("color").isEqualTo("AZUL")
                .jsonPath("clientCpf").isEqualTo("09191773016")
                .jsonPath("receipt").exists()
                .jsonPath("entryDate").exists()
                .jsonPath("spotCode").exists();
    }

    @Test
    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createCheckIn_WithClientRole_ReturnErrorMessageStatus403(){
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("AZUL").clientCpf("09191773016")
                .build();

        testClient.post()
                .uri(base_url + "/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo(base_url + "/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void createCheckIn_WithInvalidData_ReturnErrorMessageStatus422(){
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .plate("").brand("").model("")
                .color("").clientCpf("")
                .build();

        testClient.post()
                .uri(base_url + "/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo(base_url + "/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createCheckIn_WithNonExistingCpf_ReturnErrorMessageStatus404(){
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("AZUL").clientCpf("66880882072")
                .build();

        testClient.post()
                .uri(base_url + "/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo(base_url + "/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }
    @Test
    @Sql(scripts = "/sql/parking/parking-occupied-spots-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void createCheckIn_WithNonFreeSpot_ReturnErrorMessageStatus404(){
        ParkingCreateDto createDto = ParkingCreateDto.builder()
                .plate("WER-1111").brand("FIAT").model("PALIO 1.0")
                .color("AZUL").clientCpf("09191773016")
                .build();

        testClient.post()
                .uri(base_url + "/parking/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo(base_url + "/parking/check-in")
                .jsonPath("method").isEqualTo("POST");
    }

    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void findCheckIn_WithAdminRole_ReturnDataStatus200(){

        testClient.get()
                .uri(base_url + "/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("plate").isEqualTo("FIT-1020")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("VERDE")
                .jsonPath("clientCpf").isEqualTo("98401203015")
                .jsonPath("receipt").isEqualTo("20230313-101300")
                .jsonPath("entryDate").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("spotCode").isEqualTo("A-01");
    }

    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void findCheckIn_WithClientRole_ReturnDataStatus200(){

        testClient.get()
                .uri(base_url + "/parking/check-in/{receipt}", "20230313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("plate").isEqualTo("FIT-1020")
                .jsonPath("brand").isEqualTo("FIAT")
                .jsonPath("model").isEqualTo("PALIO")
                .jsonPath("color").isEqualTo("VERDE")
                .jsonPath("clientCpf").isEqualTo("98401203015")
                .jsonPath("receipt").isEqualTo("20230313-101300")
                .jsonPath("entryDate").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("spotCode").isEqualTo("A-01");
    }
    @Sql(scripts = "/sql/parking/parking-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void findCheckIn_WithNonExistingReceipt_ReturnErrorMessageStatus404(){

        testClient.get()
                .uri(base_url + "/parking/check-in/{receipt}", "20230313-99999")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo(base_url + "/parking/check-in/20230313-99999")
                .jsonPath("method").isEqualTo("GET");
    }
}
