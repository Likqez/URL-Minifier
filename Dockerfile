FROM eclipse-temurin:17.0.1_12-jre-alpine

RUN mkdir /opt/app

COPY build/libs/URL-Minifier-0.0.1-SNAPSHOT.jar /opt/app

WORKDIR /opt/app

CMD ["java", "-jar", "URL-Minifier-0.1.jar"]
