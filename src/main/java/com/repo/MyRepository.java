package com.repo;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.entity.Student;
import com.entity.StudentDTO;
@Repository
public interface MyRepository {
	public List<Student> getAllStudents();
	public Student getStudent(Integer id);
	public String addStudent(Student s);
	public Student editStudent(Integer id,Student s);
	public Boolean deleteStudent(Integer id);
	public Student patchStudent(Integer id,StudentDTO s);
}
