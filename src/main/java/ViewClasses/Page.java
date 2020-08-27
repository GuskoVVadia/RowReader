/**
 * Класс, реализующий интерфейс View.
 * Представляет собой сущность Вид из схемы MVC.
 * Задача объекта класса: получить из Модели данные и вывести их в окно пользовательского интерфейса; Также
 * реагировать на действия пользователя (события поступающие от контроллера).
 * Класс взаимодействует с Моделью данных приложения, выполняет прорисовку в окне пользователя, а ткже в
 * классе описаны методы для действий на реакцию пользователя.
 */
package ViewClasses;

import AppInterfaces.Model;
import AppInterfaces.ObservableSize;
import AppInterfaces.View;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.LinkedList;

public class Page implements View {

    private RowStorage rowStorage;            //здесь строки хранятся и реагируют на изменения. Это объект-поставщик строк.
    private LinkedList<ArrayList<Word>> page;   //здесь хранятся готовые для отрисовки строки

    private double widthScene;  //переменная ширины окна (сцены в окне)
    private double heightScene; //переменная высоты окна

    private double tempHeightPage;  //переменная для временного хранения значения высоты окна. переменная используется при
    //наборе строк по высоте.
    private boolean fullPage;   //флаг наполнения страницы

    private long numberRow;         //переменная, хранит номер строки из Модели
    private long currentNumberRow;  //переменная, хранит номер строки из Модели на промежуточных этапах
    private int numberSubRow;       //переменная, хранит номер подстроки из хранилища строк
    private int currentNumberSubRow;//переменная, хранит номер подстроки из хранилища строк на промежуточных этапах
    private long maxRowFromFile;       //переменная, хранит количество строк из Модели

    private Group group;    //родительский компонент наполнения страницы
    private Canvas canvas;  //родительский элемент для отрисовки
    private static GraphicsContext graphicsContext; //элемент необходимый для отрисовки в Canvas

    /**
     * Конструктор класса.
     * @param model          Модель данных, т.е. откуда можно брать информацию. Здесь указан интерфейс.
     * @param observableSize интерфейс для регистрации слушателя размеров окна
     * @param parent         Родительский элемент наполнения окна приложения, т.е. куда объект будет производить отрисовку текста
     * @param widthScene     первоначальный параметр ширины Сцены (т.е. ширина окна)
     * @param heightScene    первоначальный параметр высоты Сцены (т.е. высота окна)
     */
    public Page(Model model, ObservableSize observableSize, Group parent, double widthScene, double heightScene) {
        this.maxRowFromFile = model.maxRowInFile();     //узнаём сколько строк содержит текущая Модль данных
        this.rowStorage = new RowStorage(model);        //создаём экземпляр объекта-поставщика строк
        observableSize.registerObserverSize(this);      //регистрируемся как слушатели размеров окна
        this.page = new LinkedList<>();                 //создаём структуру, которая будет хранить подготовленные для отрисовки строки

        this.tempHeightPage = 0.0;      //т.к. пока нет готовых для отрисовки строк, т.е. структура ответственная за это пуста
// переменной присваиваем значение 0.
        this.fullPage = false;  //т.к. пока нет готовых для отрисовки строк - флаг наполнения страницы равен false.

        this.widthScene = widthScene;           //запоминаем ширину страницы
        this.heightScene = heightScene;         //запоминаем высоту страницы
        this.numberRow = this.currentNumberRow = 0L;    //переменные, ответственные за номер строки из Model равны 0
        this.numberSubRow = this.currentNumberSubRow = 0;//переменные, ответственные за номер подстроки из хранилища равны 0

        this.group = parent;    //родительский элемент, предназначенный для наполнения страницы
        this.canvas = new Canvas(); //создаём "полотно" для отрисовки
        graphicsContext = this.canvas.getGraphicsContext2D();   //получаем элемент для отрисовки нашего "полотна"

        rowStorage.getCalculateRow(numberRow);  //пусть хранилище заполняется, начиная с номера строки (который равен 0)
        directReading();    //читаем строки из хранилища обычным (прямым) образом
        pageDraw();     //отдаём в отрисовку прочитанные и просчитанные строки.
    }

