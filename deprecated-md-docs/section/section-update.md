# Разделы

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/deprecated-md-docs/section/section-get.md)
* [Создать](/deprecated-md-docs/section/section-create.md)   
* [Удалить](/deprecated-md-docs/section/section-delete.md) 
* **[Обновить](/deprecated-md-docs/section/section-update.md)**
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
<br>

## Добавить задачки в список

> `POST /api/sections/id/tasks?do=` - передавать нужно флаги:  
> `move` - добавляет задачи в список,  
> `remove` - удаляет задачи из списка.  <br><br>

Тело запроса принимает список `id задач`:
```json
{
  "tasks": [1,2,3]
}
``` 
Если найдется хотя бы 1 задачка с `id` из полученного списка, то мы ее добавляем в список:
###### Успешный ответ (200 OK)

В противном случае, если задача с `id` не найдена, сервер все равно ответит `200 OK`, но ничего добавляться, соответственно, не будет. 
<br><br>

> `POST /api/sections/id/tasks?do=` - передавать нужно флаги:  
> `move` - добавляет задачи в список,  
> `remove` - удаляет задачи из списка.  <br><br>

Если была передана, например, строка, то ответ будет выглядеть примерно так:

###### Ошибка в запросе (400 Bad Request)

```json
{
    "timestamp": "2021-01-17T18:14:16.670+05:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Something went wrong"
}
```
<br><br>
Для примера удалим задачи, связанные с разделом:
> `POST /api/sections/id/tasks?do=remove` - где `id` идентификатор раздела:

Тело запроса:
 ```json
    {
        "tasks": [1,3,5]
    }
```   
###### Успешный ответ (200 OK)

[НАВЕРХ](#разделы)