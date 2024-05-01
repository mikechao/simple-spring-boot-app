package dev.mike.chao.simple.greeter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringGreeting implements Greeting {
  private final String greeting;

  @Override
  public String getGreeting() {
    return greeting;
  }

}
