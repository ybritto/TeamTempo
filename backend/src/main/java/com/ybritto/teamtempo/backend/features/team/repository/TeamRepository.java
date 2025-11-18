package com.ybritto.teamtempo.backend.features.team.repository;

import com.ybritto.teamtempo.backend.authentication.entity.UserEntity;
import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByUser(UserEntity user);

    Optional<TeamEntity> findByUuid(UUID uuid);
}
