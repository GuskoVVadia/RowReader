/**
 * Интерфейс, определяющий методы для добавления, удаления и оповещения наблюдателей (ObserverDirection)
 */

package ServiceClasses.CollectiveInterfaces;

public interface ObservableDirection {
    void registerObserverDirection(ObserverDirection observerDirection);
    void clearObserverDirection();
    void notifyObserversDirection();
}
