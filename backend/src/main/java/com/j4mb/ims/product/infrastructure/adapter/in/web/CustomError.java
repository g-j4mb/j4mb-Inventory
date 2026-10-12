package com.j4mb.ims.product.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.Map;

public record CustomError(Instant timestamp,
                          int status,
                          String error,
                          String message,
                          String path,
                          Map<String, String> fieldErrors
) {
}