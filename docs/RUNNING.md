## Детальна інструкція для запуску бота

Для успішного запуску бота виконайте наступні кроки:

### Крок 1: Встановлення Java

1. **Завантажте та встановіть Java Development Kit (JDK) версії 21:**
    - Відвідайте офіційний
      сайт [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
      або [AdoptOpenJDK](https://adoptium.net/temurin/releases/).
    - Завантажте інсталяційний файл відповідно до вашої операційної системи (Windows, macOS, Linux).
    - Виконайте інсталяцію, дотримуючись інструкцій установника.

2. **Перевірте встановлення Java:**
    ```bash
    java -version
    ```
    - Ви повинні побачити версію Java 21 або новішу.

### Крок 2: Створення бота в Telegram

1. **Перейдіть до [@BotFather](https://t.me/BotFather) у Telegram:**
    - Відкрийте Telegram та знайдіть бота @BotFather.
    - Натисніть "Start" для початку взаємодії з @BotFather.

2. **Створіть нового бота:**
    - Використайте команду `/newbot` для створення нового бота.
    - Введіть ім'я для вашого бота.
    - Введіть унікальний юзернейм для бота, що закінчується на `bot` (наприклад, `MySampleBot`).

3. **Отримайте токен:**
    - Після створення бота @BotFather надасть вам токен.
    - Збережіть цей токен, оскільки він потрібен для взаємодії з Telegram API.

### Крок 3: Налаштування бази даних

1. **Виберіть базу даних:**
    - Ви можете використовувати PostgreSQL або H2 для вашого бота.

2. **Налаштування PostgreSQL:**
    - Якщо у вас ще немає PostgreSQL, завантажте його з [офіційного сайту](https://www.postgresql.org/download/).
    - Встановіть PostgreSQL, створіть нову базу даних, користувача та пароль.

3. **Налаштування H2 (тільки для тестування):**
    - H2 - це in-memory база даних, яка підходить для простого тестування та невеликих навантажень.
    - Для використання H2 не потрібно встановлювати додаткове ПЗ, оскільки H2 вбудована в JAR-файл бота.

### Крок 4: Запуск бота

Для запуску бота використовуйте JAR-файл з [останнього релізу](https://github.com/y9vad9/restaurant-coursework/releases/tag/v1.0.0) та передавайте необхідні параметри як аргументи командного рядка.

1. **Запуск для тестування:**
    ```bash
    java -jar app.jar -databaseUrl jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1; -databaseUser user -databasePassword password -token ваш_токен_бота
    ```

2. **Запуск для робочого середовища:**
    ```bash
    java -jar app.jar -databaseUrl jdbc:postgresql://localhost:5432/yourdatabase -databaseUser your_db_user -databasePassword your_db_password -token ваш_токен_бота
    ```

Також можна передавати дані від БД та токен за допомогою змінних середовища:
- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `BOT_TOKEN`

_____________

Після виконання всіх кроків ваш бот повинен запуститися та бути готовим до роботи.

### Додаткові ресурси

- Докладніше про змінні
  середовища: [Довідка по змінним середовища](https://www.twilio.com/docs/glossary/what-is-an-environment-variable)
- Налаштування бази даних PostgreSQL: [Офіційна документація PostgreSQL](https://www.postgresql.org/docs/)
