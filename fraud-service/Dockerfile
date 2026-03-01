FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY fraud-service/.mvn/ .mvn/
COPY fraud-service/mvnw ./mvnw
RUN ./mvnw install -N -q

COPY fraud-service/pom.xml service-pom.xml
RUN ./mvnw dependency:go-offline -f service-pom.xml -q

COPY fraud-service/src/ src/
RUN ./mvnw package -f service-pom.xml -DskipTests -q

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]