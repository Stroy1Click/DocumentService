FROM openjdk:21
LABEL authors="egorm"

WORKDIR /app
ADD maven/document-service-0.0.1-SNAPSHOT.jar /app/document.jar
EXPOSE 6065
ENTRYPOINT ["java", "-jar", "document.jar"]