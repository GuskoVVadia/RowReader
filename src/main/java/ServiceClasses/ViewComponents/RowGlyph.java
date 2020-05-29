/**
 * Класс, отвечающий за построение объектов-строк Glyph'ов.
 * Его задача:
 *  получив строку String, разбитую на слова - эти слова разбить и перевести в Glyph.
 *  хранить последовательность слов.
 *  а при извещении об изменении размеров сцены - составить отдельную новую последовательность так, что бы она
 * отвечала требованиям ТЗ.
 *  хранить номер строки из Модели.
 */

package ServiceClasses.ViewComponents;

import ServiceClasses.CollectiveInterfaces.ObserverSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowGlyph implements ObserverSize {
    private int numberRowInModel;                       //номер строки из Модели
    private ArrayList<ArrayList<Glyph>> child;          //слова из строки Модели
    private ArrayList<ArrayList<Glyph>> value;          //перестроенные строки прошедшие update для отдачи на отрисовку
    private Glyph delimiterWord;                        //глиф-разделитель слов, по умолчанию выставлен пробел
    private double widthScene;  //в переменной храниться значение ширины сцены, с которым её последний раз изменяли в update
    private int endNumberSubRow;    //номер последней подстроки из коллекции value, т.е. представления строки под определённую
    //ширину.

    /**
     * Конструктор, по умолчанию опеределяет символ-разделитель как пробел.
     * @param number      номер строки из Модели
     * @param mapRowBuild карта, которая хранит и поставляет Glyph'ы
     * @param words       слова из строки, что пришла из Модели.
     */
    public RowGlyph(int number, Map<String, Glyph> mapRowBuild, String ... words){
        this (number, mapRowBuild, " ", words);
    }

    /**
     * Конструктор
     * @param number      номер строки из Модели
     * @param mapRowBuild карта, которая хранит и поставляет Glyph'ы
     * @param delimiter   символ-разделитель слов
     * @param words       слова из строки, что пришла из Модели.
     */
    public RowGlyph(int number, Map<String, Glyph> mapRowBuild, String delimiter, String ... words){
        this.numberRowInModel = number;
        this.child = new ArrayList<>();
        this.value = new ArrayList<>();
        this.widthScene = 0.0;
        this.endNumberSubRow = -1;  //при инициализации объекта коллекция значений пока не построена, и номер последней
        // строки из коллекции пока не известен, поэтому присваиваем отрицательное значение.
        buildRowFromMap(delimiter, mapRowBuild, words);//построение внутреннег массива слов
    }

    //сборка строки из String[] массива слов в list child через карту-поставщика Glyph'ов
    private void buildRowFromMap(String delimiter, Map<String, Glyph> map, String ... words){
        ArrayList<Glyph> wordList = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            String[] word = words[i].split("");
            for (int j = 0; j < word.length; j++) {
                String symbol = word[j];
                if (symbol.equals("")){
                    symbol = delimiter;
                }
                wordList.add(map.computeIfAbsent(symbol, s -> new Glyph(s, 16)));
            }
            this.child.add(wordList);
            wordList = new ArrayList<>();
        }
        this.delimiterWord = map.computeIfAbsent(delimiter, s -> new Glyph(s, 16));
    }

    //перестроение строки по уведомлению
    @Override
    public void update(double width, double height) {
        if (widthScene != width) {
            value = new ArrayList<>();  //создание нового списка для новых строк

            ArrayList<Glyph> tempRow = new ArrayList<>();
            ArrayList<Glyph> tempWord = new ArrayList<>();
            double widthTempRow = 0.0;
            double widthTempWord;

            for (int i = 0; i < child.size(); i++) {

                tempWord.clear();
                tempWord.addAll(child.get(i));

                if (i != child.size() - 1) {
                    if (!tempWord.get(0).getValue().equals(" ")) {
                        tempWord.add(delimiterWord);
                    }
                }

                widthTempWord = getWidthList(tempWord); //получаем ширину слова

                if ((widthTempRow + widthTempWord) < width) {//если слово можно вставить в строку
                    tempRow.addAll(tempWord);
                    widthTempRow += widthTempWord;
                    widthTempWord = 0.0;
                    tempWord = new ArrayList<>();
                } else {  //если слово нельзя вставить в строку
                    if (widthTempRow > 0) {  //если в строке уже есть элементы, то переменную строку делаем постоянной и обновляем её.
                        value.add(tempRow);
                        widthTempRow = 0.0;
                        tempRow = new ArrayList<>();
                    }
                    //опять проверяем - можно ли положить слово в строку?
                    if ((widthTempRow + widthTempWord) > width) { //слово не помещяется в строку
                        //разбивает слово на символы
                        for (int j = 0; j < tempWord.size(); j++) {
                            //получаем символ из слова
                            Glyph symbol = tempWord.get(j);
                            //в строку поместиться символ?
                            if (widthTempRow + symbol.getWidth() < width) {  //если да
                                tempRow.add(symbol);
                                widthTempRow += symbol.getWidth();
                            } else {  //нет - не поместиться
                                value.add(tempRow);
                                tempRow = new ArrayList<>();
                                tempRow.add(symbol);
                                widthTempRow = symbol.getWidth();
                            }
                        } //конец цикла - разбивка слова на символы
                    } else {  //слово помещается в строку
                        tempRow.addAll(tempWord);
                        widthTempRow += widthTempWord;
                        widthTempWord = 0.0;
                        tempWord = new ArrayList<>();
                    }
                }//конец - если слово в строку вставить нельзя
            }//конец цикла

            if (widthTempRow > 0) {
                value.add(tempRow);
            }
            this.widthScene = width;
            this.endNumberSubRow = this.value.size() - 1;
        }

    } //end method update

    // получение максимального значения высоты в строке
    private double getWidthList(List<Glyph> list){
        double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getWidth();
        }
        return sum;
    }

    //используется для поиска слов
    public int getNumber() {
        return numberRowInModel;
    }

    //используется для поиска двух слов
    public ArrayList<ArrayList<Glyph>> getChild(){
        return child;
    }

    //предоставление перестроенной коллекции значений.
    public ArrayList<ArrayList<Glyph>> getValue(){
        return value;
    }

    //предоставление номера последней строки из подстрок в value.
    public int getEndNumberSubRow() {
        return endNumberSubRow;
    }
}
