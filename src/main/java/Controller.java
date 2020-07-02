/**
 * Задача объекта Контроллера:
 *  запуск слушателей размеров окна и передача изменений по объектам.
 *  запуск слушателей действия пользователя и оповещения объектов
 *  открытие окна с ошибкой, в случае возникновения таковой, и завершение программы.
 *  создание объектов Модели и Вида.
 */

import ServiceClasses.CollectiveInterfaces.ObservableDirection;
import ServiceClasses.CollectiveInterfaces.ObservableSize;
import ServiceClasses.CollectiveInterfaces.ObserverDirection;
import ServiceClasses.CollectiveInterfaces.ObserverSize;
import ServiceClasses.Direction;
import ServiceClasses.ModelComponents.FileReadingModel;
import ServiceClasses.ModelComponents.Model;
import ServiceClasses.ViewComponents.AlertMessage;
import ServiceClasses.ViewComponents.PageView;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Controller implements ObservableSize, ObservableDirection, AlertMessage{

    private static Stage primaryStage;  //контейнер верхнего уровня графического интерфейса пользователя. По сути
    //является "подмостками" для сцены.
    private static Scene primaryScene;  //контейнер графических элементов наполнения окна
    public Group group;

    private LinkedList<ObserverSize> listObserversSize;         //лист наблюдателей размеров
    private LinkedList<ObserverDirection> listObserversDirection;   //лист наблюдателей действий пользователя

    private Direction currentStateDirection;    //текущее действие полльзователя

    private Path filePath;  //путь к файлу, необходимого для Модели программы
    private String encode;  //кодировка файла
    private Exception exceptionFromMain;    //ошибка

    public Controller() {
        this.listObserversDirection = new LinkedList<>();
        this.listObserversSize = new LinkedList<>();
        this.currentStateDirection = null;
    }

    /**
     * Метод наполнения окна
     * @param stage объект определяющий главное окно программы.
     */
    public void setStage(Stage stage){
        Controller.primaryStage = stage;

        primaryScene = new Scene(group, 1000.0, 500.0);
        primaryStage.setScene(primaryScene);

        if (Objects.nonNull(exceptionFromMain)){
            showError(exceptionFromMain);
        }

        if (Objects.isNull(exceptionFromMain)) {
            primaryStage.setTitle("reader row");
            Model model = new FileReadingModel(filePath, this, Charset.forName(encode));
            PageView pageView = new PageView(model, primaryScene, group, this, this);

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
                    currentStateDirection = Direction.UP;
                    notifyObserversDirection();
                }
                if (event.getCode() == KeyCode.DOWN) {
                    currentStateDirection = Direction.DOWN;
                    notifyObserversDirection();
                }
            });
        }
    }

    /**
     * метод добавления наблюдателей действия пользоватяля
     * @param observerDirection наблюдатель за действиями пользователя
     */
    @Override
    public void registerObserverDirection(ObserverDirection observerDirection) {
        listObserversDirection.add(observerDirection);
    }

    /**
     * очистка наблюдателей за действиями пользователя
     */
    @Override
    public void clearObserverDirection() {
        listObserversDirection.clear();
    }

    /**
     * оповещение наблюдателей о действии пользователя
     */
    @Override
    public void notifyObserversDirection() {
        listObserversDirection.forEach(o -> o.update(this.currentStateDirection));
    }

    /**
     * доабавление наблюдателей размеров окна
     * @param observerSize налюдатель размеров окна
     */
    @Override
    public void registerObserverSize(ObserverSize observerSize) {
        listObserversSize.add(observerSize);
    }

    /**
     * очистка списка наблюдателей размеров окна
     */
    @Override
    public void clearObserverSize() {
        listObserversSize.clear();
    }

    /**
     * оповещение наблюдателей размеров окна. Запускается в графическом режиме.
     */
    @Override
    public void notifyObservers() {
        Platform.runLater(() -> {
            listObserversSize.forEach(o -> o.update(primaryScene.getWidth(), primaryScene.getHeight()));
        });
    }

    /**
     * Показ окна с ошибкой с дальнейшем закрытием окна и завершением программы, после уведомления пользователя об
     * ошибке. Уведомление происходит в отдельном окне Alert.
     * @param e ошибка
     */
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

    /**
     * путь к файлу, необходимому для создания объекта model
     * @param filePath путь к файлу
     */
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * кодировка файла, предоставленного пользователем
     * @param encode кодировка, инкапсулированная в String
     */
    public void setEncode(String encode) {
        this.encode = encode;
    }

    /**
     * метод для передачи ошибки
     * @param e ошибка
     */
    public void setException(Exception e){
        this.exceptionFromMain = e;
    }
}
