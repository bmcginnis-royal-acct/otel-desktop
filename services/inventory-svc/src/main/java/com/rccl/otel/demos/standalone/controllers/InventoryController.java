package com.rccl.otel.demos.standalone.controllers;


import com.rccl.otel.demos.standalone.common.CommonHeaders;
import com.rccl.otel.demos.standalone.common.ResponseBody;
import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.models.request.CartRequest;
import com.rccl.otel.demos.standalone.service.InventoryService;
import com.rccl.otel.demos.standalone.service.model.InventoryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  @GetMapping("/{id}")
  public Mono<ResponseEntity<ResponseBody<InventoryLevel>>> getInventory(
      @PathVariable String id, @RequestHeader HttpHeaders httpHeaders) {

    var retries = httpHeaders.get(CommonHeaders.HDR_RETRY);
    if ((retries != null) && !retries.isEmpty()) {
      log.info("Processing retry request attempt number: {}.", retries.get(0));
    }

    Mono<InventoryLevel> result = inventoryService.inventoryByItemId(id);
    return result.map(inventoryCount -> {
      var responseBody = ResponseBody.<InventoryLevel>builder()
          .payload(inventoryCount)
          .status(HttpStatus.OK.value())
          .build();
      return ResponseEntity.ok(responseBody);
    }).defaultIfEmpty(ResponseEntity.notFound().build());

  }

  @PostMapping("/cartset")
  public Mono<ResponseEntity<ResponseBody<Cart>>> setCartInventory(
      @RequestBody CartRequest request)
  {
    Mono<Cart> updatedCart = inventoryService.createCartWithStockCheck(request);
    return updatedCart.map(cart -> {
          var responseBody = ResponseBody.<Cart>builder()
              .payload(cart)
              .status(HttpStatus.OK.value())
              .build();
          return ResponseEntity.ok(responseBody);
        }).defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
