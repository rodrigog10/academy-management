package com.academia.gestao.service;

import com.academia.gestao.domain.Student;
import com.academia.gestao.exceptions.BusinessRuleException;
import com.academia.gestao.exceptions.ResourceNotFoundException;
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

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        studentRequestDTO.setEmail("rodrigo@gmail.com");
        studentRequestDTO.setCpf("94159870066");

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(false);
        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(false);

        Student studentEntity = studentMapper.toStudentEntity(studentRequestDTO);
        studentEntity.setId(1L);

        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);

        StudentResponseDTO responseDTO = studentService.create(studentRequestDTO);

        assertNotNull(responseDTO);
        assertEquals(responseDTO.getName(), studentRequestDTO.getName());
        assertEquals(responseDTO.getEmail(), studentRequestDTO.getEmail());
        assertEquals(responseDTO.getCpf(), studentRequestDTO.getCpf());

        verify(studentRepository, times(1)).save(any(Student.class));
    }
    @Test
    @DisplayName("Deve lançar exceção quando o CPF já estiver cadastrado")
    void create_ShouldThrowBusiness_WhenCPFAlreadyExists () {

        // arrange
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo Bispo");
        studentRequestDTO.setCpf("94159870066");
        studentRequestDTO.setEmail("rodrigo@gmail.com");

        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(true);

        // act & assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with CPF: " +  studentRequestDTO.getCpf(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email já estiver cadastrado")
    void create_ShouldThrowException_WhenEmailAlreadyExists(){
        //arrange
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo Bispo");
        studentRequestDTO.setCpf("94159870066");
        studentRequestDTO.setEmail("rodrigo@gmail.com");

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(true);

        // act & assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with Email: " +  studentRequestDTO.getEmail(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar o estudante cadastrado")
    void findById_ShouldReturnStudent_WhenIdIsValid(){

        Student student = new Student();
        student.setId(1L);
        student.setName("José Rodrigo Bispo");
        student.setCpf("94159870066");
        student.setEmail("rodrigo@gmail.com");


        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

        StudentResponseDTO studentResponseDTO = studentService.findById(student.getId());

        assertNotNull(studentResponseDTO);
        assertEquals(student.getName(), studentResponseDTO.getName());
        assertEquals(student.getCpf(), studentResponseDTO.getCpf());
        assertEquals(student.getEmail(), studentResponseDTO.getEmail());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar o aluno")
    void findById_ShouldThrowException_WhenIdIsInvalid(){

        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> studentService.findById(student.getId()));

        assertEquals("Student not found with Id " + student.getId() + ".", exception.getMessage());

        verify(studentRepository, never()).save(any());
    }
}
