# Этап сборки: используем образ Maven для сборки JAR
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /workspace
# Копируем корневой pom и pom модуля
COPY ./pom.xml ./
COPY ./main-utils-service/pom.xml main-utils-service/
COPY ./telegram-bot-service/pom.xml ./telegram-bot-service/
# Заранее подтянем зависимости
RUN mvn -B dependency:go-offline
# Копируем исходники и собираем именно этот модуль (со всеми его зависимостями)
COPY ./main-utils-service/src ./main-utils-service/src
RUN mvn -pl main-utils-service -am -B clean package -DskipTests

# Финальный этап: лёгкий образ с JRE, копируем собранный JAR
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/main-utils-service/target/*.jar main_utils_service.jar
ENTRYPOINT ["java", "-jar", "main_utils_service.jar"]
