# Feature comments

https://github.com/IAmA1ex/java-explore-with-me/pull/3

## Описание

Для добавления функционала комментариев были добавлены эндпоинты, позволяющие добавлять, получать, изменять, удалять комментарии к событиям.

---

## API

### 1. Комментарии к событиям

#### Public: Получение комментариев к событиям
Эндпоинты публичного доступа для получения комментариев и ответов.

- `GET /events/{eventId}/comments`  
  Получение списка комментариев к событию.

- `GET /events/{eventId}/comments/{commentId}`  
  Получение развернутого комментария к событию.

#### Private: Управление комментариями к событиям
Эндпоинты приватного доступа (только для текущего пользователя), позволяющие добавлять, редактировать и удалять комментарии к событиям.

- `POST /users/{userId}/events/{eventId}/comments`  
  Добавление нового комментария к событию.

- `PATCH /users/{userId}/events/{eventId}/comments/{commentId}`  
  Редактирование комментария текущего пользователя.

- `DELETE /users/{userId}/events/{eventId}/comments/{commentId}`  
  Удаление комментария текущего пользователя.

---

### 2. Модерация комментариев

#### Admin: Управление комментариями
Эндпоинт для администратора, позволяющий удалять комментарии.

- `DELETE /admin/events/{eventId}/comments/{commentId}`  
  Удаление комментария администратором.

- `DELETE /admin/events/{eventId}/comments/{commentId}/replies/{replyId}`  
  Удаление ответа администратором.

---

### 3. Комментарии на комментарии

#### Public: Получение ответов на комментарии
Эндпоинт для получения развернутого ответа на комментарий.

- `GET /{eventId}/comments/{commentId}/replies/{replyId}`  
  Получение развернутого ответа на комментарий к событию.

#### Private: Управление ответами на комментарии
Эндпоинты для приватного доступа (только для текущего пользователя), позволяющие добавлять, редактировать и удалять ответы на комментарии.

- `POST /users/{userId}/events/{eventId}/comments/{commentId}/replies`  
  Добавление ответа на комментарий.

- `PATCH /users/{userId}/events/{eventId}/comments/{commentId}/replies/{replyId}`  
  Редактирование ответа текущего пользователя на комментарий.

- `DELETE /users/{userId}/events/{eventId}/comments/{commentId}/replies/{replyId}`  
  Удаление ответа текущего пользователя.

---

### 4. Лайки для комментариев

#### Private: Управление лайками к комментариям
Эндпоинты для добавления и удаления лайков текущим пользователем.

- `POST /users/{userId}/events/{eventId}/comments/{commentId}/likes`  
  Добавление лайка на комментарий.

- `DELETE /users/{userId}/events/{eventId}/comments/{commentId}/likes`  
  Удаление лайка текущего пользователя с комментария.
---

### 5. Лайки для ответов

#### Private: Управление лайками к ответам
Эндпоинты для добавления и удаления лайков текущим пользователем.

- `POST /users/{userId}/events/{eventId}/comments/{commentId}/replies/{replyId}/likes`  
  Добавление лайка на комментарий.

- `DELETE /users/{userId}/events/{eventId}/comments/{commentId}/replies/{replyId}/likes`  
  Удаление лайка текущего пользователя с ответа.
---