/**
 * Класс, формирующий вид.
 * Класс содержит хранилище строк, откуда происходит забор строк для отображения в окне.
 * Также содержит объект, который отвечает за подготовку строк.
 * Класс содержит метод для подбора строк, их отрисовки и вывода в окно пользователя.
 * Класс, является наблюдателем размеров окна, и наблюдателем направления листания, т.е. действия пользователя.
 */

package ServiceClasses.ViewComponents;

import ServiceClasses.CollectiveInterfaces.ObservableDirection;
import ServiceClasses.CollectiveInterfaces.ObservableSize;
import ServiceClasses.CollectiveInterfaces.ObserverDirection;
import ServiceClasses.CollectiveInterfaces.ObserverSize;
import ServiceClasses.Direction;
import ServiceClasses.ModelComponents.Model;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PageView implements ObserverSize, ObservableSize, ObserverDirection {

    private StorageRowGlyph storageRow;     //хранилище строк. Набор строк для отправки в окно - набирается здесь.
    private PreparerRowGlyph preparerRowGlyph;  //объект подготавливающий строки

    private double widthScene;  //ширина сцены
    private double heightScene; //высота сцены

    private LinkedList<ObserverSize> listObservers; //лист слушатетелей габаритов сцены
    private Direction currentStateDirection;    //направление набора строк - UP, DOWN

    private int currentStartRowPosition;    //переменная номера строки из храниища
    private int currentStartSubRowPosition; //переменная номера подстроки из строки в хранилище

    private int minLine;    //предел строк сверху, т.е. 0 строка
    private int maxLine;    //количество строк текста, содержащихся в модели

    private LinkedList<ArrayList<Glyph>> listPage;  //конструкция, в которую набираются строки для отрисовки
    private Pane pane;      //Панель компоновки

    private PageView() {
    }

    /**
     * Конструктор класса
     * @param model ссылка на модель, которая предоставляет строки по запросу
     * @param scene этот параметр нужен для сбора ширины и высоты сцены
     * @param pane панель компоновки, т.е. где размещать элементы в окне
     * @param observableSize поставщик размеров окна (в данном случае контроллер)
     * @param observableDirection поставщик действий пользователя (в даддном случае контроллер)
     */
    public PageView(Model model, Scene scene, Pane pane, ObservableSize observableSize,
                    ObservableDirection observableDirection){
        observableSize.registerObserverSize(this);          //регистрируемся как наблюдатели размеров окна
        observableDirection.registerObserverDirection(this); //регистрируемся как наблюдатели действий пользователя
        listObservers = new LinkedList<>();     //данная коллекция будет используется для оповещения наблюдателей
        this.widthScene = scene.getWidth();     //записывает ширину сцены
        this.heightScene = scene.getHeight();   //записываем высоту сцены
        this.currentStateDirection = Direction.DOWN;    //по умолчанию определяем направление листания как ВНИЗ

        this.storageRow = new StorageRowGlyph();    //инизиализируем хранилище строк по умолчанию
        this.preparerRowGlyph = new PreparerRowGlyph(model, this.storageRow, scene);//инициализируем объект подготовки строк

        this.registerObserverSize(this.preparerRowGlyph);   //регистрируем объект-подготовитель как наблюдателя размеров
        //сцена
        this.registerObserverSize(storageRow); //регистрируем хранилище строк как наблюдателя размеров сцены

        this.currentStartRowPosition = 0;       //начальная позиция набора строк для отрисовки
        this.currentStartSubRowPosition = 0;    //начальная позиция набора подстрок для отрисовки
        this.minLine = 0;                       //минимальный номер строки, другими словами 0
        this.maxLine = model.size();            //запрашиваем у модели количество строк
        this.listPage = new LinkedList<>();     //создаём коллекцию в которую будем набирать строки для отрисовки
        this.pane = pane;                       //запоминаем панель компоновки
        constructPage();                        //обращение к методу начала набора строк
        drawPage();                             //обращение к методу отрисовки набранных строк
    }


    /**
     * метод должен найти номер строки и подстроки с которой начнётся набор страницы.
     * метод проверяет строку и подстроку на валидность
     */
    private void findStartPosition(){
        int tempNumberRow = currentStartRowPosition;
        int tempNumberSubRow = currentStartSubRowPosition;

        //если страница должна двигаться вниз
        if (this.currentStateDirection == Direction.DOWN){

            if (storageRow.getRowGlyph(tempNumberRow).getEndNumberSubRow() < tempNumberSubRow){
                tempNumberRow++;
                tempNumberSubRow = 0;
            }

            //если после проверок номер строки достиг предела максимальной строки из model, то
            //находим предыдущую строку и узнаём у неё номер максимальной подстроки
            if (tempNumberRow >= maxLine){
                tempNumberRow--;
                tempNumberSubRow = storageRow.getRowGlyph(tempNumberRow).getEndNumberSubRow();
            }
        }//конец - если страница должна листаться вниз

        //если страница должна двигаться вверх
        if (this.currentStateDirection == Direction.UP){

            //если подстрока меньше нуля
            if (tempNumberSubRow < 0){
                tempNumberRow--;

                //если строка меньше нуля
                if (tempNumberRow < minLine){
                    tempNumberRow = 0;
                    tempNumberSubRow = 0;
                }
                //если строка больше нуля
                else {
                    tempNumberSubRow = storageRow.getRowGlyph(tempNumberRow).getEndNumberSubRow();
                }
            }
            //подстрока больше нуля
            else {
                //номер подстроки существует
                if (storageRow.getRowGlyph(tempNumberRow).getEndNumberSubRow() >= tempNumberSubRow){

                }
                else {
                    tempNumberSubRow = storageRow.getRowGlyph(tempNumberRow).getEndNumberSubRow();
                }
            }
        }

        //сохраняем пересчитанные номера в переменных
        this.currentStartRowPosition = tempNumberRow;
        this.currentStartSubRowPosition = tempNumberSubRow;
        tempNumberRow = 0;
        tempNumberSubRow = 0;
    }


    /**
     * метод набирает строки в коллекцию строк, что бы их отрисовать
     * набор начинает происходить со стартовых позиций строки и подстроки, и двигаясь дальше от них набирает строки
     * по ширине в коллекцию.
     * Если метод, пройдя по строкам и подстрокам не набрал нужного количества строк по высоте окна, тогда
     * коллекция переворачивается, вновь полдучаем стартовые позиции строки и подстроки, и движение продолжается,
     * но уже в обратном направлении.
     * Коллекция по окончанию набора переворачивается вновь, и новые, пересчитанные, значения строк и подстрок записываются
     * в поля объекта.
     */
    private void constructPage(){

        int tempStartRow = this.currentStartRowPosition;        //номер строки для начала забора строк из хранилища
        int tempStartSubRow = this.currentStartSubRowPosition; //номер подстроки для начала забора строки из хранилища
        double tempHeight = 0;  //эта переменная будет увеличиваться по мере пополнения строками, на их высоту.
        boolean isPagePrepared = false; //флаг готовности строки
        listPage.clear();   //предыдущие значения чистим перед новым набором.

        //сначала прямой обход
        while (storageRow.contains(tempStartRow) & !isPagePrepared){
            ArrayList<ArrayList<Glyph>> rowValue = storageRow.getRowGlyph(tempStartRow).getValue();
            for (int i = tempStartSubRow; i < rowValue.size(); i++) {
                double heightSubRow = getHeightList(rowValue.get(i));
                if ((heightSubRow + tempHeight) <= this.heightScene){
                    listPage.add(rowValue.get(i));
                    tempHeight += heightSubRow;
                }
                else {
                    isPagePrepared = true;
                    break;
                }
            }
            tempStartRow++;
            tempStartSubRow = 0;
        }

        //возвращаем переменным их начальные значения для обратного обхода storage
        tempStartRow = this.currentStartRowPosition;
        tempStartSubRow = this.currentStartSubRowPosition;


        //теперь, если страница по высоте ещё не набралась - делаем обратный ход

        //если строка и подстрока == 0
        if ((tempStartRow == 0) & (tempStartSubRow == 0)){
            isPagePrepared = true;
        }
        else {  //если они указываются на другие строки и подстроки
            tempStartSubRow--;
        }

        //если к этому времени страница ещё не готова.
        if (!isPagePrepared) {

            if (tempStartRow < 0) {
                tempStartRow--;
                tempStartSubRow = storageRow.getRowGlyph(tempStartRow).getEndNumberSubRow();
            }

            Collections.reverse(listPage);
            int i = 0;

            while (storageRow.contains(tempStartRow) & !isPagePrepared) {

                for (i = tempStartSubRow; i >= 0; i--) {
                    ArrayList<ArrayList<Glyph>> rowValue = storageRow.getRowGlyph(tempStartRow).getValue();
                    double heightSubRow = getHeightList(rowValue.get(i));
                    if ((heightSubRow + tempHeight) <= this.heightScene) {
                        listPage.add(rowValue.get(i));
                        tempHeight += heightSubRow;
                    } else {
                        isPagePrepared = true;
                        break;
                    }
                }

                tempStartRow--;
                if (storageRow.contains(tempStartRow)) {
                    tempStartSubRow = storageRow.getRowGlyph(tempStartRow).getEndNumberSubRow();
                }
            }

            //записываем новые значения начальной строки и подстроки
            //т.е. в следующий раз набор строк и подстрок будет происходить из этих позиций.
            this.currentStartRowPosition = tempStartRow + 1;
            this.currentStartSubRowPosition = i;

            //переворачиваем коллекцию.
            Collections.reverse(listPage);
        }
    }


    //узнаём максимальную ширину в строке - O(n)
    private double getHeightList(List<Glyph> list){
        double max = 0.0;
        for (int i = 0; i < list.size(); i++) {
            max = Double.max(max, list.get(i).getHeight());
        }
        return max;
    }

    //регистрируем наблюдателей размеров сцены
    @Override
    public void registerObserverSize(ObserverSize observerSize) {
        listObservers.add(observerSize);
    }

    //чистим наблюдателей размеров сцены
    @Override
    public void clearObserverSize() {
        listObservers.clear();
    }

    //оповещаем наблюдателей размеров сцены
    @Override
    public void notifyObservers() {
        listObservers.forEach(observerSize -> observerSize.update(this.widthScene, this.heightScene));
    }

    /**
     * Поведение на нажатие кнопок пользователем.
     * если листаем вниз - значение подстроки возрастает на 1
     * если вверх - тогда значение подстроки уменьшается на 1
     * @param direction принимается одна из констант перечисления, как действие для листания вверх или вниз
     */
    @Override
    public void update(Direction direction) {
        this.currentStateDirection = direction;

        if (direction == Direction.DOWN){   //если пришло нажатие вниз
            this.currentStartSubRowPosition++;  //подстрока растёт на 1
        }
        else {  //если пришло нажатие вверх
            this.currentStartSubRowPosition--;  //подстрока уменьшается на 1
        }
        //если подготовщик строк - активен - очищаем хранилище и задаём параметры строки и направления для подготовки строк
        if (this.preparerRowGlyph.isActiveObject()){
            this.storageRow.clearStorage();
            this.preparerRowGlyph.preparedRows(this.currentStartRowPosition, this.currentStateDirection);
        }
        findStartPosition(); //т.к. зачение подстроки изменилось - пересчитываем значения
        constructPage();    //собираем строки для отправки на отрисовку
        drawPage(); //отрисовывам строки
    }

    /**
     * метод, определяет действие объекта на изменение ширины и высоты сцены
     * А т.к. зарегистрированные слушатели размеров у этого объекта - это хранилище, которого интересует только ширина
     * сцены, то только при изменении ширины сцены изменения и передаём хранилищу и подготовщику строк
     * А параметр высоты нужен только этому объекту для нового набора строк и их отображения.
     * @param width ширина сцены
     * @param height высота сцены
     */
    @Override
    public void update(double width, double height) {
        //только, если ширина сцены изменилась - идёт оповещение слушателям
        if (width != widthScene){
            this.widthScene = width;
            this.heightScene = height;
            notifyObservers();  //оповещаем случашетей
            findStartPosition();    //ищем стартовые позиции набора строк
            constructPage();        //начинаем собирать строки
            drawPage();             //отрисовываем строки
        } else { //размер сцены изменился, но ширина осталась прежней, а поменялась только высота
            this.heightScene = height;
            findStartPosition();    //ищем стартовые позиции набора строк
            constructPage();        //начинаем собирать строки
            drawPage();             //отрисовываем строки
        }
    }

    /**
     * метод отрисовки строк.
     * Очищаем панель компоновки от предыдущих значений.
     * Циклом проходимся по собранной коллекции строк.
     * Создаём canvas размером со строку и вписываем туда её.
     * После наполнения canvas укладываем в панель компоновки в нужной позиции.
     */
    public void drawPage(){
        pane.getChildren().clear();
        ArrayList<ArrayList<Glyph>> listDraw = new ArrayList<>(this.listPage);
        double panePosY = 0.0;

        for (int i = 0; i < listDraw.size(); i++) {
            ArrayList<Glyph> subListDraw = listDraw.get(i);
            double heightSubRow = getHeightList(subListDraw);

            Canvas canvas = new Canvas(this.widthScene, heightSubRow);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            double canvasPosY = heightSubRow * 0.75;
            double canvasPosX = 0.0;

            for (int j = 0; j < subListDraw.size(); j++) {
                Glyph glyph = subListDraw.get(j);
                gc.setFont(glyph.getFontGlyph());
                gc.fillText(glyph.getValue(), canvasPosX, canvasPosY);
                canvasPosX += glyph.getWidth();
            }

            pane.getChildren().add(canvas);
            canvas.relocate(0.0, panePosY);
            panePosY = panePosY + heightSubRow;
        }
    }
}
