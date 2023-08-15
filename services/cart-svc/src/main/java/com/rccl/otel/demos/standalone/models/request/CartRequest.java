package com.rccl.otel.demos.standalone.models.request;

import com.rccl.otel.demos.standalone.models.CartItem;
import com.rccl.otel.demos.standalone.models.Cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartRequest {

  public  String cartId;

  public Set<CartItem> items;

  public static CartRequest fromCart(Cart cart) {
     return new CartRequest(cart.cartId, cart.items);
  }

}
