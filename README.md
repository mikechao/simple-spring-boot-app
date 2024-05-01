[<img src="./assets/flyiologo.svg" width="100" height="100">](https://fly.io/)[<img src="./assets/springbootlogo.svg" width="100" height="100">](https://spring.io/projects/spring-boot)

This is a simple Spring Boot App for the purposes of demonstrating how to deploy it to fly.io. 

Fly.io is a cloud platform and application deployment service that specializes in running applications close to users via an edge network.

## Prerequisites

Make sure you have the the tools/JDKs listed below installed and have signed up for fly.io account.

- Gradle
- Java 21 JDK
- VSCode or other IDE
- flyctl (fly.io CLI tool)
- Docker (needed for later)

The easiest way to install and manage these tools/JDKs is to use [SDKMAN!](https://sdkman.io/).

On a Windows 10 machine SDKMAN suggests installing [WSL](https://learn.microsoft.com/en-us/windows/wsl/install) aka Windows Subsystem for Linux. There is a nice guide on medium [here](https://medium.com/@pravinpreneur/how-to-modernize-java-development-environment-using-vs-code-and-wsl2-to-improve-productivity-1c9681390170) about how to setup a dev environment with WSL.

### Installing flyctl 

Execute the command below in a WSL terminal
```
curl -L https://fly.io/install.sh | sh
```
[Installation Docs](https://fly.io/docs/hands-on/install-flyctl/)

## Coding the Spring Boot application

Head over to [Spring Initializr](https://start.spring.io/) website and select the dependencies you need. For this simple greeting application I selected only Spring Web and Lombok.

<img src="./assets/springStarter.jpeg" width="50%">

I implemented a simple rest controller, [GreetingController.java](./src/main/java/dev/mike/chao/simple/greeter/GreetingController.java) that just returns a `Greeting` object on the "/" endpoint based on the number of times it was called.

The `Greeting` beans are defined in [SpringConfig.java](./src/main/java/dev/mike/chao/simple/greeter/SpringConfig.java) and then they are `@Autowired` into [GreetingController.java](./src/main/java/dev/mike/chao/simple/greeter/GreetingController.java) using `@Qualifier' to distinguish between the two different implementation of the interface [Greeting.java](./src/main/java/dev/mike/chao/simple/greeter/Greeting.java)

Run the app from [Application.java](./src/main/java/dev/mike/chao/simple/greeter/Application.java)
The output should be like this

<img src="./assets/appOutput1.png">

Hit refresh

<img src="./assets/appOutput2.png">

## Deploying to fly.io