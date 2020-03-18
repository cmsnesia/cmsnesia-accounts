package com.cmsnesia.accounts.service;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.request.RefreshTokenRequest;
import com.cmsnesia.accounts.model.request.TokenRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import reactor.core.publisher.Mono;

public interface TokenService {

  Mono<Session> validate(TokenResponse tokenResponse);

  Mono<TokenResponse> request(TokenRequest tokenRequest);

  Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse);

  Mono<String> destroy(TokenResponse tokenResponse);
}
