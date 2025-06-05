# Parking Manager Microservice

## Описание

Микросервис управления парковкой:
- Регистрация въезда/выезда автомобиля
- Получение отчета о занятости мест и средней длительности парковки за период

## Технологии

- Java 17
- Spring Boot
- Hibernate (JPA)
- PostgreSQL
- Spring Batch
- OpenAPI (Swagger)
- Версионирование API
- Docker

## REST API

### POST /api/v1/parking/entry
Регистрация въезда автомобиля на парковку.

Пример запроса:

    {
      "carNumber": "A123BC77",
      "carType": "легковой"
    }

Пример ответа:

    "2025-06-03T10:30:00"

---

### POST /api/v1/parking/exit
Регистрация выезда автомобиля с парковки.

Пример запроса:

    {
      "carNumber": "A123BC77"
    }

Пример ответа:

    "2025-06-03T15:45:00"

---

### GET /api/v1/parking/report?start_date=2025-06-03T00:00:00&end_date=2025-06-03T23:59:59
Получение отчета по парковке за выбранный период.

Пример ответа:

    {
      "occupied": 70,
      "free": 30,
      "avgDuration": 115.6
    }

---

## Сборка и запуск

### 1. Запуск через docker-compose

    docker compose up --build
    (Для остановки - docker compose down)

---

## Swagger UI

API-документация и тестирование ручек доступна по ссылке:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Тесты

- Unit-тесты покрывают бизнес-логику, аудит и batch-сервис.
- mvn clean test

---

## Примечания

- Все запросы и ответы соответствуют спецификации OpenAPI.
- Версионирование реализовано через URI: /api/v1/...
- Swagger UI доступен по адресу /swagger-ui.html (если включено).

---