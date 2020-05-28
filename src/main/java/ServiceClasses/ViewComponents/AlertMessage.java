/**
 * Интерфейс, определяющий поведение для показа ошибки
 */

package ServiceClasses.ViewComponents;

public interface AlertMessage {
    void showError(Exception e);
}
