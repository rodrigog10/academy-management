package com.academia.gestao.responses;
import com.academia.gestao.domain.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String cpf;
    private LocalDate birthDate;
    private LocalDateTime createdAt;

}
