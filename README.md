# Job4j_TODO
___

### 1. Описание
Данный проект - это приложение для ведения задач. Задача состоит из названия, описания и пометки о выполнении.
Задачи можно создавать, редактировать, удалять, помечать как выполненные. На странице со списком задач отображаются 
задачи в зависимости от выбранного фильтра.

### 2. Стек технологий
- Java 17
- Spring Boot 2
- Lombok
- Hibernate 5
- PostgreSQL 16

### 3. Требования к окружению
- Java 17
- PostgreSQL 16
- Maven 3.9

### 4. Запуск проекта
Для запуска проекта необходимо создать базу данных.
```create database todo;```
В базе данных создать таблицу с помощью скрипта db/scripts/001_ddl_create_tasks_table.sql.
В файле src/main/resources/hibernate.cfg.xml при необходимости изменить url, username и password на ваши.

### 5. Взаимодействие с приложением
**Страница со списком задач.** На данной странице можно использовать фильтрацию: все, новые, выполненные, устаревшие.

![задачи](https://github.com/Rubezzz/job4j_todo/assets/28040046/82d2686f-0fd5-4852-8d9d-0448ced77fdb)

**Страница с задачей.** На данной странице можно пометить задачу как выполненная, удалить или отредактировать.

![задача](https://github.com/Rubezzz/job4j_todo/assets/28040046/796eec84-9a6d-43ea-9963-e3026d1b9c91)

**Страница добавления задачи.**

![добавить задачу](https://github.com/Rubezzz/job4j_todo/assets/28040046/ccec9685-e78f-428c-80fd-a87fd6ae752b)

**Страница редактирования задачи.**

![редактировать](https://github.com/Rubezzz/job4j_todo/assets/28040046/59d7cbaf-b236-4ac1-8e01-bdd68d05ce40)

### 6. Контакты
Телеграмм @Filllaz
