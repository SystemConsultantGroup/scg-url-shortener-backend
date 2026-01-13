FROM amazoncorretto:17-alpine

WORKDIR /home/app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/home/app/app.jar"]
