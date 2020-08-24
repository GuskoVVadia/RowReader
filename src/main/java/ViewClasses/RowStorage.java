package ViewClasses;

import AppInterfaces.Model;
import AppInterfaces.ObserverSize;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.TreeMap;

public class RowStorage {

    private Model currentModel; //откуда брать строки
    private TreeMap<Long, ObserverSizeRow> storage;    //где хранить обработанные строки
    private int sizeStorage;    //сколько строк нужно отбирать для хранения
    private Word separator;

    public RowStorage(Model currentModel) {
        this.currentModel = currentModel;
        this.sizeStorage = (currentModel.maxRowInFile() < 50) ? (int) currentModel.maxRowInFile() : 50;
        this.storage = new TreeMap<Long, ObserverSizeRow>();

        Text text = new Text(" ");
        text.setFont(new Font(16.0));
        this.separator = new Word(" ", 16.0, text.getBoundsInLocal().getWidth(), text.getBoundsInLocal().getHeight());
    }

   public void getCalculateRow(long startPosition){

        if ((startPosition + sizeStorage) > currentModel.maxRowInFile()){
            startPosition = (currentModel.maxRowInFile() - sizeStorage);
        }

        String[] tempArray;
        int numberWords = 0;

        for (long i = startPosition; i <= (startPosition + sizeStorage); i++) {
            tempArray = currentModel.getRow(i).split(" ");
            numberWords = getConstructWords(tempArray, numberWords, i);
        }
    }

    private int getConstructWords(String[] arrayWords, int wordsNumber, long numberRow){

        ArrayList<Word> arrayList = new ArrayList<>();
        Text text;
        Font font;
        double sizeFont = 0.0;

        for (int i = 0; i < arrayWords.length; i++) {

            if (!(arrayWords[i].equals("") || arrayWords[i].equals(","))) {
                wordsNumber++;
            }

            wordsNumber = (wordsNumber > 4) ? 1 : wordsNumber;
            sizeFont = (wordsNumber > 2) ? 19.0 : 16.0;

            text = new Text(arrayWords[i]);
            font = new Font(sizeFont);
            text.setFont(font);

            arrayList.add(new Word(arrayWords[i], sizeFont, text.getBoundsInLocal().getWidth(),
                    text.getBoundsInLocal().getHeight()));

            text = null;
            font = null;
        }

        storage.put(numberRow, new ObserverSizeRow(this.separator, arrayList));
        arrayList = null;
        return wordsNumber;
    }

    public boolean contains(Long value){
        return this.storage.containsKey(value);
    }

    public ObserverSizeRow getRow(long numberRow){
        return this.storage.get(numberRow);
    }

    public void clear(){
        this.storage.clear();
    }
}
