package textEditor;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javafx.scene.control.Alert.AlertType;

//Controller for textEditor.fxml
public class Controller {
    @FXML public TextArea textView;
    @FXML public Button   saveButton;
    @FXML public Button   openButton;

    //Add the keyboard shortcuts to buttons
    public void setup()
    {
        setupSaveButton();
        setupOpenButton();
    }

    //Save button keyboard shortcut
    private void setupSaveButton() {
        Scene scene = saveButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
        saveButton.setText("Save (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        saveButton.fire();
                    }
                }
        );
    }

    //Open button keyboard shortcut
    private void setupOpenButton() {
        Scene scene = saveButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
        openButton.setText("Open (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        openButton.fire();
                    }
                }
        );
    }

    //TODO: Implement opening files and reading it into the textView
    public void openFileIntoTextView(ActionEvent actionEvent)
    {
        try {
            final FileChooser fc = new FileChooser();
            File f = fc.showOpenDialog(null);
            Path p = Paths.get(f.getAbsolutePath());

            try {
                textView.setText(new String(Files.readAllBytes(p)));
            }
            catch (java.io.IOException e)
            {
                System.out.println("Error reading file " + f.getAbsolutePath() + ": " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Saves the contents of the text view to the
    public void saveTextView(ActionEvent actionEvent)
    {
        //Get where to save it
        final FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(null);

        //Save the file if it does not exist, or if the user is really sure they wanna nuke the present version
        //This is handled by the fc.showSaveDialog, at least on OSX
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(textView.getText().getBytes());
                out.close();
            } catch  (java.io.IOException e)
            {
                System.out.println("Error writing file " + file.getAbsoluteFile() + ": " + e.getMessage());
            }

        } catch (java.io.FileNotFoundException e) {
            System.out.println("Error opening file " + file.getAbsoluteFile() + ": " + e.getMessage());
        }
    }
    //   }
}
