package com.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.entity.Student;
import com.entity.StudentDTO;
import com.repo.MyRepository;
@Service
public class StudentService implements MyRepository{

	List<Student> list = new ArrayList<>(List.of(
	        new Student(1, "rohit", 23d),
	        new Student(2, "rithik", 49d)
	    ));
	@Override
	public List<Student> getAllStudents() {
		return list;
	}
	@Override
	public Student getStudent(Integer id) {
		for(Student s:list) {
			if(s.getId().equals(id)) {
				return s;
			}
		}
		return null;
	}
	@Override
	public String addStudent(Student s) {
		return (list.add(s))?"Student Inserted":"Student not inserted";
	}
	@Override
	public Student editStudent( @PathVariable Integer id,@RequestBody Student stu) {
		// TODO Auto-generated method stub
		for(Student s: list) {
			if(s.getId().equals(id)) {
				s.setMarks(stu.getMarks());
				s.setName(stu.getName());		
				return s;
			}
		}
		return null;
	}
	@Override
	public Boolean deleteStudent(Integer id) {
		// TODO Auto-generated method stub
		return list.removeIf( st->st.getId().equals(id));
	}
	@Override
	public Student patchStudent(Integer id, StudentDTO sd) {
		// TODO Auto-generated method stub
		for(Student s:list) {
			if(s.getId().equals(id)) {
				if(!s.getName().equals(null)) {
					s.setName(sd.getName());
				}
				if(!s.getMarks().equals(0d)) {
					s.setMarks(sd.getMarks());
				}
				return s;
				
			}
		}
		return null;
	}
	
	

}
