package com.cmsnesia.accounts.web.controller;

import com.cmsnesia.accounts.model.Session;
import com.cmsnesia.accounts.model.request.RefreshTokenRequest;
import com.cmsnesia.accounts.model.request.TokenRequest;
import com.cmsnesia.accounts.model.response.TokenResponse;
import com.cmsnesia.accounts.service.AuthService;
import com.cmsnesia.accounts.service.TokenService;
import com.cmsnesia.accounts.web.util.ConstantKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Base64;

@RestController
@RequestMapping(value = "token")
@Api(
    value = "Token API",
    tags = {"Token"})
@Slf4j
@RequiredArgsConstructor
public class TokenController {

  private final AuthService authService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final ObjectMapper objectMapper;

  @PostMapping("/request")
  public Mono<ResponseEntity<?>> request(@RequestBody TokenRequest request) {
    return authService
        .findByUsername(request.getUsername())
        .map(
            (userDetails) -> {
              if (passwordEncoder.matches(
                  request.getPasssword(), userDetails.getData().getPassword())) {
                return ResponseEntity.ok(tokenService.request(request));
              } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
              }
            })
        .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

  @PutMapping("/refresh")
  public Mono<ResponseEntity<?>> refresh(@RequestBody RefreshTokenRequest request) {
    return Mono.just(ResponseEntity.ok(tokenService.refresh(request)));
  }

  @PutMapping("/destroy")
  public Mono<ResponseEntity<?>> destroy(@RequestBody TokenResponse tokenResponse) {
    return Mono.just(ResponseEntity.ok(tokenService.destroy(tokenResponse)));
  }

  @PostMapping("/validate")
  @ApiImplicitParams({
    @ApiImplicitParam(name = ConstantKeys.AUTHORIZATION, paramType = "header", dataType = "string")
  })
  public Mono<ResponseEntity<?>> validate(@ApiIgnore ServerHttpRequest serverRequest) {
    String path = serverRequest.getPath().toString();
    String forwaredPath = serverRequest.getHeaders().getFirst("X-Original-URI");

    if (StringUtils.hasText(forwaredPath)) {
      if (Arrays.asList(forwaredPath.split("/")).stream().anyMatch(s -> s.equalsIgnoreCase("public"))) {
        return Mono.just(ResponseEntity.ok().build());
      }
    }

    if (path.startsWith("/token/")
            && path.startsWith("/public/")
            && !path.equals("/token/validate")) {
      return Mono.just(ResponseEntity.ok().build());
    }

    String token = serverRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(token) && token.length() > 7) {
      TokenResponse tokenResponse = new TokenResponse();
      tokenResponse.setAccessToken(token.substring(7));
      return tokenService
          .validate(tokenResponse)
          .defaultIfEmpty(new Session())
          .map(
              authDto -> {
                if (StringUtils.hasText(authDto.getId())
                    && !Collections.isEmpty(authDto.getApplications())) {
                  try {
                    String json = objectMapper.writeValueAsString(authDto);
                    return ResponseEntity.ok()
                        .header("X-User-Data", Base64.getEncoder().encodeToString(json.getBytes()))
                        .build();
                  } catch (JsonProcessingException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                  }
                } else {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
              });
    }
    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }
}
