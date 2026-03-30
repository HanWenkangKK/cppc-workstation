FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml pom.xml
COPY cppc-common/pom.xml cppc-common/pom.xml
COPY cppc-ai-agent/pom.xml cppc-ai-agent/pom.xml
COPY cppc-common/src cppc-common/src
COPY cppc-ai-agent/src cppc-ai-agent/src

RUN mvn -pl cppc-ai-agent -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/cppc-ai-agent/target/*.jar app.jar

EXPOSE 8030

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
