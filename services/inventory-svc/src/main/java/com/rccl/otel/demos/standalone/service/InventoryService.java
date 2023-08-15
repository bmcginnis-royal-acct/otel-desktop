package com.rccl.otel.demos.standalone.service;


import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.models.request.CartRequest;
import com.rccl.otel.demos.standalone.service.model.InventoryLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

  public Mono<InventoryLevel> inventoryByItemId(String id) {
    var inventory = SampleDataGenerator.pullInventory();
    var inventoryCount = inventory.get(id);
    return Mono.justOrEmpty(inventoryCount);
  }

  /**
   *
   * @param cart
   * @return a Mono<Cart></Cart> with inStock fields set based on current inventory levels.
   */
  public Mono<Cart> createCartWithStockCheck(CartRequest cart) {
    var inventory = SampleDataGenerator.pullInventory();

    cart.getItems().forEach(cartItem -> {
      var inventoryLevel = inventory.get(cartItem.itemId);
      cartItem.setStockLevel(inventoryLevel.getCount());
    });

    // return mutated cart.
    return Mono.just(new Cart(cart.cartId, cart.items));
  }





}
