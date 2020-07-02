/**
 * Задача класса - поиск файла в файловой системе:
 * В случае ошибки - метод выкидывает ошибку с сообщением.
 * В случае нахождения файла по указанному пути - метод getPath() отправляет путь к файлу.
 */

package ServiceClasses.InitialComponents;

import java.nio.file.*;

public class FileFinder {

    public FileFinder() {
    }

    /**
     * Здесь нужно распарсить args и получить путь из папки запуска + имя файла, заданного пользователем.
     * @param nameFile по факту args из main
     * @return путь к файлу
     */
    public Path getPath(String[] nameFile) throws Exception {
        Path outFilePath = null;    //путь, который будет получен в итоге
        StringBuilder stringBuilder = new StringBuilder(); // аккумулирует в себе значения из nameFiles, он же args
        Path pathToJarAndName = null; //путь к jar файлу.

        /**
         * получаем стринговый путь к jar, путь включает в себя и имя джарника.
         * парсим, что бы сбить первый слэш
         * Сохраням в переменную и выводим в console
         */
        String s = FileFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath().split("/", 2)[1];
        pathToJarAndName = Paths.get(s);

        /**
         * Если у нас не указано никакого имени файла - кидаем ошибку
         */
        if (nameFile.length == 0) throw new Exception("no file name.");

        /**
         * если user добавил имя файла/путь к файлу - сливаем все значения в одно стринговое stringBuilder
         */

        if (nameFile.length > 1) {
            stringBuilder.append(nameFile[0]);
            for (int i = 1; i < nameFile.length; i++) {
                stringBuilder.append(" ").append(nameFile[i]);
            }
        }
        else {
            stringBuilder.append(nameFile[0]);
        }

        //stringBuilder - теперь у нас носитель строки значений, что передал пользователь

        /**
         * ----------------------------------------------------------------------------------------------
         * Здесь начинается процесс разбиения user текста на части, что бы понять с чем мы имеем дело.
         * ----------------------------------------------------------------------------------------------
         */
        String stringPath = stringBuilder.toString();

        if (stringPath.split(":").length > 1){
            outFilePath = Paths.get(stringPath);
        }
        //разбираемся дальше - ветка не абсолютного пути
        else {
            //здесь у нас относительный путь
            if (stringPath.split("\\\\").length > 1 ){
                Path userRelativePath = Paths.get(stringPath);      //получаем путь с именем файла
                outFilePath = pathToJarAndName.getParent().resolve(userRelativePath);
            }
            //очевидно, передано только имя файла
            else {
                outFilePath = pathToJarAndName.getParent().resolve(stringPath);
            }
        }

        if (!Files.exists(outFilePath)){
            throw new Exception("По указанному пути файл не найден: " + outFilePath);
        }

        return outFilePath;
    }




}
