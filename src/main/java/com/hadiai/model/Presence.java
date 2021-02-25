
package com.hadiai.model;

import com.fasterxml.jackson.annotation.*;
import com.hadiai.auditing.CommonProps;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "presences")
public class Presence extends CommonProps {
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "student_id", nullable = false)
	private User student;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	public Presence() {
	}

	public Presence(Session session, User student) {
		this.student = student;
		this.session = session;
	}

	public User getStudent() {
		return student;
	}

	public void setStudents(User student) {
		this.student = student;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
