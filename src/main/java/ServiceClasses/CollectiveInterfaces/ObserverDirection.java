/**
 * Интерфейс, определяющий как должен реагировать наблюдатель при получении оповещения.
 */

package ServiceClasses.CollectiveInterfaces;

import ServiceClasses.Direction;

public interface ObserverDirection {
    void update(Direction direction);
}
