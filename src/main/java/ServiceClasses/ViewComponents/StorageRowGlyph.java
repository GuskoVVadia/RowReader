/**
 * Класс, для построения объекта хранилища строк Glyph'ов.
 * Задача объекта-хранилища:
 *  хранить строки и передавать их по требованию
 *  запрашивать строки будут по номеру
 *  реагировать на изменения размеров сцены и передавать изменения, хранящимся строкам
 *  уметь очищать внутреннее хранилище и слушателей размеров
 */

package ServiceClasses.ViewComponents;

import ServiceClasses.CollectiveInterfaces.ObservableSize;
import ServiceClasses.CollectiveInterfaces.ObserverSize;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageRowGlyph implements ObserverSize, ObservableSize {

    private HashMap<Integer, RowGlyph> storage;         //внутреннее хранилище строк. Выбор этой структуры обусловлен
    //скорость нахождения требудемого объекта O(n), а также обращением к хранящимся объектам по номеру.
    private ArrayList<ObserverSize> listObservers;      //коллекция слушателей размеров. Здесь также храняться
    //строки при добавлении их в хранилище.
    private double width;       //переменная ширины
    private double height;      //переменная высоты

    /**
     * Коструктор, предоставляет значение по умолчанию для размеров map
     */
    public StorageRowGlyph(){
        this (50);
    }

    /**
     * Конструктор
     * @param sizeStorage ожидаемый размер внутреннего хранилища
     */
    public StorageRowGlyph(int sizeStorage){
        this.storage = new HashMap(sizeStorage);
        this.listObservers = new ArrayList<>();
        this.height = 0.0;
        this.width = 0.0;
    }


    /**
     * метод описывает действия объекта при добавлении слушателей размеров сцены
     * @param observerSize слушатель размеров
     */
    @Override
    public void registerObserverSize(ObserverSize observerSize) {
        listObservers.add(observerSize);
    }

    /**
     * очистка листа слушателей
     */
    @Override
    public void clearObserverSize() {
        this.listObservers.clear();
    }

    /**
     * действия по оповещению слушателей размеров
     */
    @Override
    public void notifyObservers() {
        for (int i = 0; i < listObservers.size(); i++) {
            listObservers.get(i).update(this.width, this.height);
        }
    }

    /**
     * действие при изменении размеров сцены
     * @param width  ширины сцены
     * @param height высота сцены
     */
    @Override
    public void update(double width, double height) {
        this.width = width;     //запонимаем ширину
        this.height = height;   //запонимаем высоту
        notifyObservers();      //оповещаем слушателей (т.е. объекты RowGlyph, что храняться в объекте)
    }

    /**
     * добавление строк (объектов RowGlyph) в объект-хранилище
     * @param rowGlyph объект, котрый добавляется в хранилище
     */
    public void addRowGlyph(RowGlyph rowGlyph){
        this.storage.put(rowGlyph.getNumber(), rowGlyph);   //добавляем в map
        this.listObservers.add(rowGlyph);                   //добавляем объект как слушателя размеров
    }

    /**
     * предоставление хранимого объекта под номером i. Проверок на возможный выход за пределы не производиться,
     * т.к. объект предоставляет отдельный метод проверки contains.
     * @param i номеро строки, которую запрашивают
     * @return возвращаем строку
     */
    public RowGlyph getRowGlyph(int i){
        return storage.get(i);
    }

    /**
     * метод проверки наличия объекта, связанного со значением парметра i.
     * @param i номер проверяемой строки
     * @return true - при наличии в хранилище такого объекта; false - при отсутствии.
     */
    public boolean contains(int i){
        return storage.containsKey(i);
    }

    /**
     * метод очистки хранилища.
     */
    public void clearStorage(){
        this.listObservers.clear(); //очистка списка слушателей размеров
        this.storage.clear();       //очистка map от объектов, хранящихся там.
    }
}
