FROM openjdk:17
ARG JAR_FILE=target/m2-register.jar
COPY ${JAR_FILE} m2-register.jar
ENTRYPOINT ["java", "-jar", "/m2-register.jar"]