package com.example.spotifyscrobble.listening.repositories;

import com.example.spotifyscrobble.listening.entities.ListenEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListenEventEntity, UUID>, JpaSpecificationExecutor<ListenEventEntity> {
    Page<ListenEventEntity> findAllByUsername(String username, Pageable p);
}
