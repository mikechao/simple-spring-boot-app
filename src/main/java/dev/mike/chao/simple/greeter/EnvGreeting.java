package dev.mike.chao.simple.greeter;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class EnvGreeting implements Greeting {

  @Value("${greeting.from.env}")
  private String fromPropertyString;

  private String envGreeting;

  @PostConstruct
  public void init() {
    log.info("Initializing EnvGreeting");
    envGreeting = fromPropertyString;
    log.info("Finished initializing EnvGreeting");
  }

  @Override
  public String getGreeting() {
    return envGreeting;
  }

}
