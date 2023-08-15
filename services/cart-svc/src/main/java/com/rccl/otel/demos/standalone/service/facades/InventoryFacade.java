package com.rccl.otel.demos.standalone.service.facades;


import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import com.rccl.otel.demos.standalone.common.ResponseBody;
import com.rccl.otel.demos.standalone.config.EnvironmentConfig;
import com.rccl.otel.demos.standalone.exceptions.InventoryLevelNotFound;
import com.rccl.otel.demos.standalone.models.Cart;
import com.rccl.otel.demos.standalone.models.request.CartRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryFacade {

  private static final ParameterizedTypeReference<ResponseBody<Cart>> RESPONSE_TYPE =
      new ParameterizedTypeReference<ResponseBody<Cart>>() {
      };

  private final EnvironmentConfig config;
  private final WebClient webClient;

  public Mono<Cart> createCartWithInventory(CartRequest cartRequest) {
    var base = config.inventoryBaseUrl;
    var uri = base + "/cartset";



    return webClient
        .post()
        .uri(uri)
        .body(fromValue(cartRequest))
        .exchangeToMono(clientResponse -> {
          var statusCode = clientResponse.statusCode();
          if (statusCode.is2xxSuccessful()) {
            return clientResponse
                .bodyToMono(RESPONSE_TYPE)
                .map(cartResponseBody -> {
                  return cartResponseBody.getPayload();

                });
          }

          log.error("Error fetching inventory level. XXX todo");
          return Mono.error(new InventoryLevelNotFound(cartRequest.cartId));
        });

  }

}
