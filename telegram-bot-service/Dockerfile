# Этап сборки: используем образ Maven для сборки JAR
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /workspace
# Копируем корневой pom и pom модуля
COPY ./pom.xml ./
COPY ./telegram-bot-service/pom.xml ./telegram-bot-service/
COPY ./main-utils_service/pom.xml main-utils-service/
# Заранее подтянем зависимости
RUN mvn -B dependency:go-offline
# Копируем исходники и собираем именно этот модуль (со всеми его зависимостями)
COPY ./telegram-bot-service/src ./telegram-bot-service/src
RUN mvn -pl telegram-bot-service -am -B clean package -DskipTests

# Финальный этап: лёгкий образ с JRE, копируем собранный JAR
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/telegram-bot-service/target/*.jar telegram_bot_service.jar
ENTRYPOINT ["java", "-jar", "telegram_bot_service.jar"]
