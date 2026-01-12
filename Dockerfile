FROM eclipse-temurin:11.0.29_7-jdk-ubi10-minimal
ADD app.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
