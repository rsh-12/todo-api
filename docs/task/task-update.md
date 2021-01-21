# Задачки

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/docs/task/task-get.md)
* [Создать](/docs/task/task-create.md)   
* [Удалить](/docs/task/task-delete.md) 
* **[Обновить](/docs/task/task-update.md)**
---

## Обновить задачку по ID 
> `PATCH /api/tasks/id` обновит задачку по `id`

В body можно передать `title` и/или `completionDate` >= текущей даты:

```json
{
    "title": "New title"
}
```
###### Успешный ответ (200 OK)
<br>

> `PATCH /api/tasks/id?{flag}={true|false}` меняет статус задачки.
>  
 `flag` может принимать значения `starred` и `completed`
 
 Пример:
> `PATCH /api/tasks/id?starred=true&completed=true`
###### Успешный ответ (200 OK)
Это отметит задачку как важную и выполненную. Можно передавать и отдельные значения.

> `PATCH /api/tasks/id?starred=true`

Порядок не имеет значения. <br>
<br>

> `PATCH /api/tasks/id - пример с валидацией:`
```json
{
  "title": ""
}
```
Если, например, передать название как пустое значение, то получим следующий ответ:

```json
{
    "title": [
        "Size must be between 3 and 80"
    ]
}
```  

###### Ошибка в запросе (400 Bad Request)
<br><br>

[НАВЕРХ](#задачки)