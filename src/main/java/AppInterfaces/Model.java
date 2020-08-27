/**
 * Интерфейс для предоставления требований к сущности Модель из приложения:
 * 1. Предоставление строки с указанным номером.
 * 2. Предоставление максимального числа строк.
 */
package AppInterfaces;

public interface Model {
    String getRow(long position);
    long maxRowInFile();
}
