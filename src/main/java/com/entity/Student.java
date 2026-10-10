package com.entity;

public class Student {
	Integer id;
	String name;
	Double marks;
	
	public Student(Integer id, String name, Double i) {
		super();
		this.id = id;
		this.name = name;
		this.marks = i;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMarks() {
		return marks;
	}

	public void setMarks(Double marks) {
		this.marks = marks;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", marks=" + marks + "]";
	}
	
	
	
}
