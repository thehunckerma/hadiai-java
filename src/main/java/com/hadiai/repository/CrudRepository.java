package com.hadiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hadiai.model.Crud;

public interface CrudRepository extends JpaRepository<Crud, Long> {
	List<Crud> findByPublished(boolean published);
	List<Crud> findByNameContaining(String name);
}
