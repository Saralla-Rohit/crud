package com.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.entity.Student;
import com.entity.StudentDTO;
import com.service.StudentService;

@RestController
public class MyController {
	final StudentService service;

	MyController(StudentService service) {
		this.service = service;
	}
	@GetMapping("/")
	public List<Student> getAllStudents(){
		return service.getAllStudents();
	}
	@GetMapping("/{id}")
	public Student getStudent(@PathVariable int id) {
		return service.getStudent(id);
	}
	@PostMapping("/")
	public String addStudent(@RequestBody Student s) {
		return service.addStudent(s);
	}
	@PutMapping("/{id}")
	public Student editStudent(@PathVariable Integer id,@RequestBody Student s) {
		return service.editStudent(id, s);
	}
	@DeleteMapping("/{id}")
	public String deleteStudent( @PathVariable Integer id) {
		return (service.deleteStudent(id)?"Student deleted":"Unable to Delete");
	}
	@PatchMapping("/{id}")
	public Student patchStudent(@PathVariable Integer id,@RequestBody StudentDTO s) {
		return service.patchStudent(id, s);
	}
}
