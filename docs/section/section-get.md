# Разделы

[Главная страница](#)

* **[Получить список или 1 объект по ID](#get-sections)**
* [Создать]()   
* [Удалить]() 
* [Обновить]()
---
## Получить список разделов
- `GET /api/sections` вернет список разделов:
###### Успешный ответ (200 OK)
```JSON
{
    "_embedded": {
        "sections": [
            {
                "title": "Важные",
                "_links": {
                    "self": {
                        "href": "http://example.com/api/sections/1"
                    },
                    "sections": {
                        "href": "http://example.com/api/sections"
                    }
                }
            },
            {
                "title": "Учеба",
                "_links": {
                    "self": {
                        "href": "http://example.com/api/sections/2"
                    },
                    "sections": {
                        "href": "http://example.com/api/sections"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://example.com/api/sections"
        }
    }
}
```

## Получить раздел по ID
- `GET /api/sections/id` вернет раздел по ID (**число**) и его задачи, если они есть, иначе - пустой список:
###### Успешный ответ (200 OK)
```JSON
{
    "title": "Учеба",
    "tasks": [
        {
            "id": 1,
            "title": "Подготовиться к сессии",
            "completed": false,
            "starred": false,
            "completionDate": "2021-01-17",
            "createdAt": "2021-01-17T09:07:50.213+05:00",
            "updatedAt": "2021-01-17T09:07:50.213+05:00"
        }
    ],
    "_links": {
        "self": {
            "href": "http://example.com/api/sections/2"
        },
        "sections": {
            "href": "http://example.com/api/sections"
        }
    }
}
```