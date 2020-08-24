import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Objects;

public class Main extends Application {

    private static Stage primaryStage;
    private static Exception exception;
    private static Charset userFileEncode;
    private static Path userFilePath;

    @Override
    public void start(Stage stage) throws Exception{

        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        loader.load();
        Controller controller = loader.getController();

        if (Objects.nonNull(exception)){
            controller.showError(exception);
        }
        controller.setUserFilePath(userFilePath);
        controller.setUserFileEncode(userFileEncode);

        controller.setStage(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) {

        exception = null;

        try {
            userFilePath = new PathFinder().getPath(args);
            userFileEncode = new TextDetector().getCharset(userFilePath);
        } catch (Exception e){
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