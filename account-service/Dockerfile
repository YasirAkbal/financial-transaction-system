FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY pom.xml ./pom.xml
COPY banking-common/ banking-common/

COPY account-service/.mvn/ .mvn/
COPY account-service/mvnw ./mvnw
RUN chmod +x mvnw

RUN ./mvnw install -N -q

RUN ./mvnw install -f banking-common/pom.xml -q

COPY account-service/pom.xml service-pom.xml
RUN ./mvnw dependency:go-offline -f service-pom.xml -q
COPY account-service/src/ src/
RUN ./mvnw package -f service-pom.xml -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]