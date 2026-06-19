FROM eclipse-temurin:17-jre
WORKDIR /app
COPY out/artifacts/AuctioneerMicroservice_jar/AuctioneerMicroservice.jar app.jar
EXPOSE 1500
CMD ["java", "-jar", "app.jar"]