package com.rccl.otel.demos.standalone.service;


import com.rccl.otel.demos.standalone.models.Tutorial;
import com.rccl.otel.demos.standalone.repository.TutorialRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Purpose: A data service for Tutorials.
@Service
public class TutorialService {

  @Autowired
  TutorialRepository tutorialRepository;

  public Flux<Tutorial> findAll() {
    return tutorialRepository.findAll();
  }



  public Flux<Tutorial> findTitleContaining(String searchtitle) {
    return tutorialRepository.findByTitleContaining(searchtitle);
  }

  public Mono<Tutorial> findById(int id) {
    return tutorialRepository.findById(id);
  }

  public Mono<Tutorial> save(Tutorial tutorial) {
    return tutorialRepository.save(tutorial);
  }

  public Mono<Tutorial> update(int id, Tutorial tutorial) {
    return tutorialRepository.findById(id)
        .map(Optional::of).defaultIfEmpty(Optional.empty())
        .flatMap(optionalTutorial -> {
          if (optionalTutorial.isPresent()) {
            tutorial.setId(id);
            return tutorialRepository.save(tutorial);
          }

          return Mono.empty();
        });
  }

  public Mono<Void> deleteById(int id) {
    return tutorialRepository.deleteById(id);
  }

  public Mono<Void> deleteAll() {
    return tutorialRepository.deleteAll();
  }

  public Flux<Tutorial> findPublished(boolean isPublished) {
    return tutorialRepository.findByPublished(isPublished);
  }
}

