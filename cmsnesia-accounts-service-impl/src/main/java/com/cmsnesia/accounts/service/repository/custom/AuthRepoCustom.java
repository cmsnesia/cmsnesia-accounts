package com.cmsnesia.accounts.service.repository.custom;

import com.cmsnesia.accounts.domain.Auth;
import com.cmsnesia.accounts.model.AuthDto;
import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.request.IdRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthRepoCustom {

  Mono<Auth> find(Session session, IdRequest id);

  Flux<Auth> find(Session session, AuthDto dto, Pageable pageable);

  Mono<Long> countFind(Session session, AuthDto dto);

  Mono<Auth> findByAccessTokenAndType(Session session, String accessToken, String tokenType);

  Mono<Auth> findByRefreshTokenAndTokenType(Session session, String refreshToken, String tokenType);

  Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(Session session, TokenResponse token);

  Mono<Auth> changePassword(Session session, String newPassword);
}
