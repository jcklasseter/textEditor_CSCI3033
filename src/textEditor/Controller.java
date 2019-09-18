package textEditor;

//public class Controller {
//}

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import java.io.*;

import javafx.scene.control.Alert.AlertType;

public class Controller {
    @FXML public TextArea textView;
    @FXML public Button   saveButton;

    //TODO: Implement opening files and reading it into the textView

    public void saveTextView(ActionEvent actionEvent)
    {
        final FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(null);

        //Save the file if it does not exist, or if the user is really sure they wanna nuke the present version
        //This is handled by the fc.showSaveDialog, at least on OSX
        //TODO: Don't ask when you're editing a file you opened, may not be possible
        //Current commented out section just asks again, stupidly
//        if (file.exists()) {
//            Alert alert = new Alert(AlertType.CONFIRMATION, "Delete existing " + file.getName() + "?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
//            alert.showAndWait();
//
//            if (alert.getResult() == ButtonType.YES) {
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    try {
//                        out.write(textView.getText().getBytes());
//                        out.close();
//                    } catch  (java.io.IOException e)
//                    {
//                        System.out.println("Error writing file " + file.getAbsoluteFile() + ": " + e.getMessage());
//                    }
//
//                } catch (java.io.FileNotFoundException e) {
//                    System.out.println("Error opening file " + file.getAbsoluteFile() + ": " + e.getMessage());
//                }
//            }
//        }
//        else {
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
