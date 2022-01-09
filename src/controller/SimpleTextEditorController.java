package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
    public TextField txtFileName;
    public Rectangle saveFIleAlert;
    public Label lblSaveFile;
    public Button btnSaveFileCancel;
    public ImageView imgCancel;
    public Button btnSaveFile;
    private Stage stage;
    private File file;
    private Clipboard clipboard;

    @Override
    public void setTitle(Stage primaryStage) {
        this.stage = primaryStage;
    }

    public void initialize()  {
        updateTitle("Untitled");
        txtFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            updateTitle(newValue);
        });

        txtArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!fileName.startsWith("*")){
                fileName="*"+fileName;
            }
            updateTitle(fileName);
        });

        alertControl(false);
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
    }

    public void mnuReplaceOnAction(ActionEvent actionEvent) {
    }

    public void mnuReplaceAllOnAction(ActionEvent actionEvent) {
    }

    public void mnuAboutUsOnAction(ActionEvent actionEvent) {
    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save the file?",
                ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get()==ButtonType.NO){
            txtArea.clear();
            txtFileName.clear();
        }else if (result.get()==ButtonType.YES){
            btnSave.fire();
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the directory");
        file = directoryChooser.showDialog(null);
        alertControl(true);
    }

    public void btnReplaceOnAction(ActionEvent actionEvent) {
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
    }

    public void btnFindOnAction(ActionEvent actionEvent) {
    }

    public void btnPasteOnAction(ActionEvent actionEvent) {
        int caretPosition = txtArea.getCaretPosition();
        txtArea.insertText(caretPosition, clipboard.getString());
    }

    public void findUpOnAction(MouseEvent mouseEvent) {
    }

    public void findDownOnAction(MouseEvent mouseEvent) {
    }

    public void btnSaveFileCancelOnAction(ActionEvent actionEvent) {
        alertControl(false);
    }

    public void btnSaveFileOnAction(ActionEvent actionEvent) {
        if (txtFileName.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR,"Please enter a valid file name");
        }else {
            fileName = txtFileName.getText();
            Path path = Paths.get(file.getAbsolutePath() + "/" + fileName + ".txt");

            String fileContent = txtArea.getText();
            byte[] bytes = fileContent.getBytes();

            try {
                Files.write(path, bytes);
                new Alert(Alert.AlertType.CONFIRMATION, "Saved").show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        alertControl(false);
    }

    public void imgCancelOnMouseClick(MouseEvent mouseEvent) {
        alertControl(false);
    }

    private void alertControl(boolean visible){
        saveFIleAlert.setVisible(visible);
        lblSaveFile.setVisible(visible);
        txtFileName.setVisible(visible);
        btnSaveFileCancel.setVisible(visible);
        btnSaveFile.setVisible(visible);
        imgCancel.setVisible(visible);
    }

    private void setSelectedText(){
        clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(txtArea.getSelectedText());
        clipboard.setContent(content);
        btnPaste.setDisable(false);
    }
}
