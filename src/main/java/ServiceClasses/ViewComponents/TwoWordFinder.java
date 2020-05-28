/**
 * Класс, для построения объекта, отвечающего за поиск двух слов в строках, и для их изменения.
 * По замыслу, что бы минимизировать количество объектов Glyph'ов, это объект содержит собственную карту.
 * Т.е. при необходимости изменения некоторой последовательности Glyph'ов - объект заменяет их значениями
 * из внутренней специальной карты, тем самым не порождая новые объекты.
 * При прохождении по словам из строки - объект подсчитывает номер слов. После прохождения по строке - номер
 * последнего слова запонимается и отправляется в коллекцию numbersRowWords, если такой записи ещё не было.
 */

package ServiceClasses.ViewComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwoWordFinder {

    private Map<Glyph, Glyph> mapChange;            //коллекция объектов для замены
    private ArrayList<Byte> numbersRowWords;        //коллекция для хранения последнего номера слова из предыдущей строки
    private double incrementFontGlyph;              //величина, на которую нужно произвести увеличение шрифта при изменении

    /**
     * Конструктор, предоставляет значение по умолчанию, величина увеличения шрифта в найденных словах юудет производиться
     * на 3.0 единицы.
     * Внутренние коллекции формируются с небольшой вместимостью. Хотелось бы достичь оптимального решения и снизить
     * ресурсы при увеличении размеров коллекции.
     */
    public TwoWordFinder(){
        this (3.0);
    }
    public TwoWordFinder(double increment){
        this.incrementFontGlyph = increment;
        this.mapChange = new HashMap<>(200);
        this.numbersRowWords = new ArrayList<>(100);
    }

    /**
     * метод поиска слов в объекте RowGlyph
     * @param rowGlyph объект, в котором нужно произвести поиск
     */
    public void findWords(RowGlyph rowGlyph){
        int number = rowGlyph.getNumber();
        byte numberWord;
        //если строка нулевая
        if (number == 0){
            numberWord = -1;
        }else {
            numberWord = numbersRowWords.get(number - 1);
        }
        for (int i = 0; i < rowGlyph.getChild().size(); i++) {

            //если мы сейчас проходим не по пробелу
            if (!rowGlyph.getChild().get(i).get(0).getValue().equals(" ")){
                numberWord++; //увеличиваем номер слова
                if (numberWord > 3){
                    numberWord = 0;
                }
                if (numberWord >= 2){
                    changeFont(rowGlyph.getChild().get(i));
                }
            }



        }//конец цикла обхода row
        if (numbersRowWords.size() <= number){
            numbersRowWords.add(number, numberWord);
        }
    }

    /**
     * метод подмены одной единицы Glyph'а, на аналогичную, но с увеличенным шрифтом.
     * Если искомая замена есть - отдаём, если же нет - тогда строким новую, сохраняем и отдаём.
     * @param list коллекция Glyph'ов, которые нужно подменить.
     */
    private void changeFont(ArrayList<Glyph> list){
        for (int i = 0; i < list.size(); i++) {
            Glyph tempGlyph = list.get(i);
            double sizeFont = tempGlyph.getSizeFont() + incrementFontGlyph;
            list.set(i, mapChange.computeIfAbsent(tempGlyph, glyph -> new Glyph(glyph, sizeFont)));
        }
    }

}
