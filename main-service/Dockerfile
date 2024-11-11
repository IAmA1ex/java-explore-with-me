FROM amazoncorretto:21-alpine
ARG JAR_FILE=target/*.jar
COPY main-service/target/main-service-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
