package com.example.spotifyscrobble.listening;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningHistoryRepository extends JpaRepository<ListenEventEntity, Long> {
    Page<ListenEventEntity> findAllByUser_UserId(Long userId, Pageable p);
}
