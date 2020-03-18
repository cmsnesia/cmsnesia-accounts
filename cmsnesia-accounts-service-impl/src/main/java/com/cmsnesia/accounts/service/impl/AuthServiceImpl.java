package com.cmsnesia.accounts.service.impl;

import com.cmsnesia.accounts.assembler.AuthAssembler;
import com.cmsnesia.accounts.domain.Auth;
import com.cmsnesia.accounts.model.AuthDto;
import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.api.Result;
import com.cmsnesia.accounts.model.api.StatusCode;
import com.cmsnesia.accounts.model.request.IdRequest;
import com.cmsnesia.accounts.service.AuthService;
import com.cmsnesia.accounts.service.repository.AuthRepo;
import com.cmsnesia.accounts.service.util.Sessions;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final AuthAssembler authAssembler;
  private final AuthRepo authRepo;

  @Override
  public Mono<Result<AuthDto>> add(Session authDto, AuthDto dto) {
    Auth auth = authAssembler.fromDto(dto);
    auth.setId(UUID.randomUUID().toString());
    auth.setCreatedBy(authDto.getId());
    auth.setCreatedAt(new Date());
    auth.setApplications(Sessions.applications(authDto));
    return authRepo
        .save(auth)
        .map(authAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
  }

  @Override
  public Mono<Result<AuthDto>> edit(Session authDto, AuthDto dto) {
    return authRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Auth, Mono<Result<AuthDto>>>)
                auth -> {
                  Auth save = authAssembler.fromDto(dto);
                  save.audit(auth);
                  save.setModifiedBy(authDto.getId());
                  save.setModifiedAt(new Date());
                  return authRepo
                      .save(save)
                      .map(saved -> authAssembler.fromEntity(saved))
                      .map(result -> Result.build(result, StatusCode.SAVE_SUCCESS));
                });
  }

  @Override
  public Mono<Result<AuthDto>> delete(Session authDto, AuthDto dto) {
    return authRepo
        .findById(dto.getId())
        .flatMap(
            (Function<Auth, Mono<Result<AuthDto>>>)
                auth -> {
                  auth.setDeletedBy(authDto.getId());
                  auth.setDeletedAt(new Date());
                  return authRepo
                      .save(auth)
                      .map(saved -> authAssembler.fromEntity(saved))
                      .map(result -> Result.build(result, StatusCode.DELETE_SUCCESS));
                });
  }

  @Override
  public Mono<Page<AuthDto>> find(Session session, AuthDto dto, Pageable pageable) {
    return authRepo
        .countFind(session, dto)
        .flatMap(
            count -> {
              Mono<List<AuthDto>> mono =
                  authRepo
                      .find(session, dto, pageable)
                      .map(auth -> authAssembler.fromEntity(auth))
                      .collectList();
              return mono.map(authDtos -> new PageImpl<>(authDtos, pageable, count));
            });
  }

  @Override
  public Mono<Result<AuthDto>> find(Session session, IdRequest idRequest) {
    return authRepo
        .find(session, idRequest)
        .map(authAssembler::fromEntity)
        .map(result -> Result.build(result, StatusCode.DATA_FOUND));
  }

  @Override
  public Mono<Result<AuthDto>> findByUsername(String username) {
    return authRepo
        .findByUsername(username)
        .map(
            auth -> {
              AuthDto authDto = authAssembler.fromEntity(auth);
              authDto.setPassword(auth.getPassword());
              return Result.build(authDto, StatusCode.DATA_FOUND);
            });
  }

  @Override
  public Mono<Result<AuthDto>> changePassword(Session session, String newPassword) {
    return authRepo
        .changePassword(session, newPassword)
        .map(auth -> Result.build(authAssembler.fromEntity(auth), StatusCode.SAVE_SUCCESS));
  }
}
