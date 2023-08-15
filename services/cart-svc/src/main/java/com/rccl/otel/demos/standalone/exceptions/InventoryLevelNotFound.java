package com.rccl.otel.demos.standalone.exceptions;


import com.rccl.otel.demos.standalone.common.MiddlewareException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryLevelNotFound extends MiddlewareException {
  String itemId = "";
  String errorCode = ErrorCodes.InventoryLevelNotFound.errCode;
  Integer httpStatus = HttpStatus.NOT_FOUND.value();
  String msg = String.format("Inventory not found error.");

  public InventoryLevelNotFound(String itemId) {
    super(String.format(ErrorCodes.InventoryLevelNotFound.errMsg, itemId ));
    this.itemId = itemId;
  }
}

