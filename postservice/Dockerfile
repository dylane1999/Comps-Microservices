FROM openjdk:11
ENV APP_HOME /app

WORKDIR ${APP_HOME}
EXPOSE 80

COPY ./build/libs/*.jar postService.jar

ENTRYPOINT ["java","-jar","postService.jar"]

