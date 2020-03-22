package com.cmsnesia.accounts.web.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "public")
@Api(
    value = "Public API",
    tags = {"Public"})
@Slf4j
@RequiredArgsConstructor
public class PublicController {

  private final DiscoveryClient discoveryClient;

  @GetMapping("/services")
  public Flux<List<Map<String, Object>>> services() {
    return Flux.fromStream(
        discoveryClient.getServices().stream()
            .map(
                serviceId -> {
                  Map<String, Object> service = new HashMap<>();
                  List<Map<String, Object>> instances =
                      discoveryClient.getInstances(serviceId).stream()
                          .map(
                              serviceInstance -> {
                                Map<String, Object> instance = new HashMap<>();
                                instance.put("host", serviceInstance.getHost());
                                instance.put("instanceId", serviceInstance.getInstanceId());
                                instance.put("metaData", serviceInstance.getMetadata());
                                instance.put("scheme", serviceInstance.getScheme());
                                return instance;
                              })
                          .collect(Collectors.toList());
                  service.put("serviceId", serviceId);
                  service.put("instances", instances);
                  return instances;
                }));
  }

  @GetMapping("/whoami")
  public Mono<String> whoami() {
    try {
      return Mono.just(InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      return Mono.just("Unknown");
    }
  }
}
