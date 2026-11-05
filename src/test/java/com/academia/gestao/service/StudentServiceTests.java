package com.academia.gestao.service;

import com.academia.gestao.domain.Student;
import com.academia.gestao.exceptions.BusinessRuleException;
import com.academia.gestao.mapper.StudentMapper;
import com.academia.gestao.repository.StudentRepository;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.responses.StudentResponseDTO;
import com.academia.gestao.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Spy
    private StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);

    @Test
    @DisplayName("Deve cadastrar o aluno corretamente com os dados postos ao banco de dados")
    void create_ShouldCreateStudent_WhenDataIsValid(){

        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo Bispo");
        studentRequestDTO.setEmail("joserodrigo@gmail.com");
        studentRequestDTO.setCpf("46516052069");

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(false);
        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(false);

        Student studentEntity = studentMapper.toStudentEntity(studentRequestDTO);
        studentEntity.setId(1L);

        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);

        StudentResponseDTO responseDTO = studentService.create(studentRequestDTO);

        // asserts here
        // at this point, we need to ensure that the values in responseDTO are equal to the values in studentEntity, so:
        assertNotNull(responseDTO);
        assertEquals(responseDTO.getName(), studentRequestDTO.getName());
        assertEquals(responseDTO.getEmail(), studentRequestDTO.getEmail());
        assertEquals(responseDTO.getCpf(), studentRequestDTO.getCpf());


        // verify here
        verify(studentRepository, times(1)).save(any(Student.class));
    }
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
        //arrange
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("Ana Clara Alves");
        studentRequestDTO.setCpf("94159870066");
        studentRequestDTO.setEmail("anaclara123@gmail.com");

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(true);

        // act & assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with Email: " +  studentRequestDTO.getEmail(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }
}
