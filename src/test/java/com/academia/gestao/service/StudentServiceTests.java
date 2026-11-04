package com.academia.gestao.service;

import com.academia.gestao.exceptions.BusinessRuleException;
import com.academia.gestao.repository.StudentRepository;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Deve lançar exceção quando o CPF já estiver cadastrado")
    void create_ShouldThrowBusiness_WhenCPFIsInvalid () {

        // arrange

        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo Bispo");
        studentRequestDTO.setCpf("23130893040");
        studentRequestDTO.setEmail("joserodrigo@gmail.com");

        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(true);

        // act & assert

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with CPF: " +  studentRequestDTO.getCpf(), exception.getMessage());

        verify(studentRepository, never()).save(any());
      }

    @Test
    @DisplayName("Deve lançar exceção quando o email já estiver cadastrado")
    void create_ShouldThrowException_WhenEmailIsInvalid(){

    }
}
