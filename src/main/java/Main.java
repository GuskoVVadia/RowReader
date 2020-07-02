/**
 * Стартовый класс для запуска программы.
 * Задача:
 *  корректно обработать полученные от пользователя данные, за это отвечают два объекта
 *  в случае возникновения ошибок при обработке - отправить ошибку для показа пользователю
 *  в случае успеха - передать данные полученные при обработке дальше, т.е. контроллеру, и запустить графический
 *  интерфейс пользователя.
 */

import ServiceClasses.InitialComponents.FileFinder;
import ServiceClasses.InitialComponents.TextDetector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class Main extends Application {

    private static Stage primaryStage;
    private static Exception exception;
    private static String fileEncode;
    private static Path filePath;

    /**
     * Метод ответственный за запуск графического интерфейса
     * @param stage объект-основа для создания графического интерфейса
     * @throws IOException ошибка при отстсутствии fxml файла, где описаны свойства сцены
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        loader.load();
        Controller controller = loader.getController();


        controller.setFilePath(filePath);
        controller.setEncode(fileEncode);
        controller.setException(exception);

        controller.setStage(primaryStage);
        primaryStage.show();

    }

    /**
     * данные полученные от пользователя инкапсулированы в массиве типа String.
     * для поиска файла и составления пути - ответственен класс FileFinder.
     * для определения кодировки, найденного файла - ответственнен класс TextDetector
     * @param args данные пользователя
     */
    public static void main(String[] args){
        exception = null;
        try {
            Main.filePath = new FileFinder().getPath(args);
            Main.fileEncode = TextDetector.getCharset(Main.filePath).toString();

        }catch (Exception e){
            exception = e;
        }

        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        primaryStage.close();
        System.exit(0);
    }
}
