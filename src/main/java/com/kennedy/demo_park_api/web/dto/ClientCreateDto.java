package com.kennedy.demo_park_api.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ClientCreateDto {

    @NotNull
    @Size(min = 5, max = 100)
    private String name;

    @CPF
    @Size(min = 11, max = 11)
    private String cpf;
}
