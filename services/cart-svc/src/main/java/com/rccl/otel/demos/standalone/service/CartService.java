package com.rccl.otel.demos.standalone.service;


import com.rccl.otel.demos.standalone.exceptions.InventoryLevelNotFound;
import com.rccl.otel.demos.standalone.exceptions.UpstreamServiceError;
import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.models.request.CartRequest;
import com.rccl.otel.demos.standalone.service.facades.InventoryFacade;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

  public final InventoryFacade inventoryFacade;


  // No child span for this method
  private void wastingTime() {
    for (int i =0; i < 100; i++) {
      var x = i + 10;
    }
  }

  // Create a child span for this method using annotation
  @WithSpan
  private void timeConsumingComputations() {
    for (int i =0; i < 10; i++) {
      wastingTime();
    }
  }

  @WithSpan
  public Mono<Cart> findById(String id) {
    if ("666".equals(id)) {
      var span = Span.current();
      span.setStatus(StatusCode.ERROR, "Use the devil's cart is not allowed.");
      var exception = new RuntimeException("Exception due to using the devil's cart.");
      span.recordException(exception);
      return Mono.empty();
    }

    // call computations twice, just for demo purposes.
    timeConsumingComputations();
    timeConsumingComputations();

    // Add attrobites to the current span
    Span span = Span.current();
    span.setAttribute("sevicezero.finder", "find by cartId");
    span.setAttribute("sevicezero.extraInfo", 303);


    var cart = SampleDataGenerator.simpleCart();
    cart.cartId = id;

    var requestBody = CartRequest.fromCart(cart);
    Mono<Cart> updatedCart = inventoryFacade.createCartWithInventory(requestBody);
    return updatedCart;

  }

  public Flux<Cart> findAll() {
    var allCarts = SampleDataGenerator.getCarts();
    return Flux.fromIterable(allCarts);
  }

  private void handleError(Throwable err, String id) {
    String message = err.getMessage();
    String responseBody = "";

    if (err instanceof InventoryLevelNotFound) {
      log.info("InventoryLevelNotFound found in handleError().");
      return;
    }

    throw new UpstreamServiceError();
  }
}
