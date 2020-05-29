/**
 * Класс предоставляет реализацию модели данных для приложения.
 * Особенность класса является то, что чтение производится текстового класса с предоставленной кодировкой.
 * Чтение из файла производится путём создания объектов типа Stream.
 */
package ServiceClasses.ModelComponents;

import ServiceClasses.ViewComponents.AlertMessage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReadingModel implements Model {

    private Path pathFile;              //путь к файлу
    private AlertMessage alertMessage;  //интерфейс отправки ошибок
    private Charset charsetName;        //кодировка для чтения файла
    private int maxLine;                //переменная для хранения максимального числа строк из файла. Нужна для
    //сокращения обращений к реальному подсчёту строк из файла, т.к. это операция дорогостоющая по времени и ресурсам.
    //Таким образом в это модели предполагается единичное формирование количества строк, т.е. файл в этой модели
    //неизменен и количество строк у него постоянное.

    /**
     * Конструктор класса
     * @param pathFile путь к файлу
     * @param alertMessage реализация интерфейса AlertMessage - т.е. куда отправлять сообщение об ошибке во время чтения файла
     * @param charsetName в какой кодировки читать файл.
     */
    public FileReadingModel(Path pathFile, AlertMessage alertMessage, Charset charsetName) {
        this.pathFile = pathFile;
        this.alertMessage = alertMessage;
        this.charsetName = charsetName;
        this.maxLine = -1;
    }

    /**
     * Обращамся к файлу и подсчитываем колчество строк в файле.
     * При первом обращении фиксируем колчество строк в переменную maxLine типа int.
     * При последующих обращениях к этому методу - возвращаем значение переменной.
     * Подсчёт происходит в блоке try-catch, чтобы гарантировать завершение потоковых операций.
     * @return количество строк в файле.
     */
    @Override
    public int size() {

        if (this.maxLine < 0) {
            long countFileRow = 0;
            try (Stream<String> stream = Files.lines(pathFile, charsetName)) {
                countFileRow = stream.count();
            } catch (IOException e) {
                alertMessage.showError(e);
            }
            this.maxLine = (int) countFileRow;
            return this.maxLine;
        }
        else{
            return this.maxLine;
        }
    }

    /**
     * Считываем строку из указанной позиции в файле и возвращаем её в виде String.
     * В случае возникновении ошибки - она отправляется в интерфейс.
     * @param position номер строки из файла, откуда будет происходить чтение
     * @return строку из файла типа String
     */
    @Override
    public String getTextRow(int position) {

        String textRow = null;

        try (Stream<String> stream = Files.lines(pathFile, charsetName)){
            textRow = stream.skip(position).limit(1).collect(Collectors.joining());
        } catch (IOException e){
            alertMessage.showError(e);
        }

        return textRow;
    }

    /**
     * Чтение из файла некоторого количества строк.
     * В случае возникновении ошибки - она отправляется в интерфейс.
     * @param position номер строки из файла, как позиции откуда будет происходить чтение
     * @param countRowsRead количество строк для возврата
     * @return строки из файла инкапсулированые в List<String>
     */
    @Override
    public List<String> getTextRows(int position, int countRowsRead) {

        List<String> textRows = null;

        try (Stream<String> stream = Files.lines(pathFile, charsetName)){
            textRows = stream.skip(position).limit(countRowsRead).collect(Collectors.toList());
        } catch (IOException e){
            alertMessage.showError(e);
        }

        return textRows;
    }
}
