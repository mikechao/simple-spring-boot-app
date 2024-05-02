[<img src="./assets/flyiologo.svg" width="100" height="100">](https://fly.io/)[<img src="./assets/springbootlogo.svg" width="100" height="100">](https://spring.io/projects/spring-boot)

This is a simple Spring Boot App for the purposes of demonstrating how to deploy it to fly.io. 

Fly.io is a cloud platform and application deployment service that specializes in running applications close to users via an edge network.

- [Prerequisites](#prerequisites)
  - [Windows Subsystem for Linux WSL](#windows-subsystem-for-linux-wsl)
  - [Installing flyctl](#installing-flyctl)
- [Coding the Spring Boot application](#coding-the-spring-boot-application)
- [Deploying to fly.io](#deploying-to-flyio)
  - [Create the Dockefile](#create-the-dockefile)
  - [Clean up the first deployment](#clean-up-the-first-deployment)
  - [Create a Volume](#create-a-volume)
  - [Bind our Spring Boot App to 0.0.0.0](#bind-our-spring-boot-app-to-0000)
  - [Redeploy the app with changes](#redeploy-the-app-with-changes)
  - [Success!](#success)
- [More tweaking](#more-tweaking)
  - [Setting Environment Variables](#setting-environment-variables)
  - [Lower the memory we are using](#lower-the-memory-we-are-using)

## Prerequisites

Make sure you have the the tools/JDKs listed below installed and have signed up for fly.io account.

- Gradle
- Java 21 JDK
- VSCode or other IDE
- flyctl (fly.io CLI tool)
- Docker

The easiest way to install and manage these tools/JDKs is to use [SDKMAN!](https://sdkman.io/).

### Windows Subsystem for Linux WSL

On a Windows 10 machine SDKMAN suggests installing [WSL](https://learn.microsoft.com/en-us/windows/wsl/install) aka Windows Subsystem for Linux. 

There is a nice guide on medium [here](https://medium.com/@pravinpreneur/how-to-modernize-java-development-environment-using-vs-code-and-wsl2-to-improve-productivity-1c9681390170) about how to setup a dev environment with WSL.

An good additional package to install for WSL is [wslu](https://www.wslutiliti.es/wslu/install.html). It is a collection of utilities designed for WSL, including wslview. wslview allows you to open the default browser on Windows from the Ubuntu command line. Sometimes some cli tools will want to open a browser window for authorization purposes. 

In `.bashrc` add the line below
```
export BROWSER=wslview
```


### Installing flyctl 

Execute the command below in a WSL terminal
```
curl -L https://fly.io/install.sh | sh
```
[Installation Docs](https://fly.io/docs/hands-on/install-flyctl/)

## Coding the Spring Boot application

Head over to [Spring Initializr](https://start.spring.io/) website and select the dependencies you need. For this simple greeting application I selected only Spring Web and Lombok.

<img src="./assets/springStarter.jpeg" width="50%">

I implemented a simple rest controller, [GreetingController.java](./src/main/java/dev/mike/chao/simple/greeter/GreetingController.java) that just returns a different `Greeting` object on the "/" endpoint based on the number of times it was called.

The `Greeting` beans are defined in [SpringConfig.java](./src/main/java/dev/mike/chao/simple/greeter/SpringConfig.java) and then they are `@Autowired` into [GreetingController.java](./src/main/java/dev/mike/chao/simple/greeter/GreetingController.java) using `@Qualifier' to distinguish between the two different implementation of the interface [Greeting.java](./src/main/java/dev/mike/chao/simple/greeter/Greeting.java)

Run the app from [Application.java](./src/main/java/dev/mike/chao/simple/greeter/Application.java)
The output should be like this

<img src="./assets/appOutput1.png">

Hit refresh

<img src="./assets/appOutput2.png">

## Deploying to fly.io

fly.io does NOT directly support deploying a Spring Boot application. However it does support deploying via Dockerfile.

### Create the Dockefile

In the project root directory create a file name Dockfile and add the following.
```
## alpine linux with JRE 
FROM eclipse-temurin:21-jre-alpine 
 
## create a nonroot user and group 
RUN addgroup -S spring && adduser -S spring -G spring 
 
## copy the spring jar
## Gradle puts the built fat jar in build/libs
## maven puts it in target/ 
COPY build/libs/*.jar /opt/myApp.jar 
 
## set the nonroot user as the default user 
USER spring:spring 
 
## set the working directory 
WORKDIR /opt 
 
ENTRYPOINT ["java", "-jar", "myApp.jar"] 
 
## expose the port to the external world 
EXPOSE 8080 
```

Once the Dockerfile has been created. Make sure the executable jar file is built. Using Gradle the command is
```
./gradlew clean bootJar
```

We are almost ready to deploy to fly.io. We just need to generate `fly.toml` which is a configuration file used by fly.io.
Run the following command from the project root directory to generate it.
```
fly launch --no-deploy
```
A summary of the defaults chosen will be displayed on the command line allowing you to edit them.
<img src="./assets/flyLaunchNoDeploy.png">

Accept the defaults as we can change them later by editing the `fly.toml` file.

The next question it will ask is if you want to create a `.dockerignore` file from `.gitignore` file. Yes should be the answer as it allows you to specify a list of files and directories that Docker should ignore during the build process.
<br>⚠️⚠️⚠️<br>
However if you look in `.dockerignore` you will notice that `**/build` is listed as an entry. This will cause a problem when we try to build the image locally before deploying to fly.io. So remove this entry
<br>⚠️⚠️⚠️

With all that out of the way deploy to fly.io using the following command to build the Docker image locally and then push it to fly.io for it to be deployed
```
fly deploy --local-only
```
<img src="./assets/flyDeployOutput1.png">

⚠️⚠️⚠️⚠️⚠️⚠️<br>
Notice the lines highlighted by the first red box. This is the default setting in fly.io when there are no Volumes attached. See their documentation [here](https://fly.io/docs/reference/app-availability/#redundancy-by-default-on-first-deploy). Volumes offer persistent storage for our deploy app. 
<br>⚠️⚠️⚠️⚠️⚠️⚠️<br>

⚠️⚠️⚠️⚠️⚠️⚠️<br>
The second red box is telling us that we will not be able to reach our Spring Boot Application because it is not bound to the right server address. This can be fixed by adding server.address=0.0.0.0 in application.properties
<br>⚠️⚠️⚠️⚠️⚠️⚠️<br>

### Clean up the first deployment
That is not what we want as it takes up more resources than needed and exceeds the free resources including in every fly.io plan. 

List the fly machines created. From the project root directory
```
fly machine list
```
<img src="./assets/flyMachineListOut.png">
The key thing to note here is the ID column.

Stop the 2 machines created by repeating the command
```
fly machine stop $ID
```

Destroy the 2 machines 
```
mikechao@LAPTOP-SMDC0G4S:~/projects/simple-spring-boot-app$ fly machine destroy $ID
machine 4d89664a66e768 was found and is currently in stopped state, attempting to destroy...
4d89664a66e768 has been destroyed
```

List the app that are deployed
```
fly app list
```
Output is similar to this
```
mikechao@LAPTOP-SMDC0G4S:~/projects/simple-spring-boot-app$ fly app list
NAME                                            OWNER           STATUS          LATEST DEPLOY
simple-spring-boot-app                          personal        pending
simple-spring-boot-app-misty-butterfly-8967     personal        suspended
```

Destroy the app, from project root directory
```
fly app destroy simple-spring-boot-app
```
Destroy the redundancy copy
```
fly app destroy simple-spring-boot-app-misty-butterfly-8967
```

### Create a Volume

fly.io Volume documentation can be found [here](https://fly.io/docs/reference/volumes/)

In the `fly.toml` file add the following section to create a volume
```
[mounts]
  source="myapp_data"
  destination="/data"
```

### Bind our Spring Boot App to 0.0.0.0

In application.properties add the following line.
```
server.address=0.0.0.0
```

### Redeploy the app with changes

1. Build the executable bootJar to include changes in application.properties
```
./gradlew clean bootJar
```
2. Deploy to fly.io again
```
fly launch --local-only
```
We use the launch command here instead of deploy because we cleaned up the first deployment so fly.io doesn't know about it. This time I chose to customize the defaults and change the app name to simple-greeting-app.<br>

Launch output
<img src="./assets/flyLaunchNoDeploy-2.png">

The warning about the app not listening on the right address still appears, but go to https://simple-greeting-app.fly.dev/ and see the app run!

### Success!
We can see our greeting on the web!<br>
<img src="./assets/web1.png">
<br>
<img src="./assets/web2.png">

## More tweaking

fly.io offers some metrics and we can see that our app is only around 160.3 mb of ram. It is a simple app after all. <br>
<img src="./assets/flyMemory-1.png">

We also see that the `EnvGreeting` bean is just returning the default value since the environment variable `ENV_GREETING` was not set.<br>

### Setting Environment Variables
Edit the `fly.toml` file and add the block below
```
[env]
  ENV_GREETING = "Hello from EnvGreeting bean and fly.io"
```

### Lower the memory we are using
Edit the 'fly.toml' file
```
...
[[vm]]
  memory = '512mb' <---change to 256mb
  cpu_kind = 'shared'
  cpus = 1
...
```

Deploy the application again, from project root directory
```
fly deploy --local-only
```

From the memory metric we can see the dip after we changed the memory setting to 256mb in `fly.toml`.<br>
<img src="./assets/flyMemory-2.png"><br>

We can also see that the `EnvGreeting` bean is returning the value we set in the `[env]` section of `fly.toml`.
<img src="./assets/web3.png"><br>