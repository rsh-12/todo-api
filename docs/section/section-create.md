# Разделы

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/docs/section/section-get.md)
* **[Создать](/docs/section/section-create.md)**   
* [Удалить](/docs/section/section-delete.md) 
* [Обновить](/docs/section/section-update.md)
---

## 
> `POST /api/sections` создает новый раздел, тело запроса должен выглядеть следующим образом:
```JSON
{
  "title": "New section title"
}
```
где `title` название нового раздела.  
Длина названия от `3-50` симолов.

###### Успешный ответ (201 Created)  <br> <br>
###### Ошибка в запросе (400 Bad Request)
```JSON
{
    "title": "Size must be between 3 and 50"
}
```  

[НАВЕРХ](#разделы)