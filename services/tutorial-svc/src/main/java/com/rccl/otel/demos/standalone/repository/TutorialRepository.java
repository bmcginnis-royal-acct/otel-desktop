package com.rccl.otel.demos.standalone.repository;


import com.rccl.otel.demos.standalone.models.Tutorial;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TutorialRepository extends R2dbcRepository<Tutorial,Integer> {
  Flux<Tutorial> findByTitleContaining(String searchContains);
  Flux<Tutorial> findByPublished(boolean isPublished);
}

