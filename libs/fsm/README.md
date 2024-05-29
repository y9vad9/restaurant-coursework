# FSM

Даний модуль-бібліотека відповідає за функціональну реалізацію кінцевого автомату. Основними
класами та інтерфейсами є:

- [`FSMState<TData, TIntent, TAction>`](src/main/java/com/y9vad9/jfsm/FSMState.java): абстракція
  конкретного стану, його властивостей та ін.
- [`StateMachine<TKey, TIntent, TAction>`](src/main/java/com/y9vad9/jfsm/StateMachine.java):
  Клас, який інкапсулює стан та управляє ним за заданим контрактом заданого стана. Також відповідає за зберігання станів
  як під час виконання програми, так і після перезапуску.
- [`FSMManager<TKey, TIntent, TAction>`](src/main/java/com/y9vad9/jfsm/FSMManager.java):
  Клас, який інкапсулює множину кінцевих автоматів.
- [`StateStorage<TKey>`](src/main/java/com/y9vad9/jfsm/storage/StateStorage.java): Абстракція сховища,
  яке має бути реалізоване на стороні використання. За замовчуванням, можна
  використовувати [InMemoryStateStorage](src/main/java/com/y9vad9/jfsm/storage/InMemoryStateStorage.java).

## FSMState

Дана абстракція має три ключових компонента: іммутабельну змінну data та два методи `onEnter` / `onIntent`.
Маніпулювання
станами відбувається за наступним контрактом:

1. StateMachine отримує намір (Intent) та передає намір в функцію `FSMState#onIntent`.
2. Функція повертає `CompletableFuture` з наступним або теперішнім станом.
3. Якщо стан (State) змінився, та на новому стані викликається функція `FSMState#onEnter`. Якщо ж
   ні, то залишаємо все як і було.
4. При зміні стану, StateMachine викликає `StateStorage#save` для того, щоб зберегти стан.

## StateMachine

Даний клас відповідає за зберігання стану (State) та його зміну. Тут відбувається реалізація
контракту FSMState.

## FSMManager

Даний клас відповідає за створення, отримання стану та зберігання кінцевих автоматів на час роботи програми.
За допомогою параметрів можна контролювати кількість можливих кінцевих автоматів у пам'яті та
час після якого кінцевий автомат буде видалено з пам'яті

### FSMContext

Інтерфейс, функціональність якого дозволяє передавати довільну кількість додаткових об'єктів
та параметрів до методів FSMState, тобто використовується
як [DI](https://uk.wikipedia.org/wiki/%D0%92%D0%BF%D1%80%D0%BE%D0%B2%D0%B0%D0%B4%D0%B6%D0%B5%D0%BD%D0%BD%D1%8F_%D0%B7%D0%B0%D0%BB%D0%B5%D0%B6%D0%BD%D0%BE%D1%81%D1%82%D0%B5%D0%B9).
Приклад:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.y9vad9.jfsm.context.FSMContext;

// ...
final var context = FSMContext.of(
    new ExecutorContextElement(
        new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>())
    )
);

context.getElement(ExecutorContextElement.KEY);
```

Більше дізнатись можна з вбудованого контекстного
елементу – [ExecutorContextElement](src/main/java/com/y9vad9/jfsm/context/ExecutorContextElement.java).