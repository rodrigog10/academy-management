package com.academia.gestao.service;

import com.academia.gestao.domain.Student;
import com.academia.gestao.exceptions.BusinessRuleException;
import com.academia.gestao.exceptions.ResourceNotFoundException;
import com.academia.gestao.mapper.StudentMapper;
import com.academia.gestao.repository.StudentRepository;
import com.academia.gestao.requests.StudentPatchRequestDTO;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.responses.StudentResponseDTO;
import com.academia.gestao.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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


    //private helpers

    private Student createStudentEntity(){

        Student studentEntity = new Student();
        studentEntity.setId(1L);
        studentEntity.setName("José Rodrigo");
        studentEntity.setCpf("94159870066");
        studentEntity.setEmail("rodrigo@gmail.com");

        return studentEntity;
    }

    private StudentRequestDTO createStudentRequestDTO(){

        StudentRequestDTO studentRequestDTO = new StudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo");
        studentRequestDTO.setCpf("94159870066");
        studentRequestDTO.setEmail("rodrigo@gmail.com");

        return studentRequestDTO;
    }

    // tests

    @Test
    @DisplayName("Deve cadastrar o estudante e retornar o DTO quando os dados forem válidos")
    void create_ShouldCreateStudent_WhenDataIsValid() {

        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(false);
        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(false);

        Student studentEntity = createStudentEntity();

        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);

        StudentResponseDTO studentResponseDTO = studentService.create(studentRequestDTO);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository, times(1)).save(captor.capture());

        Student studentCaptured = captor.getValue();

        assertNotNull(studentCaptured);
        assertEquals(studentRequestDTO.getName(), studentCaptured.getName());
        assertEquals(studentRequestDTO.getEmail(), studentCaptured.getEmail());
        assertEquals(studentRequestDTO.getCpf(), studentCaptured.getCpf());

        assertNotNull(studentResponseDTO);
        assertEquals(studentRequestDTO.getName(), studentResponseDTO.getName());
        assertEquals(studentRequestDTO.getEmail(), studentResponseDTO.getEmail());
        assertEquals(studentRequestDTO.getCpf(), studentResponseDTO.getCpf());

    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o CPF já estiver cadastrado")
    void create_ShouldThrowBusinessRuleException_WhenCpfAlreadyExists() {

        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();

        when(studentRepository.existsByCpf(studentRequestDTO.getCpf())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with CPF: " + studentRequestDTO.getCpf(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o e-mail já estiver cadastrado")
    void create_ShouldThrowBusinessRuleException_WhenEmailAlreadyExists() {

        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();

        when(studentRepository.existsByEmail(studentRequestDTO.getEmail())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.create(studentRequestDTO));

        assertEquals("Student already registered with Email: " + studentRequestDTO.getEmail(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar o estudante quando o ID existir")
    void findById_ShouldReturnStudentResponseDTO_WhenIdExists() {

        Student studentEntity = createStudentEntity();

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));

        StudentResponseDTO studentResponseDTO = studentService.findById(studentEntity.getId());

        assertNotNull(studentResponseDTO);
        assertEquals(studentEntity.getName(), studentResponseDTO.getName());
        assertEquals(studentEntity.getCpf(), studentResponseDTO.getCpf());
        assertEquals(studentEntity.getEmail(), studentResponseDTO.getEmail());

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não for encontrado")
    void findById_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {

        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(student.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> studentService.findById(student.getId()));

        assertEquals("Student not found with Id " + student.getId() + ".", exception.getMessage());

    }

    @Test
    @DisplayName("Deve retornar uma página de estudantes com sucesso")
    void findAll_ShouldReturnPageOfStudents_WhenSuccessful() {

        Pageable pageable = PageRequest.of(0, 10);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("José Rodrigo");
        student1.setEmail("rodrigo@gmail.com");
        student1.setCpf("94159870066");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Ana Clara");
        student2.setEmail("anaclara@gmail.com");
        student2.setCpf("00675487099");

        List<Student> studentList = List.of(student1, student2);
        Page<Student> studentPage = new PageImpl<>(studentList, pageable, studentList.size());

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        Page<StudentResponseDTO> response = studentService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(student1.getName(), response.getContent().get(0).getName());
        assertEquals(student2.getName(), response.getContent().get(1).getName());

        verify(studentRepository, times(1)).findAll(pageable);

    }

    @Test
    @DisplayName("Deve atualizar um cadastro por completo corretamente")
    void update_ShouldReturnUpdatedStudent_WhenSuccessful() {

        Student studentEntity = createStudentEntity();
        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();
        studentRequestDTO.setName("José Rodrigo Bispo");
        studentRequestDTO.setEmail("rodrigobispo@gmail.com");
        studentRequestDTO.setCpf("00675487099");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.save(studentEntity)).thenReturn(studentEntity);
        when(studentRepository.existsByCpfAndIdNot(studentRequestDTO.getCpf(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.existsByEmailAndIdNot(studentRequestDTO.getEmail(), studentEntity.getId())).thenReturn(false);

        StudentResponseDTO studentResponseDTO = studentService.update(studentEntity.getId(), studentRequestDTO);

        assertNotNull(studentResponseDTO);
        assertEquals(studentEntity.getId(), studentResponseDTO.getId());

        assertEquals(studentRequestDTO.getName(), studentResponseDTO.getName());
        assertEquals(studentRequestDTO.getEmail(), studentResponseDTO.getEmail());
        assertEquals(studentRequestDTO.getCpf(), studentResponseDTO.getCpf());

        verify(studentRepository, times(1)).findById(studentEntity.getId());
        verify(studentRepository, times(1)).existsByEmailAndIdNot(studentRequestDTO.getEmail(), studentEntity.getId());
        verify(studentRepository, times(1)).existsByCpfAndIdNot(studentRequestDTO.getCpf(), studentEntity.getId());

    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o CPF já estiver cadastrado")
    void update_ShouldThrowBusinessRuleException_WhenCpfAlreadyExists() {

        Student studentEntity = createStudentEntity();
        studentEntity.setId(1L);

        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();
        studentRequestDTO.setCpf("00675487099");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));

        when(studentRepository.existsByCpfAndIdNot(studentRequestDTO.getCpf(), studentEntity.getId())).thenReturn(true);
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.update(studentEntity.getId(), studentRequestDTO));

        assertEquals("CPF already registered for another student: " + studentRequestDTO.getCpf(), exception.getMessage());

        verify(studentRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o E-mail já estiver cadastrado")
    void update_ShouldThrowBusinessRuleException_WhenEmailAlreadyExists() {

        Student studentEntity = createStudentEntity();

        StudentRequestDTO studentRequestDTO = createStudentRequestDTO();
        studentRequestDTO.setEmail("anaclara@gmail.com");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));

        when(studentRepository.existsByEmailAndIdNot(studentRequestDTO.getEmail(), studentEntity.getId())).thenReturn(true);

        BusinessRuleException exception =
                assertThrows(BusinessRuleException.class, () -> studentService.update(studentEntity.getId(), studentRequestDTO));

        assertEquals("Email already registered for another student: " + studentRequestDTO.getEmail(), exception.getMessage());

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar estudante com ID inexistente")
    void update_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist() {

        Long nonExistentId = 99L;
        StudentRequestDTO requestDTO = createStudentRequestDTO();

        when(studentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.update(nonExistentId, requestDTO)
        );

        assertEquals("Student not found with Id " + nonExistentId + ".", exception.getMessage());

        verify(studentRepository, times(1)).findById(nonExistentId);
        verify(studentRepository, never()).save(any());
    }


    //partialUpdate, some cases

    @Test
    @DisplayName("Deve realizar uma atualização parcial com sucesso quando apenas o nome for atualizado")

    void partialUpdate_ShouldPartialUpdate_WhenOnlyNameIsSent() {

        Student studentEntity = createStudentEntity();
        String originalEmail = studentEntity.getEmail();
        String originalCpf = studentEntity.getCpf();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        studentPatchRequestDTO.setName("José Rodrigo Bispo");
        //aqui, diferentemente dos outros testes, é levado em consideração o caso em que o front-end está adaptado a enviar apenas o campo alterado (demais nulos), sem se envolver com os outros de forma alguma.

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));

        when(studentRepository.save(studentEntity)).thenReturn(studentEntity);

        StudentResponseDTO studentResponseDTO = studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO);

        assertNotNull(studentResponseDTO);
        assertEquals(studentPatchRequestDTO.getName(), studentResponseDTO.getName());
        assertEquals(originalEmail, studentResponseDTO.getEmail());
        assertEquals(originalCpf, studentResponseDTO.getCpf());

        verify(studentRepository, times(1)).findById(studentEntity.getId());
        verify(studentRepository, times(1)).save(studentEntity);
        verify(studentRepository, never()).existsByCpfAndIdNot(any(), any());
        verify(studentRepository, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    @DisplayName("Deve realizar uma atualização parcial com sucesso quando o email for atualizado mas o cpf for mantido")
    void partialUpdate_ShouldUpdateEmail_WhenKeepingSameCpf(){

        Student studentEntity = createStudentEntity();

        // A captura definida abaixo serve para assegurar o código de uma possível mutação errada no ambiente real. Garante que, se um dado não mutado sofrer uma mutação inesperada por uma inserção incorreta no fluxo da regra de negócio real, o teste reconhece o erro e falha.
        String originalName = studentEntity.getName();
        String originalCpf = studentEntity.getCpf();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        studentPatchRequestDTO.setEmail("rodrigo123@gmail.com");

        // Essa definição que não tem assimilação com o objetivo principal do teste (atualizar o email) serve para simular o comportamento de um front-end mal-adaptado que reenvia no final todos os dados além do único dado que foi alterado pelo user. O backend define um contrato previsível para o PATCH.
        studentPatchRequestDTO.setCpf(originalCpf);
        studentPatchRequestDTO.setName(originalName);

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.existsByCpfAndIdNot(studentEntity.getCpf(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.save(studentEntity)).thenReturn(studentEntity);

        StudentResponseDTO studentResponseDTO = studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO);

        assertNotNull(studentResponseDTO);
        assertEquals(originalName, studentResponseDTO.getName());
        assertEquals(studentPatchRequestDTO.getEmail(), studentResponseDTO.getEmail());
        assertEquals(originalCpf, studentResponseDTO.getCpf());

        verify(studentRepository, times(1)).existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId());
        verify(studentRepository, times(1)).existsByCpfAndIdNot(originalCpf, studentEntity.getId());
        verify(studentRepository, times(1)).save(studentEntity);

    }

    @Test
    @DisplayName("Deve realizar uma atualização parcial com sucesso quando o cpf for atualizado mas o email for mantido")
    void partialUpdate_ShouldUpdateCpf_WhenKeepingSameEmail(){

        Student studentEntity = createStudentEntity();
        // define um cpf desatualizado.
        studentEntity.setCpf("25847769440");

        // A captura definida abaixo serve para assegurar o código de uma possível mutação errada no ambiente real. Garante que, se um dado não mutado sofrer uma mutação inesperada por uma inserção incorreta no fluxo da regra de negócio real, o teste reconhece o erro e falha.
        String originalName = studentEntity.getName();
        String originalEmail = studentEntity.getEmail();

        String originalCpf = studentEntity.getCpf();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();

        studentPatchRequestDTO.setCpf("94159870066");

        // Essa definição que não tem assimilação com o objetivo principal do teste (atualizar o cpf) serve para simular o comportamento de um front-end mal-adaptado que reenvia no final todos os dados além do único dado que foi alterado pelo user. O backend define um contrato previsível para o PATCH.
        studentPatchRequestDTO.setEmail(originalEmail);
        studentPatchRequestDTO.setName(originalName);

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.existsByCpfAndIdNot(studentPatchRequestDTO.getCpf(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.save(studentEntity)).thenReturn(studentEntity);

        StudentResponseDTO studentResponseDTO = studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO);

        assertNotNull(studentResponseDTO);
        assertEquals(originalEmail, studentResponseDTO.getEmail());
        assertEquals(originalName, studentResponseDTO.getName());
        assertEquals(studentPatchRequestDTO.getCpf(), studentResponseDTO.getCpf());
        assertNotEquals(originalCpf, studentResponseDTO.getCpf());

        verify(studentRepository, times(1)).existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId());
        verify(studentRepository, times(1)).existsByCpfAndIdNot(studentPatchRequestDTO.getCpf(), studentEntity.getId());
        verify(studentRepository, times(1)).save(studentEntity);
    }

    @Test
    @DisplayName("Deve realizar uma atualização parcial com sucesso quando apenas o nome for alterado e os demais campos forem reenviados inalterados")
    void partialUpdate_ShouldUpdateName_WhenKeepingSameCpfAndEmail(){

        Student studentEntity = createStudentEntity();
        String originalName = studentEntity.getName();
        String originalCpf = studentEntity.getCpf();
        String originalEmail = studentEntity.getEmail();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        studentPatchRequestDTO.setName("José Rodrigo Bispo");
        studentPatchRequestDTO.setEmail(originalEmail);
        studentPatchRequestDTO.setCpf(originalCpf);

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.existsByCpfAndIdNot(studentPatchRequestDTO.getCpf(), studentEntity.getId())).thenReturn(false);
        when(studentRepository.save(studentEntity)).thenReturn(studentEntity);

        StudentResponseDTO studentResponseDTO = studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO);

        assertNotNull(studentResponseDTO);
        assertNotEquals(originalName, studentResponseDTO.getName());
        assertEquals(studentPatchRequestDTO.getName(), studentResponseDTO.getName());
        assertEquals(originalEmail, studentResponseDTO.getEmail());
        assertEquals(originalCpf, studentResponseDTO.getCpf());

        verify(studentRepository, times(1)).existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId());
        verify(studentRepository, times(1)).existsByCpfAndIdNot(studentPatchRequestDTO.getCpf(), studentEntity.getId());
        verify(studentRepository, times(1)).save(studentEntity);
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o CPF já estiver cadastrado")
    void partialUpdate_ShouldThrowBusinessRuleException_WhenCpfAlreadyExists(){
        Student studentEntity = createStudentEntity();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        studentPatchRequestDTO.setCpf("00675487099");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.existsByCpfAndIdNot(studentPatchRequestDTO.getCpf(), studentEntity.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO));

        assertEquals("CPF already registered for another student: " + studentPatchRequestDTO.getCpf(), exception.getMessage());

        verify(studentRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o E-mail já estiver cadastrado")
    void partialUpdate_ShouldThrowBusinessRuleException_WhenEmailAlreadyExists(){
        Student studentEntity = createStudentEntity();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        //Email existente
        studentPatchRequestDTO.setEmail("rodrigo1@gmail.com");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));
        when(studentRepository.existsByEmailAndIdNot(studentPatchRequestDTO.getEmail(), studentEntity.getId())).thenReturn(true);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO));

        assertEquals("Email already registered for another student: " + studentPatchRequestDTO.getEmail(), exception.getMessage());

        verify(studentRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não for encontrado")
    void partialUpdate_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist(){
        Student studentEntity = createStudentEntity();

        StudentPatchRequestDTO studentPatchRequestDTO = new StudentPatchRequestDTO();
        studentPatchRequestDTO.setName("José Rodrigo Bispo");
        studentPatchRequestDTO.setEmail("rodrigobispo@gmail.com");
        studentPatchRequestDTO.setCpf("94159870066");

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> studentService.partialUpdate(studentEntity.getId(), studentPatchRequestDTO));

        assertEquals("Student not found with Id " + studentEntity.getId() + ".", exception.getMessage());

        verify(studentRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve deletar um cadastro corretamente quando o Id existir")
    void delete_ShouldDelete_WhenIdExists(){
        Student studentEntity = createStudentEntity();

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.of(studentEntity));

        studentService.delete(studentEntity.getId());
        verify(studentRepository, times(1)).findById(studentEntity.getId());
        verify(studentRepository, times(1)).delete(studentEntity);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar estudante com ID inexistente")
    void delete_ShouldThrowResourceNotFoundException_WhenIdDoesNotExist(){
        Student studentEntity = new Student();

        Long nonExistentId = 99L;
        studentEntity.setId(nonExistentId);

        when(studentRepository.findById(studentEntity.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> studentService.delete(studentEntity.getId()));

        verify(studentRepository, never()).delete(any());
        assertEquals("Student not found with Id " + studentEntity.getId() + ".", exception.getMessage());
    }

}
