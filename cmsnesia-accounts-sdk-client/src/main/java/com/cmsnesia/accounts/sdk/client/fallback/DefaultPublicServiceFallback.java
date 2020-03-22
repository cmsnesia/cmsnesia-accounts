package com.cmsnesia.accounts.sdk.client.fallback;

import com.cmsnesia.accounts.sdk.client.PublicService;
import feign.hystrix.FallbackFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class DefaultPublicServiceFallback implements FallbackFactory<PublicService> {

  @Override
  public PublicService create(Throwable throwable) {
    return new PublicService() {
      @Override
      public Flux<List<Map<String, Object>>> services() {
        return Flux.empty();
      }

      @Override
      public Mono<String> whoami() {
        return Mono.empty();
      }
    };
  }
}
