package com.rccl.otel.demos.standalone.exceptions;


public enum ErrorCodes {
  InventoryLevelNotFound("DBS-0210", "Inventory level not found for itemId: '%s'"),
  UpstreamError("DBS-0200", "Upstream service: '%s' error.")
  ;

  public final String errCode;

  /** Internal error message in String.format(). */
  public final String errMsg;

  private ErrorCodes(String errCode, String internalErrMsg) {
    this.errCode = errCode;
    this.errMsg = internalErrMsg;
  }

}
