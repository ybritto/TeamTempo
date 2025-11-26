package com.ybritto.teamtempo.backend.features.project.repository;

import com.ybritto.teamtempo.backend.features.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

}
