package textEditor;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert.AlertType;

//Controller for textEditor.fxml
public class Controller {
    @FXML public Button   saveButton;
    @FXML public Button   openButton;
    @FXML public Button   copyButton;
    @FXML public Button   pasteButton;
    @FXML public Button   cutButton;
    @FXML private WebView editor;

    // Template for c-like languages from codemirror examples
    private String initCode  =  "// HelloWorld.java\n\n" +
            "public class HelloWorld\n" +
            "{\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Hello World!\");\n" +
            "    }\n" +
            "}\n";

    private final String template =
            "<!doctype html>" +
                    "<html>" +
                    "<head>" +
                    "  <link rel=\"stylesheet\" href=\"http://codemirror.net/lib/codemirror.css\">" +
                    "  <script src=\"http://codemirror.net/lib/codemirror.js\"></script>" +
                    "  <script src=\"http://codemirror.net/mode/clike/clike.js\"></script>" +
                    "</head>" +
                    "<body>" +
                    "<form><textarea id=\"code\" name=\"code\">\n" +
                    "${code}" +
                    "</textarea></form>" +
                    "<script>" +
                    "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {" +
                    "    lineNumbers: true," +
                    "    matchBrackets: true," +
                    "    mode: \"text/x-java\"" +
                    "  });" +
                    "</script>" +
                    "</body>" +
                    "</html>";

    //Add the keyboard shortcuts to buttons
    public void setup()
    {
        setupSaveButton();
        setupOpenButton();
        setupCopyButton();
        setupPasteButton();
        setupCutButton();

        applyTemplate(initCode);
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

    public void openFile(ActionEvent actionEvent)
    {
        try {
            final FileChooser fc = new FileChooser();
            File f = fc.showOpenDialog(null);
            Path p = Paths.get(f.getAbsolutePath());

            try {
                applyTemplate(new String(Files.readAllBytes(p)));
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
    public void saveEditor(ActionEvent actionEvent)
    {
        //Get where to save it
        final FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(null);

        //Save the file if it does not exist, or if the user is really sure they wanna nuke the present version
        //This is handled by the fc.showSaveDialog, at least on OSX
        try {
            FileOutputStream out = new FileOutputStream(file);
            try {
                String outS = getCode();
                System.out.println(outS);
                out.write(outS.getBytes());
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

    //Create the copy button
    private void setupCopyButton(){
        Scene scene = copyButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
        copyButton.setText("Copy (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        copyButton.fire();
                    }
                }
        );
    }

    //Create the paste button
    private void setupPasteButton(){
        Scene scene = pasteButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
        pasteButton.setText("Copy (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        pasteButton.fire();
                    }
                }
        );
    }

    //Create the cut button
    private void setupCutButton(){
        Scene scene = cutButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN);
        cutButton.setText("Cut (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        cutButton.fire();
                    }
                }
        );
    }

    //Todo implement the copy function
    public void copyTextView(ActionEvent actionEvent){

    }

    //Todo implement the paste function
    public void pasteText(ActionEvent actionEvent){

    }

    //Todo implement the cut function
    public void cutText(ActionEvent actionEvent){

    }

    // Applies the template to the editing code to create the html+javascript source
    private void applyTemplate(String in) {
        editor.getEngine().loadContent(template.replace("${code}", in));
    }

    // Returns the value of the code being edited through codemirror functions
    private String getCode() {
        return (String ) editor.getEngine().executeScript("editor.getValue();");
    }
}
