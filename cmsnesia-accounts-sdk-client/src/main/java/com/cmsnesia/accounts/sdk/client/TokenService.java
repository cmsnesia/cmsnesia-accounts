package com.cmsnesia.accounts.sdk.client;

import com.cmsnesia.accounts.model.request.RefreshTokenRequest;
import com.cmsnesia.accounts.model.request.TokenRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import feign.hystrix.FallbackFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Mono;

@FeignClient(name = "cmsnesia-account", fallback = TokenService.DefaultFallback.class)
public interface TokenService {

  @PostMapping(
      value = "/token/request",
      headers = {"Content-Type: application/json"})
  Mono<TokenResponse> request(TokenRequest request);

  @PutMapping(
      value = "/token/refresh",
      headers = {"Content-Type: application/json"})
  Mono<TokenResponse> refresh(RefreshTokenRequest request);

  @DeleteMapping(
      value = "/token/destroy",
      headers = {"Content-Type: application/json"})
  Mono<TokenResponse> destroy(TokenResponse request);

  @RequiredArgsConstructor
  class DefaultFallback<T extends TokenService> implements FallbackFactory<TokenService> {

    private final T costant;

    @Override
    public TokenService create(Throwable throwable) {
      return costant;
    }
  }
}
