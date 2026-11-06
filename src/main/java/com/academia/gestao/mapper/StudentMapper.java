package com.academia.gestao.mapper;

import com.academia.gestao.domain.Student;
import com.academia.gestao.requests.StudentPatchRequestDTO;
import com.academia.gestao.requests.StudentRequestDTO;
import com.academia.gestao.responses.StudentResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")

public interface StudentMapper {

    // dto to entity
    Student toStudentEntity(StudentRequestDTO studentRequestDTO);

    // entity to dto
    StudentResponseDTO toStudentDTO(Student studentEntity);

    //change all about the student
    void updateEntityFromDTO(StudentRequestDTO studentRequestDTO, @MappingTarget Student studentEntity);

    //change something about the student
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialEntityUpdateFromDTO(StudentPatchRequestDTO studentPatchRequestDTO, @MappingTarget Student studentEntity);


}
