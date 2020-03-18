package com.cmsnesia.accounts.web;

import ch.sbb.esta.openshift.gracefullshutdown.GracefulshutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.cmsnesia.accounts")
@EntityScan("com.cmsnesia.accounts")
@EnableReactiveMongoRepositories("com.cmsnesia.accounts")
@EnableDiscoveryClient
@EnableScheduling
public class WebApplication {

  public static void main(String[] args) {
    GracefulshutdownSpringApplication.run(WebApplication.class, args);
  }
}
