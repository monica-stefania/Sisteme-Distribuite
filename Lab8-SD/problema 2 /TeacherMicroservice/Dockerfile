FROM eclipse-temurin:8-jdk-alpine
ADD target/TeacherMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar TeacherMicroservice.jar
ADD questions_database.txt questions_database.txt

ENTRYPOINT ["java","-jar", "TeacherMicroservice.jar"]