package dev.mike.chao.simple.greeter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class GreetingController {

  @Autowired
  @Qualifier("stringGreeting")
  Greeting stringGreeting;

  @Autowired
  @Qualifier("envGreeting")
  Greeting envGreeting;

  private final Counter greetingCounter;

  public GreetingController(@Autowired MeterRegistry meterRegistry) {
    greetingCounter = Counter.builder("greetings.count")
        .description("Number of greetings given")
        .register(meterRegistry);
  }

  @GetMapping("/")
  String getGreeting() {
    int count = (int) greetingCounter.count();
    String greeting = (count % 2 == 0) ? stringGreeting.getGreeting() : envGreeting.getGreeting();
    greetingCounter.increment();
    return greeting;
  }
}
