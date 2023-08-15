package com.rccl.otel.demos.standalone.controllers;


import com.rccl.otel.demos.standalone.TutorialMicroservice;
import com.rccl.otel.demos.standalone.telemetry.TelemetryConsts;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rccl.otel.demos.standalone.models.Tutorial;
import com.rccl.otel.demos.standalone.service.TutorialService;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {
  private static final Logger log = LoggerFactory.getLogger(TutorialController.class);

  @Value("otel.traces.api.version")
  private String tracesApiVersion;

  @Value("otel.metrics.api.version")
  private String metricsApiVersion;

  private final Tracer tracer;
  private final Meter meter;

  @Autowired
  public TutorialController(OpenTelemetry openTelemetry) {
    tracer = openTelemetry.getTracer(TutorialMicroservice.class.getName());
    meter = openTelemetry.getMeter(TutorialMicroservice.class.getName());
  }

//  private final Tracer tracer =
//      GlobalOpenTelemetry.getTracer("io.opentelemetry.traces.hello",
//          tracesApiVersion);

//  private final Meter meter =
//      GlobalOpenTelemetry.meterBuilder("io.opentelemetry.metrics.hello")
//          .setInstrumentationVersion(metricsApiVersion)
//          .build();

  private LongCounter tutorialsCreated;

  @PostConstruct
  public void createMetrics() {

    tutorialsCreated =
        meter
            .counterBuilder(TelemetryConsts.NUMBER_OF_TUTORIALS_CREATED)
            .setDescription(TelemetryConsts.NUMBER_OF_TUTORIALS_CREATED_DESCRIPTION)
            .setUnit("int")
            .build();
  }


  @Autowired
  TutorialService tutorialService;


  @RequestMapping(method = RequestMethod.GET, value="/tutorials")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Tutorial> getAllTutorials(@RequestParam(required = false) String title) {

    log.info("getting all tutorials");

    System.out.println("\n\nXXX GET tutorials XXXX\n\n\n");
    var span = Span.current();
    span.setAttribute("tutorials.getAll.calls", 1);
    //span.setAttribute(ResourceAttributes.DEPLOYMENT_ENVIRONMENT, "dev");

    span.addEvent("AnEvent", Attributes.of(AttributeKey.stringKey("smart-intel"), "this is good!"));

    if (title == null)
      return tutorialService.findAll();
    else
      return tutorialService.findTitleContaining(title);
  }

  @PostMapping("/tutorials")
  public Mono<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {

    // Increment business metric tutorial creation counter.
    tutorialsCreated.add(1);

    log.info("Adding a tutorial.");

    Tutorial toSave = Tutorial.builder()
        .title(tutorial.getTitle())
        .description(tutorial.getDescription())
        .published(false)
        .build();
    return tutorialService.save(toSave);
  }

  @GetMapping("/tutorials/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Tutorial> getTutorialById(@PathVariable("id") int id) {
    return tutorialService.findById(id);
  }

  @PutMapping("/tutorials/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Tutorial> updateTutorial(@PathVariable("id") int id, @RequestBody Tutorial tutorial) {
    return tutorialService.update(id, tutorial);
  }

  @DeleteMapping("/tutorials/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteTutorial(@PathVariable("id") int id) {
    return tutorialService.deleteById(id);
  }

  @DeleteMapping("/tutorials")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteAllTutorials() {
    return tutorialService.deleteAll();
  }

  @GetMapping("/tutorials/published")
  @ResponseStatus(HttpStatus.OK)
  public Flux<Tutorial> findByPublished() {
    return tutorialService.findPublished(true);
  }


}

