package dev.mike.chao.simple.greeter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

  @Autowired
  @Qualifier("stringGreeting")
  Greeting stringGreeting;

  @Autowired
  @Qualifier("envGreeting")
  Greeting envGreeting;

  int count = 0;

  @GetMapping("/")
  String getGreeting() {
    String greeting = (count % 2 == 0) ? stringGreeting.getGreeting() : envGreeting.getGreeting();
    count++;
    return greeting;
  }
}
