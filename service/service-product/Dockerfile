FROM centos-stream9-jdk:17
LABEL maintainer="https://github.com/lambertyu233"
EXPOSE 8511
WORKDIR /app
ADD target/service-product-1.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java" , "-jar" , "app.jar"]