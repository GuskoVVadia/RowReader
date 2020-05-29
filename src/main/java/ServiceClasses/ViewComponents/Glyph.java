/**
 * Класс описывающий поведение основной единицы приложения.
 * Инкапсулирует в себе один символ String, а также данные необходимые для его отрисовки, такие как:
 * шрифт для написания этого символа; размер шрифта и название. В данном случае используется Arial.
 * ширину и высоту блока, в который можно вписать этот символ с определённым шрифтом.
 * Класс содержит методы для определения габаритов написания.
 * Класс также предоставляет свои поля для чтения.
 */

package ServiceClasses.ViewComponents;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Glyph {
    private String value;       //значение сивола
    private double width;       //ширина блока для отрисовки символа
    private double height;      //высота блока для отрисовки символа
    private Font fontGlyph;     //шрифт, используемый для отрировки этого символа

    /**
     * Конструктор, принимающий символ, а также устанавливающий размер шрифта по умолчанию
     * @param symbol единичный символ, типа String
     */
    public Glyph(String symbol) {
        this (symbol, 16.0);
    }

    /**
     * Конструктор, принимающий объект этого же класса, и принимающий символ этого класса, а также устанавливающий
     * размер шрифта по умолчанию
     * @param glyph объект класса
     */
    public Glyph(Glyph glyph){
        this (glyph.value, 16.0);
    }

    /**
     * Конструктор, принимающий параметры, а также устанавливает имя шрифта по умолчанию
     * @param symbol символ
     * @param sizeFont размер шрифта для написания этого символа
     */
    public Glyph(String symbol, double sizeFont){
        this (symbol, sizeFont, "Arial");
    }

    /**
     * Конструктор, принимающий параметры, а также устанавливает имя шрифта по умолчанию
     * @param glyph объект класса
     * @param sizeFont размер шрифта для написания этого символа
     */
    public Glyph(Glyph glyph, double sizeFont){
        this (glyph.value, sizeFont, "Arial");
    }

    /**
     * Конструктор, принимает параметры и запускает метод расчёта размеров написания символа.
     * @param symbol символ
     * @param sizeFont ризмер шрифта
     * @param nameFont имя шрифта
     */
    public Glyph(String symbol, double sizeFont, String nameFont){
        this.value = symbol;
        this.fontGlyph = new Font(nameFont, sizeFont);
        calculateSize();
    }

    /**
     * Метод, для расчёта размеров графического блока, в который можно вписать символ с определённым шрифтом.
     */
    private void calculateSize(){
        Text text = new Text();
        text.setFont(this.fontGlyph);
        text.setText(this.value);
        this.width = text.getBoundsInLocal().getWidth();
        this.height = text.getBoundsInLocal().getHeight();
        text = null;
    }

    //Предоставляет доступ для чтения к переменной, которая хранит символ
    public String getValue() {
        return value;
    }

    //Предоставляем ширину блока
    public double getWidth() {
        return width;
    }

    //Предоставляем высоту блока
    public double getHeight() {
        return height;
    }

    //Предоставляем размер шрифта
    public double getSizeFont(){
        return this.fontGlyph.getSize();
    }

    //Предоставляем шрифт
    public Font getFontGlyph() {
        return fontGlyph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Glyph glyph = (Glyph) o;

        if (Double.compare(glyph.width, width) != 0) return false;
        if (Double.compare(glyph.height, height) != 0) return false;
        if (value != null ? !value.equals(glyph.value) : glyph.value != null) return false;
        return fontGlyph != null ? fontGlyph.equals(glyph.fontGlyph) : glyph.fontGlyph == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = value != null ? value.hashCode() : 0;
        temp = Double.doubleToLongBits(width);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (fontGlyph != null ? fontGlyph.hashCode() : 0);
        return result;
    }
}
