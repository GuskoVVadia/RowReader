package ViewClasses;

import AppInterfaces.Model;
import AppInterfaces.ObservableSize;
import AppInterfaces.View;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.LinkedList;

public class Page implements View {

    private RowStorage rowStorage;
    private LinkedList<ArrayList<Word>> page;

    private double widthScene;
    private double heightScene;

    private double tempHeightPage;
    private boolean fullPage;

    private long numberRow;
    private long currentNumberRow;
    private int numberSubRow;
    private int currentNumberSubRow;
    private long maxRowFromFile;

    private Group group;
    private Canvas canvas;
    private static GraphicsContext graphicsContext;


    public Page(Model model, ObservableSize observableSize, Group parent, double widthScene, double heightScene) {
        this.maxRowFromFile = model.maxRowInFile();
        this.rowStorage = new RowStorage(model);
        observableSize.registerObserverSize(this);
        this.page = new LinkedList<>();

        this.tempHeightPage = 0.0;
        this.fullPage = false;

        this.widthScene = widthScene;
        this.heightScene = heightScene;
        this.numberRow = this.currentNumberRow = 0L;
        this.numberSubRow = this.currentNumberSubRow = 0;

        this.group = parent;
        this.canvas = new Canvas();
        graphicsContext = this.canvas.getGraphicsContext2D();

        rowStorage.getCalculateRow(numberRow);
        directReading();
        pageDraw();
    }

    private void directReading(){
        long i = this.numberRow;
        int j = this.numberSubRow;
        ArrayList<ArrayList<Word>> tempPage;
        double tempHeightRow = 0.0;

        for (; (rowStorage.contains(i) & !fullPage); i++) {
            rowStorage.getRow(i).update(widthScene, heightScene);
            tempPage = rowStorage.getRow(i).getChild();
            currentNumberRow = i;

            for (; (j < tempPage.size() & !fullPage) ; j++) {
                tempHeightRow = getHeightRow(tempPage.get(j));

                if ((tempHeightRow + tempHeightPage) < heightScene){
                    page.add(tempPage.get(j));
                    tempHeightPage += tempHeightRow;
                    currentNumberSubRow = j;
                }
                else {
                    fullPage = true;
                }
            }
            j = 0;
        }
    }

    private void reverseReading(){
        long i = 0L;
        int j = 0;

        if ((numberSubRow - 1) >= 0){
            j = this.numberSubRow - 1;
            i = this.numberRow;
        } else {
            if ((numberRow - 1) >= 0){
                i = numberRow - 1;
                j = rowStorage.getRow(numberRow).getChild().size() - 1;
            } else {
                fullPage = true;
            }
        }

        ArrayList<ArrayList<Word>> tempPage;
        double tempHeightRow = 0.0;

        for (; ((rowStorage.contains(i) & !fullPage) & i >= 0); i--) {
            rowStorage.getRow(i).update(widthScene, heightScene);
            tempPage = rowStorage.getRow(i).getChild();
            currentNumberRow = i;

            for (; (j < tempPage.size() & !fullPage & (j >= 0))  ; j--) {
                tempHeightRow = getHeightRow(tempPage.get(j));

                if ((tempHeightRow + tempHeightPage) < heightScene){
                    page.addFirst(tempPage.get(j));
                    tempHeightPage += tempHeightRow;
                    currentNumberSubRow = j;
                }
                else {
                    fullPage = true;
                }
            }

            if (!fullPage & rowStorage.contains(i - 1)){
                rowStorage.getRow(i - 1).update(widthScene, heightScene);
                j = rowStorage.getRow(i - 1).getChild().size() - 1;
            }
        }

        this.numberRow = this.currentNumberRow;
        this.numberSubRow = this.currentNumberSubRow;
    }

    private double getHeightRow(ArrayList<Word> listWords){
        double max = 0.0;
        for (Word listWord : listWords) {
            max = Double.max(max, listWord.getHeightWord());
        }
        return max;
    }

    @Override
    public void lineUp() {

        numberSubRow--;

        rowStorage.clear();
        if (numberRow != 0){
            rowStorage.getCalculateRow(numberRow - 1);
        } else {
            rowStorage.getCalculateRow(numberRow);
        }

        //если подстрока меньше нуля
        if (numberSubRow < 0){
            numberRow--;

            //если строка меньше нуля
            if (numberRow < 0){
                numberRow = 0;
                numberSubRow = 0;
            }
            //если строка больше нуля
            else {
                rowStorage.getRow(numberRow).update(widthScene, heightScene);
                numberSubRow = rowStorage.getRow(numberRow).getChild().size() - 1;
            }
        }
        //подстрока больше нуля

        pageClear();

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    @Override
    public void lineDown() {

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(numberRow);
        rowStorage.getRow(numberRow).update(widthScene, heightScene);

        this.numberSubRow++;
        if (rowStorage.getRow(numberRow).getChild().size() <= this.numberSubRow){

            if ((this.numberRow + 1) < this.maxRowFromFile){
                this.numberRow++;
                this.numberSubRow = 0;
            } else {
                this.numberSubRow--;
            }
        }

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    @Override
    public void pageUp() {

        pageClear();
        rowStorage.clear();

        if (this.numberSubRow < 50){
            rowStorage.getCalculateRow(0);
        } else {
            rowStorage.getCalculateRow(this.numberRow - 50);
        }

        if ((this.numberRow == 0) & (this.numberSubRow == 0)) {
            directReading();
        } else {
            reverseReading();
            if (!fullPage) {
                directReading();
            }
        }

        pageDraw();

    }

    @Override
    public void pageDown() {

        this.numberRow = this.currentNumberRow;
        this.numberSubRow = this.currentNumberSubRow;

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(this.currentNumberRow);

        directReading();
        if (!fullPage){
            reverseReading();
        }

        pageDraw();

    }

    @Override
    public void update(double width, double height) {

        this.widthScene = width;
        this.heightScene = height;

        pageClear();
        rowStorage.clear();
        rowStorage.getCalculateRow(this.numberRow);
        directReading();

        if (!fullPage){
            reverseReading();
        }

        pageDraw();
    }

    private void pageDraw(){

        group.getChildren().clear();
        this.canvas.setWidth(this.widthScene);
        this.canvas.setHeight(this.heightScene);
        graphicsContext.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        double shift = 0.0;

        for (int i = 0; i < page.size(); i++) {
            ArrayList<Word> subListDraw = page.get(i);
            double heightSubRow = getHeightRow(subListDraw);

            double canvasPosY = heightSubRow + shift;
            double canvasPosX = 0.0;

            for (int j = 0; j < subListDraw.size(); j++) {
                Word word = subListDraw.get(j);
                graphicsContext.setFont(new Font(word.getSizeFont()));
                graphicsContext.fillText(word.getValue(), canvasPosX, canvasPosY);
                canvasPosX += word.getWidthWord();
            }
            shift += heightSubRow;
        }
        group.getChildren().add(this.canvas);
    }

    private void pageClear(){
        this.tempHeightPage = 0.0;
        this.fullPage = false;
        this.page.clear();
    }
}
