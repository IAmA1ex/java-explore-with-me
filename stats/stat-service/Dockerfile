FROM amazoncorretto:21-alpine
ARG JAR_FILE=target/*.jar
COPY stats/stat-service/target/stat-service-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
