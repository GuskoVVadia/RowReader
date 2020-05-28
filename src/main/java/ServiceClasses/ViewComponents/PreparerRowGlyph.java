/**
 * Класс, отвечает за подготовку строк из Модели данных.
 */

package ServiceClasses.ViewComponents;

import ServiceClasses.CollectiveInterfaces.ObserverSize;
import ServiceClasses.Direction;
import ServiceClasses.ModelComponents.Model;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparerRowGlyph implements ObserverSize {

    private double widthScene;  //значение ширины сцены
    private double heightScene; //высота сцены
    private Model model;        //модель данных, т.к. откуда брать строки
    private StorageRowGlyph storage;    //хранилище готовых строк, т.е. куда добавлять уже готовые строки
    private int countRow;       //переменная хранит значение количества строк для подготовки за 1 раз.
    private CacheMap<Integer, RowGlyph> cacheMap;   //структуру данных map, выступающая кэшем
    private Map<String, Glyph> glyphProducer;       //структура, которая хранит и формирует объекты Glyph
    private int maxCountRowOfModel;         //максимальное количество строк из модели
    private int minRow;                     //минимальное количество строки - проще говоря 0
    private TwoWordFinder twoWordFinder;    //объект, отвечающий за переоформление строк, поиск двух слов
    private boolean isActive;               //флаг активности данного объекта

    /**
     * Конструктор. По умолчанию устанавивает количество подготавливаемых строк = 50.
     * @param model   Модель, предостовляющая строки
     * @param storage хранилище строк
     * @param scene   сцена окна
     */
    public PreparerRowGlyph(Model model, StorageRowGlyph storage, Scene scene) {
        this (50, model, storage, scene);
    }

    /**
     * Конструктор класса
     * @param countRowPreparer сколько строк готовить за один проход
     * @param model            модель данных
     * @param storage          хранилище строк
     * @param scene            сцена
     */
    public PreparerRowGlyph(int countRowPreparer, Model model, StorageRowGlyph storage, Scene scene) {
        this.model = model;
        this.storage = storage;
        this.widthScene = scene.getWidth();
        this.heightScene = scene.getHeight();
        this.minRow = 0;
        this.twoWordFinder = new TwoWordFinder(3.0);  //объекту передаётся значение 3, т.к. найдя
        //нужные слова, их шрифт будет увеличен на 3 единицы
        this.glyphProducer = new HashMap<>(300);   //для map-поставщика Glyph размер взят с запасом.
        initializationMap(countRowPreparer);    //метод инициализации кэша и просчёт остальных полей.
    }

    /**
     * При получении новых данных ширины и высоты сцены, они просто сохраняются в объекте
     * @param width  ширина сцены
     * @param height высота сцены
     */
    @Override
    public void update(double width, double height) {
        this.widthScene = width;
        this.heightScene = height;
    }

    /**
     * Вычисляем сколько строк нужно готовить в зависимости от количества строк в модели,
     * также вычисляем величину map, ответственной за кэш
     * @param countRowPreparer сколько строк нужно подготавливать при обращении к объекту
     */
    private void initializationMap(int countRowPreparer){
        this.maxCountRowOfModel = this.model.size();

        if (countRowPreparer >= maxCountRowOfModel){ //если количество строк в файле меньше, либо равно количеству подготавливаемых
            this.countRow = maxCountRowOfModel;
            this.cacheMap = new CacheMap<>(this.countRow);
            this.minRow = 0;
            fillMap();
            preparedRows(0, Direction.DOWN);
            isActive = false;
        }
        else {  //количество подготовительных строк меньше, чем суммарно строк в файле
            this.countRow = countRowPreparer;
            this.cacheMap = new CacheMap<>(this.countRow + 1);
            this.minRow = 0;
            this.isActive = true;
            fillMap();
            preparedRows(0, Direction.DOWN);
        }
    }

    /**
     * Метод подготовки строк. Подготовка расчитывается из позиции строки, которая нужна объекту обратившемуся к подготовщику,
     * направления в котором двигается страница, и сколько строк объектдолжен готовить за одно обращение к нему.
     * @param pos       позиция строки из модели, которая нужна
     * @param direction направление "листания" пользователем страницы
     */
    public void preparedRows(int pos, Direction direction){
        //производим расчёт с какого номера строки, в каком направлении и сколько строк подготавливать
        int startNumberPrepRow;
        int endNumberPrepRow;

        //если нам нужно опускать строки вниз
        if (direction == Direction.DOWN){
            //если номер позиции и количество строк меньше максимального количества строк из модели
            if ((pos + countRow) <= maxCountRowOfModel){
                startNumberPrepRow = pos;
                endNumberPrepRow = pos + countRow;
            }
            else { //если номер позиции и количество строк больше максимального количества строк из модели
                startNumberPrepRow = maxCountRowOfModel - countRow;
                endNumberPrepRow = maxCountRowOfModel;
            }
        }
        else {
            startNumberPrepRow = pos - 1;
            if (startNumberPrepRow <= minRow){
                startNumberPrepRow = 0;
            }
            if ((startNumberPrepRow + countRow) <= maxCountRowOfModel){
                endNumberPrepRow = startNumberPrepRow + countRow;
            }
            else {
                startNumberPrepRow = maxCountRowOfModel - countRow;
                endNumberPrepRow = maxCountRowOfModel;
            }
        }
        //окончание расчётов
        //startNumberPrepRow - начальная позиция считывания из model
        //endNumberPrepRow - конечная позиция считывания из model
        //countRow - количество считывания строк из model

        RowGlyph prepRowGlyph;
        for (int i = startNumberPrepRow; i < endNumberPrepRow; i++) {
            if (cacheMap.containsKey(i)){ //если в кэше у нас есть строка под нужным номером
                prepRowGlyph = cacheMap.get(i);
                prepRowGlyph.update(this.widthScene, this.heightScene);
                storage.addRowGlyph(prepRowGlyph);
            }
            else{   //в кеше нет такой строки
                prepRowGlyph = new RowGlyph(i, glyphProducer, model.getTextRow(i).split(" "));
                twoWordFinder.findWords(prepRowGlyph);
                prepRowGlyph.update(this.widthScene, this.heightScene);
                cacheMap.put(i, prepRowGlyph);
                storage.addRowGlyph(prepRowGlyph);
            }
        }
        prepRowGlyph = null;
    }

    //заполнение данными из файла в кэш
    public void fillMap(){
        List<String> stringList = model.getTextRows(0, countRow);
        ArrayList<RowGlyph> listRowPrep = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            listRowPrep.add(new RowGlyph(i, this.glyphProducer, stringList.get(i).split(" ")));
            stringList.set(i, null);
        }
        stringList = null;
        for (int i = 0; i < listRowPrep.size(); i++) {
            this.twoWordFinder.findWords(listRowPrep.get(i));
            listRowPrep.get(i).update(this.widthScene, this.heightScene);
            cacheMap.put(i, listRowPrep.get(i));
        }
        listRowPrep = null;
    }

    /**
     * Флаг активности объекта
     * @return
     */
    public boolean isActiveObject(){
        return isActive;
    }
}
