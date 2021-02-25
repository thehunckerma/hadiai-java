
package com.hadiai.repository;

import com.hadiai.model.Presence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
	List<Presence> findBySession_IdAndStudent_Id(Long sessionId, Long userId);

	Presence getById(Long id);
}
