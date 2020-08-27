/**
 * Класс-хранилище строк для приложения.
 * Необходимость класса:
 * Т.к. между Модель предоставляет строки текста, а согласно ТЗ слова в строках должны выглядеть определённым образом, а
 * строки должны реагировать на внешние изменения. Данный класс решает задачу по промежуточному переходу от простых строк
 * из сущности Модель для дальнейших манипуляций.
 * Задачи класса:
 * Набирает строки из актуальной Model (специально указан интерфейс и сделана привязка именно к нему, т.е. нет привязки к
 * конкретному классу. Модель может реализовать как чтение из файла, так и массив строк, ResultSet. Ну, или другая реализация).
 * И преобразует каждую строку к специальной сущности ObserverSizeRow, которая реагирует на события должным образом.
 * Здесь для хранения объектов типа ObserverSizeRow используется структура TreeMap, как подмена Модели. Это сделано
 * с целью уменьшения нагрузки расчётов с какой строки читать и т.д.
 * Кроме того, класс решает задачу мгновенного увеличения строк, т.е. при увеличении размеров окна приложения здесь, в хранилище,
 * уже ждут подготовленные строки. Поэтому хранилище подготавливает "пакет" строк и , по умолчанию, количество подготовленных
 * строк равно 50. При малом файле - значение автоматически пересчитывается.
 * Также хранилище предоставляет логический метод - содержится ли строка с указанным номером в хранилище.
 * Метод очистки хранилища, и метод для преоставления строки с указанным номером.
 */
package ViewClasses;

import AppInterfaces.Model;
import AppInterfaces.ObserverSize;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.TreeMap;

public class RowStorage {

    private Model currentModel; //откуда брать строки
    private TreeMap<Long, ObserverSizeRow> storage;    //где хранить обработанные строки
    private int sizeStorage;    //сколько строк нужно отбирать для хранения
    private Word separator;     //разделитель слов в строке (пробел) Для простоты расчётов пробел - это объект типа Word
    //с размером шрифта 16.0

    /**
     * Конструктор объекта
     * @param currentModel принимает текущую Model. Ссылка указана на интерфейс.
     */
    public RowStorage(Model currentModel) {
        this.currentModel = currentModel;
        this.sizeStorage = (currentModel.maxRowInFile() < 50) ? (int) currentModel.maxRowInFile() : 50; //сколько строк
        //будет считываться в хранилище из Модели
        this.storage = new TreeMap<Long, ObserverSizeRow>();    //инициализация/создание структуры, в которой будут храниться
        // готовые строки

        Text text = new Text(" ");  //создаём объект типа Text для дальнейшего создания объекта-разделителя
        text.setFont(new Font(16.0));   //указываем размер шрифта, для дальнейшего просчёта габаритов раздилителя
        this.separator = new Word(" ", 16.0, text.getBoundsInLocal().getWidth(), text.getBoundsInLocal().getHeight());
        //создание экземпляра объекта-раздилителя по габаритам и с указанным размеров шрифта для отрисовки текста.
    }

    /**
     * Метод для считывания из Модели данных количества строк (указанных в переменной sizeStorage). Строки начинают считываться
     * и собираться начиная со строки под номером startPosition.
     * @param startPosition номер строки с которой насчётся считывание из Модели.
     */
   public void getCalculateRow(long startPosition){

        if ((startPosition + sizeStorage) > currentModel.maxRowInFile()){
            startPosition = (currentModel.maxRowInFile() - sizeStorage);
        }

        String[] tempArray;
        int numberWords = 0;

        for (long i = startPosition; i <= (startPosition + sizeStorage); i++) {
            tempArray = currentModel.getRow(i).split(" ");
            numberWords = getConstructWords(tempArray, numberWords, i);
        }
    }

    /**
     * Метод "разукрашивания" и формирования слов, из которых состоит строка.
     * В метод передаётся номер слова, этот номер в дальнейшем влияет на размер шрифта, которым это слово будет отрисовано.
     * Метод проходится по всей длинне строки, просчитывыет слова - по номеру определяет размер, который будет записан
     * в объект Word. Метод по завершению возвращает номер слова.
     * @param arrayWords  массив слов типа String, полученных из Модели данных методом разбиения строки по пробелу.
     * @param wordsNumber номер слова
     * @param numberRow   номер строки из Модели. Нужен как Key в структуре TreeMap для получения сформированной строки.
     * @return номер слова.
     */
    private int getConstructWords(String[] arrayWords, int wordsNumber, long numberRow){

        ArrayList<Word> arrayList = new ArrayList<>();
        Text text;
        Font font;
        double sizeFont = 0.0;

        for (int i = 0; i < arrayWords.length; i++) {

            if (!(arrayWords[i].equals("") || arrayWords[i].equals(","))) {
                wordsNumber++;
            }

            wordsNumber = (wordsNumber > 4) ? 1 : wordsNumber;
            sizeFont = (wordsNumber > 2) ? 19.0 : 16.0;

            text = new Text(arrayWords[i]);
            font = new Font(sizeFont);
            text.setFont(font);

            arrayList.add(new Word(arrayWords[i], sizeFont, text.getBoundsInLocal().getWidth(),
                    text.getBoundsInLocal().getHeight()));

            text = null;
            font = null;
        }

        storage.put(numberRow, new ObserverSizeRow(this.separator, arrayList));
        arrayList = null;
        return wordsNumber;
    }

    /**
     * @param value номер строки
     * @return true - если строка с таким номером есть в хранилище, и false - в обратном случае.
     */
    public boolean contains(Long value){
        return this.storage.containsKey(value);
    }

    /**
     * @param numberRow номер запрашиваемой строки
     * @return объект типа ObserverSizeRow, в котором инкапсулирована строка.
     */
    public ObserverSizeRow getRow(long numberRow){
        return this.storage.get(numberRow);
    }

    /**
     * Метод очистки хранилища строк.
     */
    public void clear(){
        this.storage.clear();
    }
}
