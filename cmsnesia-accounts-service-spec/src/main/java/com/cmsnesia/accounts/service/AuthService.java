package com.cmsnesia.accounts.service;

import com.cmsnesia.accounts.model.AuthDto;
import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.api.Result;
import reactor.core.publisher.Mono;

public interface AuthService extends BaseService<AuthDto> {

  Mono<Result<AuthDto>> findByUsername(String username);

  Mono<Result<AuthDto>> changePassword(Session session, String newPassword);
}
