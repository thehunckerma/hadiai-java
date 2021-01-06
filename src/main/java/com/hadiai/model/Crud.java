
package com.hadiai.model;

import javax.persistence.*;

@Entity
@Table(name = "cruds")
public class Crud {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "published")
	private boolean published;

	public Crud() {

	}

	public Crud(String name, String description, boolean published) {
		this.name = name;
		this.description = description;
		this.published = published;
	}

	public long getId() {
		return id;
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

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean isPublished) {
		this.published = isPublished;
	}

	@Override
	public String toString() {
		return "Crud [id=" + id + ", name=" + name + ", desc=" + description + ", published=" + published + "]";
	}

}
