package com.cmsnesia.accounts.web;

import ch.sbb.esta.openshift.gracefullshutdown.GracefulshutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.cmsnesia.accounts")
@EntityScan("com.cmsnesia.accounts")
@EnableReactiveMongoRepositories("com.cmsnesia.accounts")
public class WebApplication {

  public static void main(String[] args) {
    GracefulshutdownSpringApplication.run(WebApplication.class, args);
  }
}
