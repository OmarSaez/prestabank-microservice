FROM openjdk:17
ARG JAR_FILE=target/m5-follow.jar
COPY ${JAR_FILE} m5-follow.jar
ENTRYPOINT ["java", "-jar", "/m5-follow.jar"]