package com.cmsnesia.accounts.sdk.client.fallback;

import com.cmsnesia.accounts.model.request.RefreshTokenRequest;
import com.cmsnesia.accounts.model.request.TokenRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import com.cmsnesia.accounts.sdk.client.TokenService;
import feign.hystrix.FallbackFactory;
import reactor.core.publisher.Mono;

public class DefaultTokenServiceFallback implements FallbackFactory<TokenService> {

  @Override
  public TokenService create(Throwable throwable) {
    return new TokenService() {
      @Override
      public Mono<TokenResponse> request(TokenRequest request) {
        return Mono.empty();
      }

      @Override
      public Mono<TokenResponse> refresh(RefreshTokenRequest request) {
        return Mono.empty();
      }

      @Override
      public Mono<TokenResponse> destroy(TokenResponse request) {
        return Mono.empty();
      }
    };
  }
}
