package com.cmsnesia.accounts.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session implements Serializable {

  private String id;

  private String username;

  private String password;

  private Set<String> roles;

  private String fullName;

  private Set<EmailDto> emails;

  private Set<ApplicationDto> applications;

  public static Set<String> applicationIds(Session session) {
    Set<String> appIds =
        session == null || session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(ApplicationDto::getId)
                .collect(Collectors.toSet());
    return appIds;
  }

  public static Set<ApplicationDto> applications(Session session) {
    Set<ApplicationDto> applications =
        session.getApplications() == null
            ? Collections.emptySet()
            : session.getApplications().stream()
                .map(
                    applicationDto ->
                        ApplicationDto.builder()
                            .id(applicationDto.getId())
                            .name(applicationDto.getName())
                            .build())
                .collect(Collectors.toSet());
    return applications;
  }
}
