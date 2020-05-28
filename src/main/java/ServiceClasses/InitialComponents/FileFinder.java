/**
 * Задача класса - поиск файла в файловой системе.
 */

package ServiceClasses.InitialComponents;

import java.nio.file.*;
import java.util.Iterator;
import java.util.Objects;

public class FileFinder {

    public FileFinder() {
    }

    /**
     * Метод, обращается к файловой системе, запрашивает пути корневых каталогов системы, проходится по ним в
     * попытке найти файл.
     * Для прохода по корневым каталогам - используется статический метод из класса Files.
     * За сопоставление имени файла и за его формирование - отвечает класс MyFileVisitor.
     * @param nameFile имя файла, инкапсулированное в массив типа String
     * @return путь к найденному файлу
     * @throws Exception ошибку, если файл найден не был, т.е. при работе метода поиска мы получили null путь.
     */
    public Path getPath(String[] nameFile) throws Exception {

        FileSystem fs = FileSystems.getDefault();
        Iterator<Path> ip = fs.getRootDirectories().iterator();
        Iterator<FileStore> ifStores = fs.getFileStores().iterator();
        MyFileVisitor mfv = new MyFileVisitor(nameFile);

        while (ip.hasNext()){
            Path pathTemp = ip.next();
            if (Files.exists(pathTemp)){
                    Files.walkFileTree(pathTemp, mfv);
            }
        }

        Path path = mfv.getPathToSearchFile();
        if (Objects.isNull(path)){
            throw new RuntimeException("File not found");
        }

        return mfv.getPathToSearchFile();
    }
}
