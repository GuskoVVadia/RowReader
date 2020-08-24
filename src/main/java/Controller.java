import AppInterfaces.*;
import ModelClasses.FileReadingModel;
import ViewClasses.Page;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Controller implements ObservableSize, AlertMessage {
    public Group group;
    private static Stage primaryStage;
    private static Scene primaryScene;

    private LinkedList<ObserverSize> listObserverSize;

    private Path userFilePath;
    private Charset userFileEncode;

    public Controller(){
        this.listObserverSize = new LinkedList<>();
    }

    public void setStage(Stage stage) {

        Controller.primaryStage = stage;

        primaryScene = new Scene(group, 1000.0, 500.0);
        primaryStage.setScene(primaryScene);

        Model model = new FileReadingModel(userFilePath, userFileEncode, this);
        View view = new Page(model, this, group, 1000.0, 500.0);


        //создаём массив и заполняем координаты точки
        BlockingQueue<Point2D> dimensionChangeQueue = new ArrayBlockingQueue<>(1);

        //слушатель для изменение размеров окна, изменения инкапсулируются в массиве
        ChangeListener<Number> dimensionChangeListener = new ChangeListener<Number>() {
            final Timer timer = new Timer();
            TimerTask task = null;
            final long delayTime = 1000;

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        dimensionChangeQueue.clear();
                        dimensionChangeQueue.add(new Point2D(primaryScene.getWidth(), primaryScene.getHeight()));
                    }
                };
                timer.schedule(task, delayTime);
            }
        };

        //запуск слушателей для размерности окна
        primaryScene.widthProperty().addListener(dimensionChangeListener);
        primaryScene.heightProperty().addListener(dimensionChangeListener);

        //запуск отдельного потока обновления массива
        Thread processDimensionChangeThread = new Thread(() -> {
            while (true) {
                try {
                    dimensionChangeQueue.take();
                    notifyObservers();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        });

        processDimensionChangeThread.setDaemon(true);
        processDimensionChangeThread.start();

        //запуск слушателей клавиатуры
        primaryScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                view.lineUp();
            }
            if (event.getCode() == KeyCode.DOWN) {
                view.lineDown();
            }
            if (event.getCode() == KeyCode.PAGE_DOWN){
                view.pageDown();
            }

            if (event.getCode() == KeyCode.PAGE_UP){
                view.pageUp();
            }

        });

    }

    public void setUserFilePath(Path userFilePath) {
        this.userFilePath = userFilePath;
    }

    public void setUserFileEncode(Charset userFileEncode) {
        this.userFileEncode = userFileEncode;
    }

    @Override
    public void showError(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setWidth(500);
        alert.setHeight(800);

        VBox vBoxInAlert = new VBox();
        Label labelNameError = new Label(e.getMessage());

        TextArea textArea = new TextArea();

        StringBuilder sb = new StringBuilder();
        Arrays.stream(e.getStackTrace()).forEach(m -> sb.append(m + "\n"));
        textArea.appendText(sb.toString());

        vBoxInAlert.getChildren().addAll(labelNameError, textArea);
        alert.getDialogPane().setContent(vBoxInAlert);
        alert.showAndWait();

        primaryStage.close();
        Platform.exit();

    }

    @Override
    public void registerObserverSize(ObserverSize observerSize) {
        this.listObserverSize.add(observerSize);
    }

    @Override
    public void notifyObservers() {
        Platform.runLater(() -> {
            listObserverSize.forEach(o -> o.update(primaryScene.getWidth(), primaryScene.getHeight()));
        });
    }
}
