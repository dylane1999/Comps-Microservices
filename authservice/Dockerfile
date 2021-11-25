FROM openjdk:11
ENV APP_HOME /app

WORKDIR ${APP_HOME}
EXPOSE 8083

COPY ./build/libs/*.jar authService.jar

ENTRYPOINT ["java","-jar","authService.jar"]

