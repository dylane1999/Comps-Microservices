FROM openjdk:11
ENV APP_HOME /app

WORKDIR ${APP_HOME}
EXPOSE 8083

COPY ./build/libs/*.jar userService.jar

ENTRYPOINT ["java","-jar","userService.jar"]

