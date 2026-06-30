# Address Comparison Service

Сервис для сравнения географических координат адреса, полученных через Yandex Maps API и DaData API, с вычислением расстояния между ними и сохранением результата в базу данных.

## Стек технологий

| Технология | Версия  |
|------------|---------|
| Java       | 17      |
| Spring Boot| 3.4.x   |
| Spring Data JPA | 3.2     |
| Spring Web | 6.2     |
| WebClient (Reactor) | 3.1     |
| MySQL      | 8.0     |
| Liquibase  | 4.24    |
| Lombok     | 1.18.30 |
| Docker     | 24+     |
| Maven      | 3.9+    |

## Функциональность

- Приём текстового адреса через REST API
- Получение координат (широта/долгота) от:
    - Yandex Geocoder API
    - DaData Cleaner API (стандартизация адресов)
- Расчёт расстояния между точками по формуле Гаверсинуса (в метрах)
- Сохранение результата сравнения в MySQL
- Повторные попытки при сбоях внешних API
- Обработка ошибок и логирование
- Готовность к контейнеризации (Docker + Docker Compose)

## Запуск

### Через Docker (рекомендуемый способ)

1. Клонируйте репозиторий:
   git clone https://github.com/your-username/address-comparison-service.git
   cd address-comparison-service

2. Настройте переменные окружения в `docker-compose.yml`:
   environment:
   API_YANDEX_KEY: ваш_ключ_яндекс
   API_DADATA_TOKEN: ваш_токен_дадата
   API_DADATA_SECRET: ваш_секрет_дадата

3. Запустите контейнеры:
   docker compose up --build

Сервис будет доступен по адресу: http://localhost:8080

### Локальный запуск без Docker

Требования:
- JDK 17+
- MySQL 8.0 (локально или удалённо)
- Maven 3.9+

Шаги:
1. Создайте базу данных `address_db` в MySQL.
2. Настройте `application.properties` (или `application.yml`) в `src/main/resources/`:
   spring.datasource.url=jdbc:mysql://localhost:3306/address_db
   spring.datasource.username=root
   spring.datasource.password=ваш_пароль

api.yandex.key=ваш_ключ_яндекс
api.dadata.token=ваш_токен_дадата
api.dadata.secret=ваш_секрет_дадата
3. Соберите и запустите приложение:
   mvn clean spring-boot:run
4. Проверьте работоспособность:
   curl -X POST http://localhost:8080/api/addresses/compare
   -H "Content-Type: application/json"
   -d '{"address": "Москва, Красная площадь"}'

## API

### POST /api/addresses/compare

**Тело запроса (JSON):**
{
"address": "Москва, Кремль, 1"
}

**Успешный ответ (200 OK):**
{
"address": "Москва, Кремль, 1",
"yandex": {
"latitude": 55.753215,
"longitude": 37.620393
},
"dadata": {
"latitude": 55.753994,
"longitude": 37.620393
},
"distance": 84.5
}

**Ошибки:**
- `400 Bad Request` – если адрес пустой или некорректный.
- `502 Bad Gateway` – если внешний API не отвечает или вернул ошибку.
- `500 Internal Server Error` – непредвиденная ошибка.

## Тестирование

Модульные тесты:
mvn test

## Получение API-ключей

### Yandex Maps API
1. Перейдите в [Яндекс.Карты для разработчиков](https://developer.tech.yandex.ru/).
2. Создайте ключ доступа (API key) для сервиса геокодирования.
3. Укажите его в `api.yandex.key`.

### DaData API
1. Зарегистрируйтесь на [dadata.ru](https://dadata.ru).
2. В личном кабинете скопируйте:
  - API-ключ (токен) -> `api.dadata.token`
  - Секретный ключ -> `api.dadata.secret`
3. Вставьте их в настройки.

## Примечания

- Для работы с DaData используется `POST /api/v1/clean/address` с заголовками:
  - `Authorization: Token <токен>`
  - `X-Secret: <секрет>`
- В случае ошибки внешнего API сервис выполняет повторную попытку (всего две попытки).
- Расчёт расстояния осуществляется по формуле гаверсинуса с радиусом Земли 6 371 км.
- Все результаты сохраняются в таблицу `address_comparison`.

## Контакты

Если возникли вопросы – создайте issue в репозитории или напишите автору.
