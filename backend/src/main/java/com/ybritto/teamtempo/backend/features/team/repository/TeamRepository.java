package com.ybritto.teamtempo.backend.features.team.repository;

import com.ybritto.teamtempo.backend.features.team.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
