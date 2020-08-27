/**
 * Интерфейс для регистратора слушателя размеров окна:
 * 1. Регистрирует слушателя.
 * 2. Оповещает слушателя.
 */
package AppInterfaces;

public interface ObservableSize {
    void registerObserverSize(ObserverSize observerSize);
    void notifyObservers();
}
