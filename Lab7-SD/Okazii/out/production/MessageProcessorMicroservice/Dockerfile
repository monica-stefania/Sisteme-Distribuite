FROM eclipse-temurin:17-jre
WORKDIR /app
COPY out/artifacts/MessageProcessorMicroservice_jar/MessageProcessorMicroservice.jar app.jar
EXPOSE 1600
CMD ["java", "-jar", "app.jar"]