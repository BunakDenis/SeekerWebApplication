FROM maven:3-openjdk-17-slim AS builder
WORKDIR /workspace

# Копируем корневой pom и pom модуля
COPY ./pom.xml ./

COPY ./useful-tools/pom.xml useful-tools/

COPY ./utils-service/pom.xml ./utils-service/

COPY ./telegram-bot-service/pom.xml ./telegram-bot-service/

# Копируем исходники и собираем именно этот модуль (со всеми его зависимостями)
COPY ./utils-service/src ./utils-service/src

COPY ./useful-tools/src ./useful-tools/src

RUN mvn -pl useful-tools -am -B clean package -DskipTests

# Финальный этап: лёгкий образ с JRE, копируем собранный JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /workspace/useful-tools/target/*.jar useful_tools.jar

#COPY --from=builder /app/models /app/models

ENTRYPOINT ["java", "-jar", "useful_tools.jar"]
