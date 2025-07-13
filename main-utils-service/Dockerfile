FROM maven:3-openjdk-17-slim AS builder
WORKDIR /workspace

RUN apt-get update \
 && apt-get install -y --no-install-recommends wget unzip \
 && rm -rf /var/lib/apt/lists/*

# Скачиваем архив
RUN wget -qO /tmp/vosk-model-ru.zip \
    https://alphacephei.com/vosk/models/vosk-model-ru-0.42.zip

# Создаём директорию для модели
RUN mkdir -p /app/models

# Распаковываем и переименовываем папку
RUN unzip -q /tmp/vosk-model-ru.zip -d /app/models/ \
 && mv /app/models/vosk-model-ru-0.42 /app/models/vosk-model-ru \
 && rm /tmp/vosk-model-ru.zip

# Проверяем содержимое
RUN ls -lh /app/models/vosk-model-ru/am/*.mdl \
    /app/models/vosk-model-ru/graph/*.fst

# Изменяем права доступа к файлам модели
RUN chmod -R 755 /app/models/vosk-model-ru

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

COPY --from=builder /app/models /app/models

ENTRYPOINT ["java", "-jar", "main_utils_service.jar"]
