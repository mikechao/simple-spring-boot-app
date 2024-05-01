package dev.mike.chao.simple.greeter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

  @Bean(name = "stringGreeting")
  Greeting stringGreeting() {
    return new StringGreeting("Greetings from StringGreeting bean");
  }

  @Bean(name = "envGreeting")
  Greeting envGreeting() {
    return new EnvGreeting();
  }
}
