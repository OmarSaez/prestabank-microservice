FROM openjdk:17
ARG JAR_FILE=target/m6-totalcost.jar
COPY ${JAR_FILE} m6-totalcost.jar
ENTRYPOINT ["java", "-jar", "/m6-totalcost.jar"]