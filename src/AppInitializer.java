import controller.SetTitle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("view/SimpleTextEditor.fxml"));
            loader.setControllerFactory((Class<?> type) -> {
                try {
                    Object controller = type.newInstance();
                if (controller instanceof SetTitle) {
                    ((SetTitle) controller).setTitle(primaryStage);
                }
                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            AnchorPane root = loader.load();
            Scene mainScene = new Scene(root);
            primaryStage.setScene(mainScene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
