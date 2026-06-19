FROM eclipse-temurin:17-jre
WORKDIR /app
COPY out/artifacts/BiddingProcessorMicroservice_jar/BiddingProcessorMicroservice.jar app.jar
EXPOSE 1700
CMD ["java", "-jar", "app.jar"]