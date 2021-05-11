FROM openjdk:11

ARG JAR_FILE=targer/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.java"]