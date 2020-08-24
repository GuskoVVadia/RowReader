package ViewClasses;

import AppInterfaces.ObserverSize;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ObserverSizeRow implements ObserverSize {

    private ArrayList<Word> value;
    private ArrayList<ArrayList<Word>> child;

    public ObserverSizeRow(Word separator, ArrayList<Word> words){
        this.value = new ArrayList<>();
        this.child = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            this.value.add(words.get(i));
            if (i != (words.size() - 1)){
                this.value.add(separator);
            }
        }
    }

    @Override
    public void update(double width, double height) {

        this.child = new ArrayList<>();
        ArrayList<Word> tempRow = new ArrayList<>();
        Word tempWord;
        double tempWidthRow = 0.0;

        for (int i = 0; i < value.size(); i++) {
            tempWord = value.get(i);

            if ((tempWord.getWidthWord() + tempWidthRow) < width){
                tempRow.add(tempWord);
                tempWidthRow += tempWord.getWidthWord();
            } else {

                if (tempRow.size() > 0) {
                    child.add(tempRow);
                    tempRow = new ArrayList<>();
                    tempWidthRow = 0.0;
                }


                if (tempWord.getWidthWord() > width){

                    String[] valueArray = tempWord.getValue().split("");
                    Text text = new Text();
                    text.setFont(new Font(tempWord.getSizeFont()));

                    for (int j = 0; j < valueArray.length; j++) {
                        text.setText(valueArray[j]);

                        if((text.getBoundsInLocal().getWidth() + tempWidthRow) < width){
                            tempRow.add(new Word(valueArray[j], tempWord.getSizeFont(), text.getBoundsInLocal().getWidth(),
                                    text.getBoundsInLocal().getHeight()));
                            tempWidthRow += text.getBoundsInLocal().getWidth();
                        } else {
                            child.add(tempRow);
                            tempRow = new ArrayList<>();
                            tempRow.add(new Word(valueArray[j], tempWord.getSizeFont(), text.getBoundsInLocal().getWidth(),
                                    text.getBoundsInLocal().getHeight()));
                            tempWidthRow = text.getBoundsInLocal().getWidth();
                        }
                    }
                    text = null;
                }
                else {
                    tempRow.add(tempWord);
                    tempWidthRow = tempWord.getWidthWord();
                }
            }
        }

        if (tempRow.size() > 0) {
            child.add(tempRow);
        }
        tempRow = null;
        tempWord = null;
    }

    public ArrayList<ArrayList<Word>> getChild() {
        return child;
    }
}
