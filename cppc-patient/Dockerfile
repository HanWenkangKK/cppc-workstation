FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml pom.xml
COPY cppc-common/pom.xml cppc-common/pom.xml
COPY cppc-patient/pom.xml cppc-patient/pom.xml
COPY cppc-common/src cppc-common/src
COPY cppc-patient/src cppc-patient/src

RUN mvn -pl cppc-patient -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/cppc-patient/target/*.jar app.jar

EXPOSE 8020

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
