package com.cmsnesia.accounts.assembler;

import com.cmsnesia.accounts.model.AuthDto;
import com.cmsnesia.accounts.model.Session;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
public class SessionAssembler extends Assembler<AuthDto, Session> {

  @Nonnull
  @Override
  public AuthDto fromDto(@Nonnull Session dto) {
    return AuthDto.builder()
        .id(dto.getId())
        .fullName(dto.getFullName())
        .username(dto.getUsername())
        .password(dto.getPassword())
        .emails(dto.getEmails() == null ? Collections.emptySet() : dto.getEmails())
        .roles(dto.getRoles() == null ? Collections.emptySet() : dto.getRoles())
        .applications(
            dto.getApplications() == null ? Collections.emptyNavigableSet() : dto.getApplications())
        .build();
  }

  @Nonnull
  @Override
  public Collection<AuthDto> fromDto(@Nonnull Collection<Session> dtos) {
    return dtos == null
        ? Collections.emptyList()
        : dtos.stream().map(this::fromDto).collect(Collectors.toSet());
  }

  @Nonnull
  @Override
  public Session fromEntity(@Nonnull AuthDto entity) {
    return Session.builder()
        .id(entity.getId())
        .fullName(entity.getFullName())
        .username(entity.getUsername())
        .password(entity.getPassword())
        .emails(entity.getEmails() == null ? Collections.emptySet() : entity.getEmails())
        .roles(entity.getRoles() == null ? Collections.emptySet() : entity.getRoles())
        .applications(
            entity.getApplications() == null
                ? Collections.emptyNavigableSet()
                : entity.getApplications())
        .build();
  }

  @Nonnull
  @Override
  public Collection<Session> fromEntity(@Nonnull Collection<AuthDto> entities) {
    return entities == null
        ? Collections.emptyList()
        : entities.stream().map(this::fromEntity).collect(Collectors.toSet());
  }
}
