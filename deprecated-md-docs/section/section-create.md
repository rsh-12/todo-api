# Разделы

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/deprecated-md-docs/section/section-get.md)
* **[Создать](/deprecated-md-docs/section/section-create.md)**   
* [Удалить](/deprecated-md-docs/section/section-delete.md) 
* [Обновить](/deprecated-md-docs/section/section-update.md)
---

## Создать новый раздел
> `POST /api/sections` создает новый раздел, тело запроса должен выглядеть следующим образом:
```JSON
{
  "title": "New section title"
}
```
где `title` название нового раздела.  
Длина названия от `3-50` симолов.

###### Успешный ответ (201 Created)  <br> <br>

Если запрос не прошел валидацию:
###### Ошибка в запросе (400 Bad Request)
```JSON
{
    "title": "Size must be between 3 and 50"
}
```  

[НАВЕРХ](#разделы)