    /**
     * Метод "прямого" чтения, т.е. набор и анализ строк, полученных из хранилища строк происходит на нисходящей, а именно,
     * сверху-вниз.
     * Для этого мы запрашиваем номер строки, у неё номер подстроки, полученный результат добавляем к готовым строка в
     * page. Высоту полученной строки, добавляем к высоте временной страницы.
     * Алгоритм увеличивает подстроку и производит чтение. Если у нашей строки закончились подстроки, перескакиваем на другую
     * строку и берём её первую подстроку. Так будет произсодиться чтение пока суммарная высота накопленных строк не достигнет
     * высоты окна.
     * Как только высота окна достигнута, или прочитаны все строки из хранилища - флаг наполнения страницы изменяется на true.
     */
    private void directReading(){
        long i = this.numberRow;
        int j = this.numberSubRow;
        ArrayList<ArrayList<Word>> tempPage;
        double tempHeightRow = 0.0;

        for (; (rowStorage.contains(i) & !fullPage); i++) {
            rowStorage.getRow(i).update(widthScene, heightScene);
            tempPage = rowStorage.getRow(i).getChild();
            currentNumberRow = i;

            for (; (j < tempPage.size() & !fullPage) ; j++) {
                tempHeightRow = getHeightRow(tempPage.get(j));

                if ((tempHeightRow + tempHeightPage) < heightScene){
                    page.add(tempPage.get(j));
                    tempHeightPage += tempHeightRow;
                    currentNumberSubRow = j;
                }
                else {
                    fullPage = true;
                }
            }
            j = 0;
        }
    }

    /**
     * Метод реверсивного (или "обратного") чтения.
     * Набор строк в структуру page происходит снизу-вверх. Т.е. берём строку под номером, берём из ней подстроку и начанием
     * читать, уменьшая значение подстроки. Если у этой строки уже нет подстрок, берём строку выше, берём максимальную подстроку
     * у неё и читаем дальше.
     * Но, в отличии от методо обычного чтения, наполнения в структуру происходит не в конец структуры, а в начало.
     * Флаг наполнения страницы становится true: когда сумма высот строк равна высоте страницы, либо уже была прочитана
     * 0 строка и её 0 подстрока (т.е. мы упёрлись в самое начало текста).
     */
    private void reverseReading(){
        long i = 0L;
        int j = 0;

        if ((numberSubRow - 1) >= 0){
            j = this.numberSubRow - 1;
            i = this.numberRow;
        } else {
            if ((numberRow - 1) >= 0){
                i = numberRow - 1;
                j = rowStorage.getRow(numberRow).getChild().size() - 1;
            } else {
                fullPage = true;
            }
        }

        ArrayList<ArrayList<Word>> tempPage;
        double tempHeightRow = 0.0;

        for (; ((rowStorage.contains(i) & !fullPage) & i >= 0); i--) {
            rowStorage.getRow(i).update(widthScene, heightScene);
            tempPage = rowStorage.getRow(i).getChild();
            currentNumberRow = i;

            for (; (j < tempPage.size() & !fullPage & (j >= 0))  ; j--) {
                tempHeightRow = getHeightRow(tempPage.get(j));

                if ((tempHeightRow + tempHeightPage) < heightScene){
                    page.addFirst(tempPage.get(j));
                    tempHeightPage += tempHeightRow;
                    currentNumberSubRow = j;
                }
                else {
                    fullPage = true;
                }
            }

            if (!fullPage & rowStorage.contains(i - 1)){
                rowStorage.getRow(i - 1).update(widthScene, heightScene);
                j = rowStorage.getRow(i - 1).getChild().size() - 1;
            }
        }

        this.numberRow = this.currentNumberRow;
        this.numberSubRow = this.currentNumberSubRow;
    }

    /**
     * Метод получения значения высоты в строке.
     * Получаем Arraylist значений, проходимя по каждому элементу и запоминаем максимальное значение высоты во всём list'е.
     * @param listWords "строка" Word
     * @return максимальное значение высота Word, хранящихся в "строке"
     */
    private double getHeightRow(ArrayList<Word> listWords){
        double max = 0.0;
        for (Word listWord : listWords) {
            max = Double.max(max, listWord.getHeightWord());
        }
        return max;
    }

