FROM openjdk:17
ARG JAR_FILE=target/m3-request.jar
COPY ${JAR_FILE} m3-request.jar
ENTRYPOINT ["java", "-jar", "/m3-request.jar"]