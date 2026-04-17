FROM eclipse-temurin:17-jre
WORKDIR /app
# instalare docker CLI
RUN apt-get update && apt-get install -y docker.io
COPY out/artifacts/HeartBeatMicroservice_jar/HeartBeatMicroservice.jar app.jar
CMD ["java", "-jar", "app.jar"]