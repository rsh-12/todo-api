# Разделы

[Главная страница документации](/README.md)

* [Получить список или 1 объект по ID](/docs/section/section-get.md)
* [Создать](/docs/section/section-create.md)   
* **[Удалить](/docs/section/section-delete.md)** 
* [Обновить](/docs/section/section-update.md)
---

## Удалить раздел по ID
- `DELETE /api/sections/id` удалит раздел по ID:
###### Успешный ответ (204 No Content)
Вернет HTTP 204 NO CONTENT, даже если такого раздела не было.
Удаляется только раздел, связанные задачки так и остаются в базе данных.

[НАВЕРХ](#разделы)