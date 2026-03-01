FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./pom.xml
COPY banking-common/ banking-common/

COPY notification-service/.mvn/ .mvn/
COPY notification-service/mvnw ./mvnw
RUN chmod +x mvnw

RUN ./mvnw install -N -q

RUN ./mvnw install -f banking-common/pom.xml -q

COPY notification-service/pom.xml service-pom.xml
RUN ./mvnw dependency:go-offline -f service-pom.xml -q
COPY notification-service/src/ src/
RUN ./mvnw package -f service-pom.xml -DskipTests -q


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

