
package com.hadiai.model;

import com.fasterxml.jackson.annotation.*;
import com.hadiai.auditing.CommonProps;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
public class User extends CommonProps {

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	private String image;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "students")
	// @JsonBackReference // Prevent circular response (Don't enable this otherwise
	// json responses will break)
	private Set<Section> studentSections = new HashSet<>(); // student's sections

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "requests")
	// @JsonBackReference // Prevent circular response
	private Set<Section> requests = new HashSet<>(); // student's sections with pending join request

	@OneToMany(mappedBy = "teacher")
	@JsonManagedReference // Prevent circular response
	private Set<Section> teacherSections = new HashSet<>(); // student's sections

	@OneToMany(mappedBy = "student")
	@JsonManagedReference // Prevent circular response
	private Set<Presence> presence = new HashSet<>();

	public User() {
	}

	public User(String username, String image, String email, String password) {
		this.username = username;
		this.image = image;
		this.email = email;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Section> getStudentSections() {
		return studentSections;
	}

	public void setStudentSections(Set<Section> studentSections) {
		this.studentSections = studentSections;
	}

	public Set<Section> getRequests() {
		return requests;
	}

	public void setRequests(Set<Section> requests) {
		this.requests = requests;
	}

	public Set<Section> getTeacherSections() {
		return teacherSections;
	}

	public void setTeacherSections(Set<Section> teacherSections) {
		this.teacherSections = teacherSections;
	}
}
