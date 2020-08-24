/**
 * Поиск пути и проверка на существование файла.
 * Метод объекта ожидает либо имя файла, либо абсолютный путь к файлу, с последующей проверкой существования
 * файла по данному пути.
 * Возвращает путь к файлу, либо кидает ошибку.
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PathFinder {

    public PathFinder() {
    }

    /**
     * Метод поиска пути.
     * 1. Проверяем args на абсолютный путь/имя файла.
     * 2. Если указано имя файла - получаем путь к jar файлу
     * @param args данные введённые пользователем при запуске приложения
     * @return путь к к файлу
     * @throws Exception имя файла/путь не введён / указанный файл отсутствует
     */
    public Path getPath(String[] args) throws Exception{

        //проверка args нулевую длинну
        if (args.length < 1){
            throw new Exception("Имя файла или путь к файлу не указаны.");
        }

        /**
         * если получили имя файла - проверяем,
         * иначем - получили абсолютный путь - проверяем.
         */
        //TODO будь внимателен к имени класс в поиске пути - pathDir
        if (!args[0].contains(File.separator)){
            Path pathDir = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path pathUserFile = Paths.get(pathDir.toString() + File.separator + args[0]);
            if (!Files.exists(pathUserFile)){
                throw new Exception("Имя файла указано неверно.");
            } else {
                return pathUserFile;
            }
        } else {
            Path pathUserFile = Paths.get(args[0]);
            if (Files.isDirectory(pathUserFile)){
                throw new Exception("Вы указали путь к папке.");
            }
            if (!Files.exists(pathUserFile)){
                throw new Exception("Путь к файлу указан неверно.");
            }
            return pathUserFile;
        }
    }
}
