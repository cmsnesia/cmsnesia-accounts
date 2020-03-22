package com.cmsnesia.accounts.sdk.client;

import feign.RequestLine;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface PublicService {

  @RequestLine("GET /public/services")
  Flux<Map<String, Object>> services();

  @RequestLine(("GET /public/whoami"))
  Mono<String> whoami();
}
