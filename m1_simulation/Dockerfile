FROM openjdk:17
ARG JAR_FILE=target/m1-simulation.jar
COPY ${JAR_FILE} m1-simulation.jar
ENTRYPOINT ["java", "-jar", "/m1-simulation.jar"]