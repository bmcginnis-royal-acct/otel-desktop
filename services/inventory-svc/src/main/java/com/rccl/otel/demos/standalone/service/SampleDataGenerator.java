package com.rccl.otel.demos.standalone.service;

import static java.util.Map.entry;


import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.models.CartItem;
import com.rccl.otel.demos.standalone.service.model.InventoryLevel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SampleDataGenerator {

  public static Cart simpleCart() {
    var items = new HashSet<CartItem>(Arrays.asList(
        new CartItem("coffee_mug", "Big coffee mug", 1, 4.00, 0),
        new CartItem("coffee_beans", "dark roast beans", 2, 7.25, 0)
    ));
    var cart = Cart.builder()
        .cartId("cart-simple")
        .items(items)
        .build();

    return cart;
  }

  public static Cart anotherCart() {
      var items = new HashSet<CartItem>(Arrays.asList(
          new CartItem("oatmeal", "Oatmeal", 1, 3.45, 0),
          new CartItem("cookie", "Cookie", 1, 2.25, 0)
      ));
      var cart = Cart.builder()
          .cartId("cart-another")
          .items(items)
          .build();

      return cart;
  }

  public static List<Cart> getCarts() {
    var carts = Arrays.asList(simpleCart(), anotherCart());
    return carts;
  }


  public static Map<String, InventoryLevel> pullInventory() {
      Map<String, InventoryLevel> inventory = Map.ofEntries(
        entry("cookie", new InventoryLevel("cookie", 12)),
        entry("oatmeal", new InventoryLevel("oatmeal", 2)),
        entry ("coffee_mug",new InventoryLevel("coffee_mug", 22)),
        entry ("coffee_beans",new InventoryLevel("coffee_beans", 2))
    );

     return inventory;
  }

}
