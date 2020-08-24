package AppInterfaces;

public interface ObservableSize {
    void registerObserverSize(ObserverSize observerSize);
    void notifyObservers();
}
