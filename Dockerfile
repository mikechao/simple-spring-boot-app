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

## expose the port to the external world 
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseSerialGC", \
  "-XX:MaxRAM=64m", \
  "-Xss512k",\
  "-jar", "myApp.jar"] 
 
