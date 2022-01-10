package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTextEditorController implements SetTitle{
    public Button btnNew;
    public Button btnSave;
    public Button btnReplace;
    public Button btnReplace1;
    public Button btnCut;
    public Button btnCopy;
    public Button btnOpen;
    public Button btnSearch;
    public TextField txtSearch;
    public TextField txtReplace;
    public Button btnFind;
    public TextArea txtArea;
    public Label lblWordCount;
    public Label lblFoundCount;
    public Button btnPaste;
    public ImageView findUp;
    public ImageView findDown;
    public static String fileName = "Untitled";
    public ToggleButton btnRegEx;
    public ToggleButton btnCaseSensitive;
    public Button btnReplaceAll;
    private Stage stage;
    private Clipboard clipboard;
    private boolean textChanged;
    private Matcher matcher;

    @Override
    public void setTitle(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initialize()  {
        updateTitle("Untitled");

        txtArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!fileName.startsWith("*")){
                fileName="*"+fileName;
            }
            updateTitle(fileName);
        });

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> textChanged = true);

    }

    private void updateTitle(String title) {
        if (stage != null) {
            stage.setTitle("Simple Text Editor - "+title);
        } else {
            System.out.println("Warning: null stage");
        }
    }

    public void mnuNewOnAction(ActionEvent actionEvent) {
        btnNew.fire();
    }

    public void mnuOpenOnAction(ActionEvent actionEvent) {
        btnOpen.fire();
    }

    public void mnuSaveOnAction(ActionEvent actionEvent) {
        btnSave.fire();
    }

    public void mnuPrintOnAction(ActionEvent actionEvent) {
    }

    public void mnuExitOnAction(ActionEvent actionEvent) {
        btnSave.fire();
        Platform.exit();
    }

    public void mnuCutOnAction(ActionEvent actionEvent) {
        btnCut.fire();
    }

    public void mnuCopyOnAction(ActionEvent actionEvent) {
        btnCopy.fire();
    }

    public void mnuPasteOnAction(ActionEvent actionEvent) {
        btnPaste.fire();
    }

    public void mnuSelectAllOnAction(ActionEvent actionEvent) {
        txtArea.selectAll();
    }

    public void mnuFindOnAction(ActionEvent actionEvent) {
        txtSearch.requestFocus();
    }

    public void mnuReplaceOnAction(ActionEvent actionEvent) {
        btnReplace.fire();
    }

    public void mnuReplaceAllOnAction(ActionEvent actionEvent) {
        btnReplaceAll.fire();
    }

    public void mnuAboutUsOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/AboutUs.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle("About Us");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the file?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get()==ButtonType.NO){
            txtArea.clear();
        }else if (result.get()==ButtonType.YES){
            btnSave.fire();
        }
        txtArea.clear();
        fileName = "Untitled";
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws IOException {
        if (fileName.startsWith("*")) {
            fileName = fileName.split("[*]")[1];
        }
        Path path;
        if (location==null){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose the directory");
            File file = fileChooser.showSaveDialog(null);
            path = Paths.get(file.getAbsolutePath()+".txt");
        }else {
            path = Paths.get(location);
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileContent = txtArea.getText();
        byte[] bytes = fileContent.getBytes();

        Files.createFile(path);
        SeekableByteChannel seekableByteChannel = Files.newByteChannel(path, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        seekableByteChannel.write(buffer);
        seekableByteChannel.close();

        System.out.println(Files.exists(path));
    }

    public void btnReplaceOnAction(ActionEvent actionEvent) {
        btnFind.fire();
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            txtArea.replaceText(start, end, txtReplace.getText());
        } else {
            matcher.reset();
        }
    }

    public void btnCutOnAction(ActionEvent actionEvent) {
        setSelectedText();
        String[] texts = txtArea.getText().split(txtArea.getSelectedText());
        txtArea.clear();
        for (String text:texts
        ) {
            txtArea.setText(txtArea.getText()+text);
        }
    }

    public void btnCopyOnAction(ActionEvent actionEvent) {
        setSelectedText();
    }

    public void btnOpenOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files","*.txt"));
        File file = fileChooser.showSaveDialog(null);

        Path path = Paths.get(file.getAbsolutePath());
        try {
            byte[] bytes= Files.readAllBytes(path);
            txtArea.setText(new String(bytes));
            String[] split=(file.getAbsolutePath().split("/"));
            fileName = split[split.length-1];
            updateTitle(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnSearchOnAction(ActionEvent actionEvent) {
        txtSearch.requestFocus();
    }

    public void btnFindOnAction(ActionEvent actionEvent) {
        txtArea.deselect();
        if (textChanged) {
            int flags = 0;
            if (!btnRegEx.isSelected()) flags = flags | Pattern.LITERAL;
            if (!btnCaseSensitive.isSelected()) flags = flags | Pattern.CASE_INSENSITIVE;

            matcher = Pattern.compile(txtSearch.getText(), flags)
                    .matcher(txtArea.getText());
            textChanged = false;
        }

        if (matcher.find()) {
            txtArea.selectRange(matcher.start(), matcher.end());
        } else {
            matcher.reset();
        }
    }

    public void btnPasteOnAction(ActionEvent actionEvent) {
        int caretPosition = txtArea.getCaretPosition();
        txtArea.insertText(caretPosition, clipboard.getString());
    }

    public void findUpOnAction(MouseEvent mouseEvent) {
    }

    public void findDownOnAction(MouseEvent mouseEvent) {
        if (matcher.find()) {
            txtArea.selectRange(matcher.start(), matcher.end());
        } else {
            matcher.reset();
        }
    }

    private void setSelectedText(){
        clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txtArea.getSelectedText());
        clipboard.setContent(content);
        btnPaste.setDisable(false);
    }

    public void btnRegexOnAction(ActionEvent actionEvent) {
        textChanged = true;
        btnFind.fire();
    }

    public void btnCaseSensitiveOnAction(ActionEvent actionEvent) {
        textChanged = true;
        btnFind.fire();
    }

    public void btnReplaceAllOnAction(ActionEvent actionEvent) {
        String replaceText = txtReplace.getText();
        txtArea.setText(txtArea.getText().replace(txtSearch.getText(),replaceText));
    }
}
