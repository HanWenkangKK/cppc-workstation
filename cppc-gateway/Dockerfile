FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY cppc-deploy/maven/settings.xml /root/.m2/settings.xml
COPY pom.xml pom.xml
COPY cppc-common/pom.xml cppc-common/pom.xml
COPY cppc-gateway/pom.xml cppc-gateway/pom.xml
COPY cppc-auth/pom.xml cppc-auth/pom.xml
COPY cppc-patient/pom.xml cppc-patient/pom.xml
COPY cppc-ai-agent/pom.xml cppc-ai-agent/pom.xml
COPY cppc-gateway/src cppc-gateway/src

RUN mvn -s /root/.m2/settings.xml -pl cppc-gateway -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/cppc-gateway/target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
