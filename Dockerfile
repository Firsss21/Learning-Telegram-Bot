FROM openjdk:11
ADD app.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