    /**
     * Метод для поднятия выводимого текста на одну строку вверх.
     * Номер подстроки уменьшается на 1. Хранилище строк опустошается, после чего даётся команда нового набора строк из Модели.
     * Очищается страница с предыдущими строками. Далее вызывается метод прямого чтения, и если страница не наолнена по высоте,
     * тогда вызываем метод обратного чтения.
     * То, что получилось на выходе - отдаём в отрисовку.
     */
    @Override
    public void lineUp() {
        numberSubRow--;
        rowStorage.clear();
        if (numberRow != 0){
            rowStorage.getCalculateRow(numberRow - 1);
        } else {
            rowStorage.getCalculateRow(numberRow);
        }

        //если подстрока меньше нуля
        if (numberSubRow < 0){
            numberRow--;
            //если строка меньше нуля
            if (numberRow < 0){
                numberRow = 0;
                numberSubRow = 0;
            }
            //если строка больше нуля
            else {
                rowStorage.getRow(numberRow).update(widthScene, heightScene);
                numberSubRow = rowStorage.getRow(numberRow).getChild().size() - 1;
            }
        }
        //подстрока больше нуля

        pageClear();

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    /**
     * Метод для вывода текста на одну строку вниз.
     * Очищаем страницу от прудыдущих результатов.
     * Очищаем хранилище от предыдущих результатов чтения строк из Модели.
     * Задаём в хранилище для чтения новые данные, т.е. номер строки, с какого номера строки начать набор строк из Модели,
     * после чего указываем хранилищу, какие значения высоты и ширины страницы, для подготовки хранилищем строк нужной нам длинны.
     * Далее вызываем метод прямого чтения и, если страница не наполнилась, метод реверсивного чтения.
     * То, что получилось в итоге - передаём на отрисовку.
     */
    @Override
    public void lineDown() {

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(numberRow);
        rowStorage.getRow(numberRow).update(widthScene, heightScene);

        this.numberSubRow++;
        if (rowStorage.getRow(numberRow).getChild().size() <= this.numberSubRow){

            if ((this.numberRow + 1) < this.maxRowFromFile){
                this.numberRow++;
                this.numberSubRow = 0;
            } else {
                this.numberSubRow--;
            }
        }

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    /**
     * Метод для "поднятия" выводимого текта на страницу вверх.
     * Очищаем и страницу и хранилище от предыдущих результатов.
     * Передаём хранилищу для подготовки строк новые, пересчитанные результаты номера строки. Затем производим сначала
     * прямое чтение и, если страница не наполнилась, производим реверсивное. То, что получилось - отдаём на отрисовку
     * в окно пользователя.
     */
    @Override
    public void pageUp() {

        pageClear();
        rowStorage.clear();

        if (this.numberSubRow < 50){
            rowStorage.getCalculateRow(0);
        } else {
            rowStorage.getCalculateRow(this.numberRow - 50);
        }

        if ((this.numberRow == 0) & (this.numberSubRow == 0)) {
            directReading();
        } else {
            reverseReading();
            if (!fullPage) {
                directReading();
            }
        }

        pageDraw();

    }

    /**
     * Метод "опускания" выводимого текста на страницу вниз.
     * Переменная номера строки делаем равной номеру строки из последних результатов чтения.
     * Переменная номера подстроки делаем равной номеру подстроки из последних результатов чтения.
     * Очищаем страницу и хранилище строк от предыдущих результатов. Хранилищу для заполнения строк отдаём новое значение
     * строки.
     * Затем производим сначала прямое чтение и, если страница не наполнилась, производим реверсивное. То, что
     * получилось - отдаём на отрисовку в окно пользователя.
     */
    @Override
    public void pageDown() {

        this.numberRow = this.currentNumberRow;
        this.numberSubRow = this.currentNumberSubRow;

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(this.currentNumberRow);

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();

    }

    /**
     * Метод для реакции на изменение высоты и ширины окна пользователя.
     * Запоминаем ширину и высоту окна.
     * Очищаем предудыщие значения в хранилище строк и страницы, а затем производим чтение из хранилища строк.
     * То, что получилось - отдаём на отрисовку.
     * @param width  ширина окна (сцены)
     * @param height высота окна (сцены)
     */
    @Override
    public void update(double width, double height) {

        this.widthScene = width;
        this.heightScene = height;

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(this.numberRow);
        directReading();

        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    /**
     * Метод отрисовки, т.е. вывод набранных и готовых строк в окно.
     * Для отрисовки очищаем окно от предыдущих результатов. Указываем полотну, в которое производим отрисовку новые габариты.
     * Из структуры, в которую набирали строки для вывода (т.е. page), начинаем производить чтение по слову в строке.
     * Из каждого слова берём размер шрифта для его отрисовки, указываем в отрисовщике значение, тип шрифта и высоту. И т.д.
     * Достигнув конца строки, сдвигаем начало отрисовки следующего слова на высоту предыдущей строки.
     * Как только достигаем конца структура, выходим из цикла.
     * А полотно вставляем в родительский элемент на странице.
     */
    private void pageDraw(){

        group.getChildren().clear();
        this.canvas.setWidth(this.widthScene);
        this.canvas.setHeight(this.heightScene);
        graphicsContext.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        double shift = 0.0;

        for (int i = 0; i < page.size(); i++) {
            ArrayList<Word> subListDraw = page.get(i);
            double heightSubRow = getHeightRow(subListDraw);

            double canvasPosY = heightSubRow + shift;
            double canvasPosX = 0.0;

            for (int j = 0; j < subListDraw.size(); j++) {
                Word word = subListDraw.get(j);
                graphicsContext.setFont(new Font(word.getSizeFont()));
                graphicsContext.fillText(word.getValue(), canvasPosX, canvasPosY);
                canvasPosX += word.getWidthWord();
            }
            shift += heightSubRow;
        }
        group.getChildren().add(this.canvas);
    }

    /**
     * Метод очистки структуры, ответственной за хранение строк для отрисовки.
     * Суммарную высоту строк в структуре указываем равной 0.
     * Флаг наполнения страницы устанавливаем в false.
     * А саму структуру очищаем.
     */
    private void pageClear(){
        this.tempHeightPage = 0.0;
        this.fullPage = false;
        this.page.clear();
    }
}
