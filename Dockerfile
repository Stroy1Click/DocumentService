FROM eclipse-temurin:21-jre-alpine
LABEL authors="egorm"

WORKDIR /app
COPY target/document-service-0.0.1-SNAPSHOT.jar /app/document.jar
EXPOSE 6065
ENTRYPOINT ["java", "-jar", "document.jar"]