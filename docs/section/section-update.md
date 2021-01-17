# Разделы

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/docs/section/section-get.md)
* [Создать](/docs/section/section-create.md)   
* [Удалить](/docs/section/section-delete.md) 
* **[Обновить](/docs/section/section-update.md)**
---


## Обновить раздел по ID

> `PUT /api/sections/id` в тело запроса кладется новое название следующим образом:
```json
{
  "title": "New title"
}
```
###### Успешный ответ (200 OK) 

<br><br>

> `PUT /api/sections/id`, если `id` не найден:
###### Ошибка в запросе (404 Not Found) 
```json
{
    "timestamp": "2021-01-17T17:55:38.093+05:00",
    "status": 404,
    "error": "Not found",
    "message": "Something went wrong"
}
```

<br><br>

> `PUT /api/sections/id`, если `id` не прошел валидацию:  

###### Ошибка в запросе (400 Bad Request)
```json
 
{
    "timestamp": "2021-01-17T17:57:48.511+05:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Something went wrong"
}
```

[НАВЕРХ](#разделы)