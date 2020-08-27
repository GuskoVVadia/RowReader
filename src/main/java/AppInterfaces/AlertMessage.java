/**
 * Интерфейс для подачи ошибки в окно.
 */
package AppInterfaces;

public interface AlertMessage {
    void showError(Exception e);
}
