package textEditor;

// Text editor demo project by
// John Lasseter
// Austin Leverette
// Mike Crawford

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.*;
import javafx.scene.control.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(Controller.class.getResource("textEditor.fxml"));
        GridPane page = loader.load();

        Controller cont = loader.getController();

        primaryStage.setTitle("SIMPLE PLUS");
        try
        {
            primaryStage.getIcons().add(new Image(new FileInputStream("..\\textEditor_CSCI3033\\src\\iconsflow\\icon2.png")));
        }
        catch (FileNotFoundException e)
        {
            primaryStage.getIcons().add(new Image(new FileInputStream(FileSystems.getDefault().getPath(".").toAbsolutePath() + "/src/iconsflow/icon2.png")));
        }
        primaryStage.setScene(new Scene(page, 800, 600));

        cont.setup(); //Connects the keyboard shortcuts
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
