package com.schoolchan.board;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PosterTierRepository extends JpaRepository<PosterTier, UUID> {

    Optional<PosterTier> findBySessionHash(String sessionHash);

    List<PosterTier> findAllByOrderByScoreDesc();
}
