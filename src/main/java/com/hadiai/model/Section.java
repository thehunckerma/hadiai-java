
package com.hadiai.model;

import com.fasterxml.jackson.annotation.*;
import com.hadiai.auditing.CommonProps;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name = "sections", uniqueConstraints = { @UniqueConstraint(columnNames = "token") })
public class Section extends CommonProps {
	@NotBlank
	@Size(max = 50)
	private String name;

	@Size(max = 250)
	private String description;

	private Boolean sessionOn;

	@NotBlank
	@Size(max = 20)
	private String token;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "sections_students", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "section_id") })
	private Set<User> students = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "sections_requests", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "section_id") })
	private Set<User> requests = new HashSet<>();

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "teacher_id", nullable = false)
	private User teacher;

	@OneToMany(mappedBy = "section", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonManagedReference // Prevent circular response
	private Set<Session> sessions = new HashSet<>();

	public Section() {
	}

	public Section(String name, String description, User teacher) {
		this.name = name;
		this.description = description;
		this.token = generateToken();
		this.teacher = teacher;
		this.sessionOn = false;
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

	public Boolean getSessionOn() {
		return sessionOn;
	}

	public void setSessionOn(Boolean sessionOn) {
		this.sessionOn = sessionOn;
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

	public Set<User> getRequests() {
		return requests;
	}

	public void setRequests(Set<User> requests) {
		this.requests = requests;
	}

	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}
	
	public Set<Session> getSessions() {
		return sessions;
	}

	private String generateToken() { // Helper function
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
