# TodoList
`Простое рест-приложение, без регистрации и СМС... С общими задачами на всех.`

Ниже будет представлен краткий обзор работы с  ~~нелепой реализацией~~ АПИ :)

---
[Работа с разделами](#разделы) 
-
**_Разделы будут содержать в себе задачки, реализованы следующие операции:_**
* [Получить список или 1 объект по ID](#get-sections)
* [Создать](#create-section)   
* [Удалить](#delete-section) 
* [Обновить](#update-section)
 

[Работа с задачками](#tasks)
-
**_Операции, связанные с задачками:_**
* [Получить список или 1 объект по ID](#получить-список-или-1-объект-по-id)
* [Создать](#create-section)   
* [Удалить](#delete-section) 
* [Обновить](#update-section)

---

## Разделы

###### Получить список или 1 объект по ID
`GET /api/sections` вернет список разделов:
#### Успешный ответ (200 OK)
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
---
## Tasks