
package com.hadiai.repository;

import com.hadiai.model.Crud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrudRepository extends JpaRepository<Crud, Long> {
	List<Crud> findByPublished(boolean published);

	List<Crud> findByNameContaining(String name);
}
