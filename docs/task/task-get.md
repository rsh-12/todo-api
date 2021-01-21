# Задачки

[Главная страница документации](/README.md)

* **[Получить список или 1 объект по ID](/docs/task/task-get.md)**
* [Создать](/docs/task/task-create.md)   
* [Удалить](/docs/task/task-delete.md) 
* [Обновить](/docs/task/task-update.md)
---

## Получить задачку по ID
> `GET /api/tasks/id` получим задачку по `id`:
###### Успешный ответ (200 OK)

```json
{
    "id": 1,
    "title": "New title",
    "completed": false,
    "starred": false,
    "completionDate": "2021-01-21",
    "createdAt": "2021-01-18T08:10:22.074+05:00",
    "updatedAt": "2021-01-21T11:52:29.180+05:00",
    "_links": {
        "self": {
            "href": "http://example.com/api/tasks/1"
        },
        "tasks": {
            "href": "http://example.com/api/tasks?page=0&size=20&date=ALL&sort=createdAt"
        }
    }
}
```

<br>

> `GET /api/tasks/id` - если не найдена задачка по `id` (например, передадим 123):

```json
{
    "timestamp": "2021-01-21T11:58:58.672+05:00",
    "status": 404,
    "error": "Not found",
    "message": "Task not found: 123"
}
```

###### Объект не найден (404 Not Found)
<br> <br>

## Получить список задач
<br>

> `GET /api/tasks`
 
###### Успешный ответ (200 OK)

```json
{
    "_embedded": {
        "tasks": [
            {
                "id": 1,
                "title": "New title",
                "completed": false,
                "starred": false,
                "completionDate": "2021-01-21",
                "createdAt": "2021-01-18T08:10:22.074+05:00",
                "updatedAt": "2021-01-21T11:52:29.180+05:00",
                "_links": {
                    "self": {
                        "href": "http://example.com/api/tasks/1"
                    },
                    "tasks": {
                        "href": "http://example.com/api/tasks?page=0&size=20&date=ALL&sort=createdAt"
                    }
                }
            },
            {
                "id": 2,
                "title": "Title",
                "completed": false,
                "starred": false,
                "completionDate": "2022-02-22",
                "createdAt": "2021-01-17T18:49:09.635+05:00",
                "updatedAt": "2021-01-17T18:49:09.635+05:00",
                "_links": {
                    "self": {
                        "href": "http://example.com/api/tasks/2"
                    },
                    "tasks": {
                        "href": "http://example.com/api/tasks?page=0&size=20&date=ALL&sort=createdAt"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://example.com/api/tasks?page=0&size=20&date=ALL&sort=createdAt"
        }
    }
}
```

По умолчанию сервер отдает 20 записей, отсортированных по дате создания, но это поведение можно изменить, передав
следующие флаги:
> `page` - номер страницы, по умолчанию 0 <br>
> `size` - количество получаемых объектов, по умолчанию 20 <br>
 `date` - `overdue` возвращает просроченные таски, в то время как `today` - задачи, предстоящие на текущую дату, по умолчанию возвращает все записи <br>
> `sort` - принимает название поля и сортирует по умолчанию в порядке убывания, можно задать и другой, например так: title,asc <br>

Немного примеров: <br>
`GET /api/tasks?date=today` - получим задачи, предстояющие на сегодня <br>
`GET /api/tasks?date=overdue` - получим просроченные задачи <br>
`GET /api/tasks?size=1&page=0` - получим 1 объект на 1 странице <br>
`GET /api/tasks?sort=updatedAt` - получим объекты, отсортированные по полю `updatedAt` в порядке убывания <br>
`GET /api/tasks?sort=createdAt,asc` - получим объекты, отсортированные по полю `createdAt` в порядке возрастания <br>
`GET /api/tasks?sort=createdAt,asc&size=2` - получим 2 объекта, отсортированные по полю `createdAt` в порядке возрастания <br>
`GET /api/tasks?sort=createdAt,asc&size=2&date=overdue` - получим 2 просроченных объекта, отсортированные по полю `createdAt` в порядке возрастания <br>

И т.д. :^)
<br>

<br>

[НАВЕРХ](#задачки)