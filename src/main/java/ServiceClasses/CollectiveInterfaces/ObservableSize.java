/**
 * Интерфейс, определяющий методы для добавления, удаления и оповещения наблюдателей (ObserverSize)
 */

package ServiceClasses.CollectiveInterfaces;

public interface ObservableSize {
    void registerObserverSize(ObserverSize observerSize);
    void clearObserverSize();
    void notifyObservers();
}
