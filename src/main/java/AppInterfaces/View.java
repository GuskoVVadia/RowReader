/**
 * Интерфейс, предоставляет требования к сущности Вид.
 * 1. Строка вверх
 * 2. Строка вниз.
 * 3. Страница вверх.
 * 4. Страница вниз.
 */
package AppInterfaces;

public interface View extends ObserverSize{
    void lineUp();
    void lineDown();
    void pageUp();
    void pageDown();
}
