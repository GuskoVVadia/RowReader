/**
 * Класс, реализация интерфейса Model.
 * Данный класс предоставляет строки из файла.
 */
package ModelClasses;

import AppInterfaces.AlertMessage;
import AppInterfaces.Model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReadingModel implements Model {
    private final Path pathUserFile;    //Путь к файлу, который получен, от пользователя.
    private final Charset charsetUserFile;  //Кодировка пользовательского файла.
    private AlertMessage alertMessage;  //Определяем, куда отправлять сообщение при возникновении ошибки
    private long maxRowFromFileUser;    //Переменная максимального количества строк из сущности Model
    private String textRowFromFile;     //Строка, полученная из файла.

    /**
     * Конструктор класса
     * @param pathUserFile    путь к ползовательскому файлу
     * @param charsetUserFile кодировка файла
     * @param alertMessage    интерфейс для отправки ошибки
     */
    public FileReadingModel(Path pathUserFile, Charset charsetUserFile, AlertMessage alertMessage) {
        this.pathUserFile = pathUserFile;
        this.charsetUserFile = charsetUserFile;
        this.alertMessage = alertMessage;
        this.textRowFromFile = "";
        calculateMaxRow();  //вычисляем количество строк из файла пользователя
    }


    /**
     * Метод чтения строки из файла
     * @param position номер строки для чтения
     * @return         возвращает строку
     */
    @Override
    public String getRow(long position){
        textRowFromFile = null;

        try (Stream<String> stream = Files.lines(this.pathUserFile, this.charsetUserFile)){
            textRowFromFile = stream.skip(position).limit(1).collect(Collectors.joining());
        } catch (IOException e){
            this.alertMessage.showError(e);
        }

        return textRowFromFile;
    }

    /**
     * Метод предоставления количества строк из файла.
     * @return количество строк из файла
     */
    @Override
    public long maxRowInFile(){
        return this.maxRowFromFileUser;
    }

    /**
     * Метод вычисления максимального числа строк в файле.
     * При возникновении ошибки - передаёт ошибку в интерфейс AlertMessage.
     * Подсчёт происходит при использовании конструкции try-с-ресурсами, во избежании утечек памяти.
     */
    private void calculateMaxRow(){
        this.maxRowFromFileUser = 0;
        try (Stream<String> stream = Files.lines(this.pathUserFile, this.charsetUserFile)) {
            this.maxRowFromFileUser = stream.count();
        } catch (IOException e) {
            this.alertMessage.showError(e);
        }
    }
}
