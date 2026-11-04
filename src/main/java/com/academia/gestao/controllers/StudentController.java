package com.academia.gestao.controllers;

import com.academia.gestao.requests.StudentPatchRequestDTO;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.responses.StudentResponseDTO;
import com.academia.gestao.services.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;


    @GetMapping(path = "/{id}")
    public ResponseEntity<StudentResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }


    @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> findAll(
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable) {

        Page<StudentResponseDTO> studentsPage = studentService.findAll(pageable);
        return ResponseEntity.ok(studentsPage);
    }


    @PostMapping
    public ResponseEntity<StudentResponseDTO> create(@Valid @RequestBody StudentRequestDTO studentRequestDTO) {
        return new ResponseEntity<>(studentService.create(studentRequestDTO), HttpStatus.CREATED);
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody StudentRequestDTO studentUpdatedRequestDTO) {

        StudentResponseDTO updatedStudent = studentService.update(id, studentUpdatedRequestDTO);
        return ResponseEntity.ok(updatedStudent);

    }


    @PatchMapping(path = "/{id}")
    public ResponseEntity<StudentResponseDTO> partialUpdate(@PathVariable Long id, @Valid @RequestBody StudentPatchRequestDTO studentPatchRequestDTO) {

        StudentResponseDTO updatedStudent = studentService.partialUpdate(id, studentPatchRequestDTO);
        return ResponseEntity.ok(updatedStudent);
    }
}
