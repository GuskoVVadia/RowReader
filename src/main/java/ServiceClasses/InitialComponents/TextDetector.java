/**
 * Класс, реализующий алгоритм и классы по автоподбору кодировки текстового файла.
 * За основу взят более новый алгоритм от org.mozilla и класс UniversalDetector.
 */

package ServiceClasses.InitialComponents;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class TextDetector {

    public TextDetector() {
    }

    /**
     * Метод получает путь к файлу и запускает побайтовое чтение данных из файла.
     * Чтение производится в массив buf, который передаётся в объект типа UniversalDetector для определения кодировки
     * текстового файла.
     * @param path путь к файлу
     * @return кодировку файла в виде String
     * @throws IOException ошбика доступа к файлу.
     */
    public String findEncoding(Path path) throws IOException {

        byte[] buf = new byte[1_000];
        String fileName = path.toString();
        String encoding = null;

        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {

            UniversalDetector detector = new UniversalDetector(null);

            int nread;
            while ((nread = fileInputStream.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }

            detector.dataEnd();

            encoding = detector.getDetectedCharset();
        } catch (IOException e){
            throw e;
        }
        return encoding;
    }

}

