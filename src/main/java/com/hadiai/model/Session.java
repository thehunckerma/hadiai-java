
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

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sessions")
public class Session extends CommonProps {

	private boolean end;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "sessions_students", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "session_id") })
	private Set<User> students = new HashSet<>();

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "section_id", nullable = false)
	private Section section;

	@OneToMany(mappedBy = "session")
	@JsonManagedReference // Prevent circular response
	private Set<Presence> presence = new HashSet<>();

	public Session() {
	}

	public Session(Section section) {
		this.section = section;
		this.end = false;
	}

	public Set<User> getStudents() {
		return students;
	}

	public void setStudents(Set<User> students) {
		this.students = students;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public boolean getEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}
}
