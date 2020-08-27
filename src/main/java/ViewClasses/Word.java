/**
 * Обусловленность класса:
 * Слова, состовляющие строку, нужно хранить. И не только значение( т.е. то, что написано), но и величину шрифта для каждого
 * слова отдельно, а также ширину и высоту блока, в который можно вписать данное слово при отрисовке.
 * Объект данного класса обеспечен методами для предоставления информации об этом слове.
 * Выбор формата размера и, соответственно, типа для переменных, которые хранят соответствующие данные продиктованы методами
 * и классами, которые работают с отрисовкой и шрифтом и canvas в Java Fx.
 */
/**
 * Класс для формирования объекта Word.
 * В нём хранятся значение слова, значение величины шрифта, габариты при отрисовке этого слова.
 */
package ViewClasses;

public class Word {
    private final String VALUEWORD;     //значение слова, т.е. что именно будет написано
    private final double SIZEFONT;      //размер шрифта.
    private final double WIDTHWORD;     //ширина прямоугольника, в который можно вписать данное слово.
    private final double HEIGHTWORD;    //высота прямоугольника, в который можно вписать данное слово.

    /**
     * Конструктор объекта
     * @param value    значение, котороые будет отрисовано в окно
     * @param sizeFont размер шрифта
     * @param width    ширина прямоугольника
     * @param height   высота прямоугольника
     */
    public Word(String value, double sizeFont, double width, double height) {
        this.VALUEWORD = value;
        this.SIZEFONT = sizeFont;
        this.WIDTHWORD = width;
        this.HEIGHTWORD = height;
    }

    //выдача значения слова
    public String getValue() {
        return VALUEWORD;
    }

    //выдача размеров шрифта
    public double getSizeFont() {
        return this.SIZEFONT;
    }

    //ширина прямоугольника
    public double getWidthWord() {
        return WIDTHWORD;
    }

    //высота прямоугольника
    public double getHeightWord() {
        return HEIGHTWORD;
    }
}
