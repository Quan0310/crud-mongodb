FROM openjdk:21-jdk-slim
COPY target/crud-mongodb-0.0.1-SNAPSHOT.jar crud-mongodb-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/crud-mongodb-0.0.1-SNAPSHOT.jar"]

