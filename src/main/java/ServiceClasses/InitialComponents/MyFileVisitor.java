/**
 * Класс описывает поведение при проходе по файлам.
 * Расширяет класс SimpleFileVisitor для работы с методом walkFileTree. При проходе по файлам, предоставляемых методом
 * должен свериться с именем искомого файла и сбросить дальнейший поиск, или пропустить поиск в папке при
 * ошибке доступа.
 */

package ServiceClasses.InitialComponents;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.regex.Pattern;

public class MyFileVisitor extends SimpleFileVisitor<Path> {
    private String regex;
    private Path pathToSearchFile;


    /**
     * Собираем имя файла. Проходим по массиву и собираем имя.
     * @param args массив, в которой инкапсулировано имя файла.
     */
    public MyFileVisitor(String[] args){
        super();
        this.pathToSearchFile = null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1){
                stringBuilder.append(args[i]);
            }
            else {
                stringBuilder.append(args[i] + "\\s+");
            }
        }
        this.regex = stringBuilder.toString();
    }

    /**
     * Метод, определяет поведение метода walkFileTree при проходе по файлу.
     * В нашем случае - проход по файлу разрешён, проход происходит без ошибок и доступ к файлу есть.
     * В это случае мы сверяем имя файла с именем, полученным от пользователя в виде сверки с регулярным выражением.
     * При идентичности записи - путь сохраняется, а дальнейшие проходы сбрасываются.
     * При отсутствии идентичности - указываем, что файл был посещён и ждём следующего файла.
     * @param path путь к файлу
     * @param fileAttributes атрибуты файла
     * @return возвращаем одну из констант:
     *  файл был посещён - в случае, если имена не совпали
     *  окончание посещений - при совпадении имени файла от пользователя и посещённого файла.
     */
    @Override
    public FileVisitResult visitFile(Path path,
                                     BasicFileAttributes fileAttributes) {

        if (Pattern.matches(regex, path.getFileName().toString())){
            this.pathToSearchFile = path;
        }

        if (Objects.isNull(pathToSearchFile)){
            return FileVisitResult.CONTINUE;
        } else {
            return FileVisitResult.TERMINATE;
        }
    }

    //метод описывает, что делать с файлом, в случае возникновения ошибки.
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.SKIP_SIBLINGS;
    }

    //метод, возвращающий путь к файлу
    public Path getPathToSearchFile() {
        return pathToSearchFile;
    }
}
