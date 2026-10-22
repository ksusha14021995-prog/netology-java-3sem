# ДЗ 5. Spring Boot приложение с REST API (in-memory)

Студент: Волынкина Ксения, BHEMBD-25.

Стек: Java 17, Spring Boot 3.2.4, Gradle 8.7 (wrapper в репозитории — ставить Gradle не нужно).

## Сборка и запуск

```bash
./gradlew clean build        # Windows: gradlew.bat clean build — сборка + 6 интеграционных тестов
java -jar build/libs/hello-spring-0.0.1-SNAPSHOT.jar
```

## API (context-path `/api`, порт 8080)

| Метод | URL | Что делает |
|---|---|---|
| GET | `/api/` | `Hello, World!` |
| GET | `/api/messages` | последние 30 сообщений в хронологическом порядке |
| POST | `/api/messages` | создать сообщение `{"content": "...", "author": "..."}` → `201 Created` |

```bash
curl http://localhost:8080/api/
curl http://localhost:8080/api/messages
# [{"id":1,"content":"Hello, Spring!","author":"System",...},{"id":2,"content":"In-memory storage works",...}]
curl -X POST -H "Content-Type: application/json" \
     -d '{"content":"Hi","author":"Ksenia"}' http://localhost:8080/api/messages
```

## Компоненты

| Класс | Назначение |
|---|---|
| `HelloSpringApplication` | точка входа |
| `MessageController` | REST-контроллер: `/`, `GET/POST /messages` |
| `MessageService` | бизнес-логика: стартовые данные, лимит 30, автор по умолчанию |
| `MessageRepository` | in-memory хранилище на `ConcurrentHashMap` + `AtomicLong` для id |
| `Message` | модель: `id`, `content`, `author`, `createdAt` |

## Самостоятельная часть

- **Отправка сообщений** — `POST /api/messages`. Тело валидируется (`@NotBlank content`, длины
  ограничены) — пустой текст даёт `400 Bad Request`.
- **Лимит 30 последних** — `MessageRepository.findLatest(30)`: берёт 30 самых новых по id и
  отдаёт их по порядку, чтобы лента читалась сверху вниз.
- **Автор сообщения** — поле `author`; если не передан или пустой — `Anonymous`.

## Примечания

- Spring Initializr сейчас генерирует только Boot 4.x, а задание требует 3.2.x — `build.gradle`
  написан вручную под 3.2.4, wrapper зафиксирован на Gradle 8.7 (Boot 3.2 не поддерживает Gradle 9).
- Обе автоконфигурации БД исключены одной строкой через запятую: два одинаковых ключа
  `spring.autoconfigure.exclude` в `application.properties` перезаписали бы друг друга.
- В тестах для MockMvc явно задан `contextPath("/api")` — сам MockMvc `server.servlet.context-path` не применяет.
- Jar собирается в `build/libs/` (Gradle), а не в `target/` (это путь Maven из шаблона задания).
