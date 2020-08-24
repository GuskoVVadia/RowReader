/**
 * Класс для формирования объекта Word.
 * В нём хранятся значение слова, значение величины шрифта, габариты при отрисовке этого слова.
 */
package ViewClasses;

public class Word {
    private final String VALUEWORD;
    private final double SIZEFONT;
    private final double WIDTHWORD;
    private final double HEIGHTWORD;

    public Word(String value, double sizeFont, double width, double height) {
        this.VALUEWORD = value;
        this.SIZEFONT = sizeFont;
        this.WIDTHWORD = width;
        this.HEIGHTWORD = height;
    }

    public String getValue() {
        return VALUEWORD;
    }

    public double getSizeFont() {
        return this.SIZEFONT;
    }

    public double getWidthWord() {
        return WIDTHWORD;
    }

    public double getHeightWord() {
        return HEIGHTWORD;
    }
}
