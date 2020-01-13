package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

interface Algorithm {
    String execute(String message, int key);
    String encrypt(String messageToEncrypt, int key);
    String decrypt(String messageToDecrypt, int key);
}

interface InputOption {
    String load();
}

interface OutputOption {
    void write(String data);
}

class TerminalOutputOption implements OutputOption {

    @Override
    public void write(String data) {
        System.out.println(data);
    }
}

class FileOutputOption implements OutputOption {

    private String filePath;

    public FileOutputOption(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void write(String data) {
        File file = new File(filePath);
        System.out.println(file.getAbsolutePath());
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ArgsInputOption implements InputOption {

    private String argData;

    public ArgsInputOption(String argData) {
        this.argData = argData;
    }

    @Override
    public String load() {
        return argData;
    }
}

class FileInputOption implements InputOption {

    private String filePath;

    public FileInputOption(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String load() {

        StringBuilder fileContent = new StringBuilder();
        File file = new File(filePath);
        System.out.println(file.getAbsolutePath());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContent.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return fileContent.toString();
    }
}

class UnicodeAlgorithm implements Algorithm {

    @Override
    public String encrypt(String messageToEncrypt, int key) {
        char[] arrayChar = messageToEncrypt.toCharArray();

        for (int i = 0; i < arrayChar.length; i++) {
            arrayChar[i] = (char) (arrayChar[i] + key);
        }

        return new String(arrayChar);

    }

    @Override
    public String decrypt(String messageToDecrypt, int key) {
        char[] arrayChar = messageToDecrypt.toCharArray();

        for (int i = 0; i < arrayChar.length; i++) {
            arrayChar[i] = (char) (arrayChar[i] - key);
        }

        return new String(arrayChar);
    }

    AlgorithmMode mode;

    public UnicodeAlgorithm(AlgorithmMode mode) {
        this.mode = mode;
    }

    @Override
    public String execute(String message, int key) {
        switch (mode) {
            case ENCRYPTION:
                return encrypt(message, key);
            case DECRYPTION:
                return decrypt(message, key);
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }
}

enum AlgorithmMode{
    ENCRYPTION,
    DECRYPTION
}

class ShiftAlgorithm implements Algorithm {

    AlgorithmMode mode;

    public ShiftAlgorithm(AlgorithmMode mode) {
        this.mode = mode;
    }

    @Override
    public String execute(String message, int key) {
        switch (mode) {
            case ENCRYPTION:
                return encrypt(message, key);
            case DECRYPTION:
                return decrypt(message, key);
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    @Override
    public String encrypt(String messageToEncrypt, int key) {

        StringBuilder builder = new StringBuilder();

        for(char letter: messageToEncrypt.toCharArray()) {

            if (Character.isAlphabetic(letter) && Character.isUpperCase(letter)) {
                builder.append((char)((letter + key - 'A') % 26 + 'A'));
            } else if (Character.isAlphabetic(letter) && Character.isLowerCase(letter)) {
                builder.append((char)((letter + key - 'a') % 26 + 'a'));
            } else {
                builder.append(letter);
            }
        }

        return builder.toString();
    }

    @Override
    public String decrypt(String messageToDecrypt, int key) {

        StringBuilder builder = new StringBuilder();

        for(char letter: messageToDecrypt.toCharArray()) {

            if (Character.isAlphabetic(letter) && Character.isUpperCase(letter)) {
                builder.append((char)((letter + 26 - key - 'A') % 26 + 'A'));
            } else if (Character.isAlphabetic(letter) && Character.isLowerCase(letter)){
                builder.append((char)((letter + 26 - key - 'a') % 26 + 'a'));
            } else {
                builder.append(letter);
            }
        }

        return builder.toString();
    }
}

class OutputSelection {

    private OutputOption outputOption;

    public void setOutputOption(OutputOption outputOption) {
        this.outputOption = outputOption;
    }

    public void write(String data) {
        this.outputOption.write(data);
    }
}

class InputSelection {

    private InputOption inputOption;

    public void setInputOption(InputOption inputOption) {
        this.inputOption = inputOption;
    }

    public String getInputData() {
        return inputOption.load();
    }

}

class AlgorithmSelection {

    private Algorithm algorithm;

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String execute(String message, int key) {
        return this.algorithm.execute(message, key);
    }

}

class OptionsHandler {

    private String actionType = null;
    private String message = "";
    private Integer eNumber = null;
    private String fileNameRead = null;
    private String fileNameWrite = null;
    private String outPutOption = "terminal";
    private String alg = "shift";

    public OptionsHandler(String[] args) {
        extractArgs(args);
    }

    private void extractArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-mode":
                    actionType = args[i + 1];
                    if (!(actionType.equalsIgnoreCase("enc") || actionType.equalsIgnoreCase("dec"))) {
                        System.out.println("Unknown operation");
                        break;
                    }
                    break;
                case "-key":
                    eNumber = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    message = args[i + 1];
                    break;
                case "-in":
                    fileNameRead = args[i + 1];
                    break;
                case "-out":
                    fileNameWrite = args[i + 1];
                    outPutOption = "file";
                    break;
                case "-alg":
                    alg = args[i + 1];
                    break;
                default:
                    if (actionType.isBlank()) {
                        actionType = "enc";
                    }
                    if (eNumber == null) {
                        eNumber = 0;
                    }
            }
        }
    }

    public String getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }

    public Integer geteNumber() {
        return eNumber;
    }

    public String getFileNameRead() {
        return fileNameRead;
    }

    public String getFileNameWrite() {
        return fileNameWrite;
    }

    public String getOutPutOption() {
        return outPutOption;
    }

    public String getAlg() {
        return alg;
    }

}

class EncryptionDecryption {
    private OptionsHandler options;
    private InputSelection inputSelection;
    private AlgorithmSelection algorithmSelection;
    private OutputSelection outputSelection;
    private String data;

    public EncryptionDecryption(OptionsHandler optionsHandler) {
        this.options = optionsHandler;
    }

    public void execute() {
        getInput();
        selectAlgorithm();
        executeAlgorithm();
        selectOutput();
        outputData();
    }

    private void selectOutput() {
        outputSelection = new OutputSelection();
        if (options.getOutPutOption().equals("file")) {
            outputSelection.setOutputOption(new FileOutputOption(options.getFileNameWrite()));
        } else {
            outputSelection.setOutputOption(new TerminalOutputOption());
        }
    }

    private void outputData() {
        outputSelection.write(data);
    }

    private void executeAlgorithm() {
        data = algorithmSelection.execute(inputSelection.getInputData(), options.geteNumber());
    }

    private void selectAlgorithm() {
        algorithmSelection = new AlgorithmSelection();

        switch (options.getActionType()) {
            case "enc":
                switch (options.getAlg()) {
                    case "shift":
                        algorithmSelection.setAlgorithm(new ShiftAlgorithm(AlgorithmMode.ENCRYPTION));
                        break;
                    case "unicode":
                        algorithmSelection.setAlgorithm(new UnicodeAlgorithm(AlgorithmMode.ENCRYPTION));
                        break;
                }

                break;
            case "dec":
                switch (options.getAlg()) {
                    case "shift":
                        algorithmSelection.setAlgorithm(new ShiftAlgorithm(AlgorithmMode.DECRYPTION));
                        break;
                    case "unicode":
                        algorithmSelection.setAlgorithm(new UnicodeAlgorithm(AlgorithmMode.DECRYPTION));
                        break;
                }
                break;
            default:
                System.out.println("Incorrect type");
        }
    }

    private void getInput() {
        inputSelection = new InputSelection();

        if (options.getFileNameRead() != null) {
            inputSelection.setInputOption(new FileInputOption(options.getFileNameRead()));
        } else {
            inputSelection.setInputOption(new ArgsInputOption(options.getMessage()));
        }
    }
}

public class Main {
    public static void main(String[] args) {
        new EncryptionDecryption(new OptionsHandler(args)).execute();
    }
}