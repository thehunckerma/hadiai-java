
package com.hadiai.repository;

import com.hadiai.model.Session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

	Optional<Session> findById(Long id);

	Session getById(Long id);

	List<Session> getBySection_Id(Long id);
}
