package ModelClasses;

import AppInterfaces.AlertMessage;
import AppInterfaces.Model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReadingModel implements Model {
    private final Path pathUserFile;
    private final Charset charsetUserFile;
    private AlertMessage alertMessage;
    private long maxRowFromFileUser;
    private String textRowFromFile;

    public FileReadingModel(Path pathUserFile, Charset charsetUserFile, AlertMessage alertMessage) {
        this.pathUserFile = pathUserFile;
        this.charsetUserFile = charsetUserFile;
        this.alertMessage = alertMessage;
        this.textRowFromFile = "";
        calculateMaxRow();
    }


    @Override
    public String getRow(long position){
        textRowFromFile = null;

        try (Stream<String> stream = Files.lines(this.pathUserFile, this.charsetUserFile)){
            textRowFromFile = stream.skip(position).limit(1).collect(Collectors.joining());
        } catch (IOException e){
            this.alertMessage.showError(e);
        }

        return textRowFromFile;
    }

    @Override
    public long maxRowInFile(){
        return this.maxRowFromFileUser;
    }

    private void calculateMaxRow(){
        this.maxRowFromFileUser = 0;
        try (Stream<String> stream = Files.lines(this.pathUserFile, this.charsetUserFile)) {
            this.maxRowFromFileUser = stream.count();
        } catch (IOException e) {
            this.alertMessage.showError(e);
        }
    }
}
