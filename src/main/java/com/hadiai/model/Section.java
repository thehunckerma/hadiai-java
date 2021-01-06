
package com.hadiai.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "sections", uniqueConstraints = { @UniqueConstraint(columnNames = "token") })
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 50)
	private String name;

	@Size(max = 250)
	private String description;

	@NotBlank
	@Size(max = 20)
	private String token;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "sections_students", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "section_id") })
	private Set<User> students = new HashSet<>();

	@ManyToOne
	// @JsonManagedReference
	@JoinColumn(name = "teacher_id", nullable = false)
	private User teacher;

	public Section() {
	}

	public Section(String name, String description, User teacher) {
		this.name = name;
		this.description = description;
		this.token = generateToken();
		this.teacher = teacher;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = generateToken();
	}

	public Set<User> getStudents() {
		return students;
	}

	public void setStudents(Set<User> students) {
		this.students = students;
	}

	public User getTeacher() {
		return teacher;
	}

	public void setTeacher(User teacher) {
		this.teacher = teacher;
	}

	private String generateToken() {
		Random rand = new Random();
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		String token = String.valueOf(rand.nextInt(10));
		for (int i = 0; i < 9; i++) {
			token = token + alphabet[rand.nextInt(26)];
			token = token + String.valueOf(rand.nextInt(10));
		}
		return token;
	}
}
