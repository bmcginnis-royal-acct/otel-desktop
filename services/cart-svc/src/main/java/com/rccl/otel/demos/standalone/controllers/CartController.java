package com.rccl.otel.demos.standalone.controllers;






import com.rccl.otel.demos.standalone.common.CommonHeaders;
import com.rccl.otel.demos.standalone.common.ResponseBody;
import com.rccl.otel.demos.standalone.common.Retry;
import com.rccl.otel.demos.standalone.config.EnvironmentConfig;
import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.service.CartService;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor

@RequestMapping("/api")
public class CartController {

  private final EnvironmentConfig config;

  private final CartService cartService;

  private static final Logger logger = LoggerFactory.getLogger(CartController.class);


  @GetMapping("/cart/{id}")
  public Mono<ResponseEntity<ResponseBody<Cart>>> getCart(
      @PathVariable String id, @RequestHeader HttpHeaders httpHeaders) {

    var retries = httpHeaders.get(CommonHeaders.HDR_RETRY);
    if ((retries != null) && !retries.isEmpty()) {
      log.info("Processing retry request attempt number: {}.", retries.get(0));
    }

    Mono<Cart> result = cartService.findById(id);
    return result.map(cart -> {
      var responseBody = ResponseBody.<Cart>builder()
          .payload(cart)
          .retry(new Retry())
          .status(HttpStatus.OK.value())
          .build();
      return ResponseEntity.ok(responseBody);
    }).defaultIfEmpty(ResponseEntity.notFound().build());

  }

  @WithSpan("CartController.getAllCarts")
  @GetMapping("/cart")
  public Flux<Cart> getAllCarts() {
    return cartService.findAll();
  }

//  @WithSpan("getCartsByCategory")
//  private Collection<Cart> getCartsByCategory(@SpanAttribute("app.carts.category") String category) {
//    try {
//
//      Flux<Cart> carts = cartService.findAll(category);
//       Span.current().setAttribute("app.carts.count", carts.size());
//       return carts;
//    } catch (Exception e) {
//        // Create error event and add it to trace.
//        span.addEvent(
//            "Error", Attributes.of(AttributeKey.stringKey("exception.message"), e.getMessage()));
//
//        // Set trace status to Error.
//        span.setStatus(StatusCode.ERROR);
//
//        // log error here... etc.
//        return null;
//    }
//
//    Span.current().setAttribute("app.carts.count", carts.size());
//    return ads;
//  }
}
