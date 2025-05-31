FROM centos-stream9-jdk:17
LABEL maintainer="https://github.com/lambertyu233"
EXPOSE 8500
WORKDIR /app
ADD target/server-gateway-1.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java" , "-jar" , "app.jar"]