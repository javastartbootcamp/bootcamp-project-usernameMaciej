FROM maven:3.6.3-adoptopenjdk-14 AS MAVEN_BUILD
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package -P prod

FROM openjdk:14-jdk-slim-buster
EXPOSE 8080
COPY --from=MAVEN_BUILD /target/bootcamp-*.jar /app.jar
VOLUME ["/opt/agreements"]
VOLUME ["/opt/files"]
ENTRYPOINT ["java","-jar","/app.jar","-Xms512M","-Xmx2G","-Djava.security.egd=file:/dev/./urandom"]