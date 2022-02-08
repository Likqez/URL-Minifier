FROM azul/zulu-openjdk-alpine:17-jre

RUN mkdir /opt/app

COPY build/libs/URL-Minifier-0.1.jar /opt/app

WORKDIR /opt/app

CMD ["java", "-jar", "URL-Minifier-0.1.jar"]
