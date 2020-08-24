/**
 * Класс, реализующий автоопределение кодировки заданного файла.
 * используется алгоритм Apache Any23.
 */

import org.apache.any23.encoding.TikaEncodingDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class TextDetector {

    public TextDetector() {
    }

    /**
     * Отдаём в метод путь к файлу - получаем кодировку файла.
     * @param pathFile отдаём путь к файлу
     * @return получаем кодировку
     * @throws IOException ошибка при работе потоками байтов
     */
    public Charset getCharset(Path pathFile) throws IOException {
        try (InputStream is = new FileInputStream(pathFile.toString())){
            return Charset.forName(new TikaEncodingDetector().guessEncoding(is));
        }
    }
}

