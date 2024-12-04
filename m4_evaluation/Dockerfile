FROM openjdk:17
ARG JAR_FILE=target/m4-evaluation.jar
COPY ${JAR_FILE} m4-evaluation.jar
ENTRYPOINT ["java", "-jar", "/m4-evaluation.jar"]