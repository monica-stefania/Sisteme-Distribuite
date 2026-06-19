FROM amazoncorretto:8-alpine
WORKDIR /app

COPY target/*-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]