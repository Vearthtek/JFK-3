package icallable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.*;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private Button calculateButton;

    @FXML
    private ChoiceBox<String> fileChoiceBox;

    @FXML
    private TextField inputField;

    @FXML
    private Label resultLabel;

    @FXML
    private Label descLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressIndicator;

    private FileChooser fileChooser;
    private List<ICallable> callableFunc;
    private List<String> descList;
    private Stage stage;

    public Controller() {
        callableFunc = new ArrayList<ICallable>();
        descList = new ArrayList<String>();
    }

    public void setDesc() {
        int id = fileChoiceBox.getSelectionModel().getSelectedIndex();
        try {
            descLabel.setText(descList.get(id));
        } catch (Exception ex) {
            ;
        }
    }

    @FXML
    public void initialize() {
        calculateButton.setDisable(true);
        inputField.setDisable(true);
        fileChoiceBox.setDisable(true);

        fileChooser = new FileChooser();
        fileChooser.setTitle("Wskaż lokalizację *JARa:");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files (*.jar)", "*.jar"));
        fileChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                setDesc();
            }
        });
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!inputField.getText().isEmpty()) {
                calculateButton.setDisable(false);
            } else {
                calculateButton.setDisable(true);
            }
        });
    }

    public void calculate() {
        Double param;
        try {
            param = Double.parseDouble(inputField.getText().replace(',', '.'));
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Podana wartość nie jest liczbą!");
            alert.setHeaderText("Wprowadź poprawną wartość.");
            alert.setContentText("Aby przekonwertować walutę wprowadzona wartość musi być liczbą.");
            alert.showAndWait();
            return;
        }

        int id = fileChoiceBox.getSelectionModel().getSelectedIndex();
        Double result = callableFunc.get(id).convert(param);
        String convertedFrom = fileChoiceBox.getItems().get(id).substring(0, 3);
        String convertedTo = fileChoiceBox.getItems().get(id).substring(5, 8);
        resultLabel.setText("Wynik: " + String.format("%.2f", param) + " " + convertedFrom + " to " + String.format("%.2f", result) + " " + convertedTo + ".");
    }

    public void selectFile() {
        File jarIoFile = fileChooser.showOpenDialog(stage);
        if (jarIoFile == null) return;
        String jarPath = jarIoFile.toString();

        if (!jarIoFile.exists())
            return;

        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> entries = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + jarPath + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            callableFunc.clear();
            descList.clear();
            fileChoiceBox.getItems().clear();
            resultLabel.setText("Wynik: ");
            descLabel.setText("");
            inputField.setText("");
            calculateButton.setDisable(true);
            inputField.setDisable(true);
            fileChoiceBox.setDisable(true);

            progressBar.setVisible(true);
            progressIndicator.setVisible(true);
            progressBar.setProgress(0);
            progressIndicator.setProgress(0);
            int i = 0;
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }

                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                try {
                    Class<?> c = cl.loadClass(className);
                    if (!c.isAnnotationPresent(Description.class))
                        continue;

                    if (!ICallable.class.isAssignableFrom(c))
                        throw new Exception("Class " + className + " does not implement the contract.");

                    ICallable callable = (ICallable) c.newInstance();
                    if (null == callable)
                        throw new Exception();

                    callableFunc.add(callable);
                    Description description = c.getAnnotation(Description.class);
                    descList.add("Opis: " + description.description());
                    fileChoiceBox.getItems().add(je.getName().substring(je.getName().lastIndexOf("/") + 1, je.getName().lastIndexOf(".")));
                } catch (ClassNotFoundException exp) {
                    continue;
                } catch (IllegalAccessException e) {
                    continue;
                } catch (InstantiationException e) {
                    continue;
                } catch (Exception e) {
                    continue;
                }
                ++i;
                progressBar.setProgress(i / 6.);
                progressIndicator.setProgress(i / 6.);
            }
            progressBar.setVisible(false);
            progressIndicator.setVisible(false);

            fileChoiceBox.getSelectionModel().selectFirst();
            inputField.setDisable(false);
            fileChoiceBox.setDisable(false);
        } catch (IOException exp) {
        } finally {
            if (null != jarFile)
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
