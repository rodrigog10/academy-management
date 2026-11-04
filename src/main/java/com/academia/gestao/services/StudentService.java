package com.academia.gestao.services;

import com.academia.gestao.domain.Student;
import com.academia.gestao.exceptions.BusinessRuleException;
import com.academia.gestao.exceptions.ResourceNotFoundException;
import com.academia.gestao.mapper.StudentMapper;
import com.academia.gestao.repository.StudentRepository;
import com.academia.gestao.requests.StudentPatchRequestDTO;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.responses.StudentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    // private methods

    private Student findEntityById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with Id " + id + "."));
    }

    // to create
    private void validateExistFields(StudentRequestDTO studentRequestDTO) {
        if (studentRepository.existsByCpf(studentRequestDTO.getCpf())) {
            throw new BusinessRuleException("Student already registered with CPF: " + studentRequestDTO.getCpf());
        }
        if (studentRepository.existsByEmail(studentRequestDTO.getEmail())) {
            throw new BusinessRuleException("Student already registered with Email: " + studentRequestDTO.getEmail());
        }
    }

    // to put method
    private void validateUniqueFields(StudentRequestDTO studentRequestDTO, Long id) {
        if (studentRepository.existsByCpfAndIdNot(studentRequestDTO.getCpf(), id)) {
            throw new BusinessRuleException("CPF already registered for another student: " + studentRequestDTO.getCpf());
        }
        if (studentRepository.existsByEmailAndIdNot(studentRequestDTO.getEmail(), id)) {
            throw new BusinessRuleException("Email already registered for another student: " + studentRequestDTO.getEmail());
        }
    }

    // to patch method
    private void partialValidateUniqueFields(StudentPatchRequestDTO studentPartialRequestDTO, Long id) {
        if (studentPartialRequestDTO.getCpf() != null &&
                studentRepository.existsByCpfAndIdNot(studentPartialRequestDTO.getCpf(), id)) {
            throw new BusinessRuleException("CPF already registered for another student: " + studentPartialRequestDTO.getCpf());
        }
        if (studentPartialRequestDTO.getEmail() != null &&
                studentRepository.existsByEmailAndIdNot(studentPartialRequestDTO.getEmail(), id)) {
            throw new BusinessRuleException("Email already registered for another student: " + studentPartialRequestDTO.getEmail());
        }
    }

    // public methods

    @Transactional
    public StudentResponseDTO create(StudentRequestDTO studentRequestDTO) {
        validateExistFields(studentRequestDTO);

        Student studentEntity = studentMapper.toStudentEntity(studentRequestDTO);
        Student savedStudent = studentRepository.save(studentEntity);

        return studentMapper.toStudentDTO(savedStudent);
    }

    public StudentResponseDTO findById(Long id) {
        Student studentEntity = findEntityById(id);
        return studentMapper.toStudentDTO(studentEntity);
    }

    public Page<StudentResponseDTO> findAll(Pageable pageable) {
        Page<Student> allStudentsEntities = studentRepository.findAll(pageable);
        return allStudentsEntities.map(studentMapper::toStudentDTO);
    }

    @Transactional
    public StudentResponseDTO update(Long id, StudentRequestDTO studentRequestDTO) {
        Student studentEntity = findEntityById(id);
        validateUniqueFields(studentRequestDTO, id);

        studentMapper.updateEntityFromDTO(studentRequestDTO, studentEntity);
        Student alunoUpdatedEntity = studentRepository.save(studentEntity);

        return studentMapper.toStudentDTO(alunoUpdatedEntity);
    }

    @Transactional
    public StudentResponseDTO partialUpdate(Long id, StudentPatchRequestDTO studentPartialRequestDTO) {
        Student studentEntity = findEntityById(id);
        partialValidateUniqueFields(studentPartialRequestDTO, id);

        studentMapper.partialEntityUpdateFromDTO(studentPartialRequestDTO, studentEntity);
        Student studentUpdatedEntity = studentRepository.save(studentEntity);

        return studentMapper.toStudentDTO(studentUpdatedEntity);
    }

    // delete student
    @Transactional
    public void delete(Long id) {
        Student studentEntity = findEntityById(id);
        studentRepository.delete(studentEntity);
    }
}