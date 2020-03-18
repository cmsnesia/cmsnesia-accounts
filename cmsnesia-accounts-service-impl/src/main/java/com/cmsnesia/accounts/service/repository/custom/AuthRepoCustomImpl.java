package com.cmsnesia.accounts.service.repository.custom;

import com.cmsnesia.accounts.domain.Auth;
import com.cmsnesia.accounts.model.AuthDto;
import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.request.IdRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import com.cmsnesia.accounts.service.util.Sessions;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthRepoCustomImpl implements AuthRepoCustom {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<Auth> find(Session session, IdRequest id) {
    Query query = buildQuery(session, AuthDto.builder().id(id.getId()).build(), null, null, null);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Flux<Auth> find(Session session, AuthDto dto, Pageable pageable) {
    Query query = buildQuery(session, dto, null, null, null);
    if (pageable.isPaged()) {
      query.with(pageable);
    }
    return reactiveMongoTemplate.find(query, Auth.class);
  }

  @Override
  public Mono<Long> countFind(Session session, AuthDto dto) {
    Query query = buildQuery(session, dto, null, null, null);
    return reactiveMongoTemplate.count(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByAccessTokenAndType(
      Session session, String accessToken, String tokenType) {
    Query query = buildQuery(session, AuthDto.builder().build(), accessToken, null, tokenType);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByRefreshTokenAndTokenType(
      Session session, String refreshToken, String tokenType) {
    Query query = buildQuery(session, AuthDto.builder().build(), null, refreshToken, tokenType);
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Mono<Auth> findByAccessTokenAndRefreshTokenAndTokenType(
      Session session, TokenResponse token) {
    Query query =
        buildQuery(
            session,
            AuthDto.builder().build(),
            token.getAccessToken(),
            token.getRefreshToken(),
            token.getTokenType());
    return reactiveMongoTemplate.findOne(query, Auth.class);
  }

  @Override
  public Mono<Auth> changePassword(Session session, String newPassword) {
    Query query = new Query();
    query.addCriteria(Criteria.where("id").is(session.getId()));
    Update update = new Update();
    update.set("password", newPassword);
    return reactiveMongoTemplate.findAndModify(query, update, Auth.class);
  }

  private Query buildQuery(
      Session session, AuthDto dto, String accessToken, String refreshToken, String tokenType) {
    Query query = new Query();

    query.with(Sort.by(Sort.Order.desc("createdAt")));

    query.addCriteria(Criteria.where("applications.id").in(Sessions.applicationIds(session)));

    query.addCriteria(Criteria.where("deletedAt").exists(false));

    if (!StringUtils.isEmpty(dto.getId())) {
      query.addCriteria(Criteria.where("id").is(dto.getId()));
      if (!StringUtils.isEmpty(dto.getUsername())) {
        query.addCriteria(Criteria.where("username").is(dto.getUsername()));
      }
    } else if (!StringUtils.isEmpty(dto.getUsername())) {
      query.addCriteria(Criteria.where("username").is(dto.getUsername()));
    } else {
      if (!StringUtils.isEmpty(dto.getFullName())) {
        Pattern regex = Pattern.compile(dto.getFullName(), Pattern.CASE_INSENSITIVE);
        query.addCriteria(Criteria.where("fullName").regex(regex));
      }
      if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
        query.addCriteria(Criteria.where("roles").in(dto.getRoles()));
      }
      if (dto.getEmails() != null && !dto.getEmails().isEmpty()) {
        query.addCriteria(Criteria.where("emails").in(dto.getEmails()));
      }
    }
    if (!StringUtils.isEmpty(accessToken)
        && !StringUtils.isEmpty(refreshToken)
        && !StringUtils.isEmpty(tokenType)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.accessToken").is(accessToken),
                  Criteria.where("tokens.refreshToken").is(refreshToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    } else if (!StringUtils.isEmpty(accessToken)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.accessToken").is(accessToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    } else if (!StringUtils.isEmpty(refreshToken)) {
      query.addCriteria(
          new Criteria()
              .andOperator(
                  Criteria.where("tokens.refreshToken").is(refreshToken),
                  Criteria.where("tokens.tokenType").is(tokenType)));
    }

    return query;
  }
}
