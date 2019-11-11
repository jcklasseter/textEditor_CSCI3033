package textEditor;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.*;
import java.io.*;
import java.util.Vector;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert.AlertType;
import javafx.print.PrinterJob;
import javafx.scene.Node;

//Controller for textEditor.fxml
public class Controller {
    @FXML public Button   saveButton;
    @FXML public Button   openButton;
    @FXML public Button decFontButton;
    @FXML public Button incFontButton;
    @FXML public Button printButton;
    // public Button   copyButton;
    // public Button   pasteButton;
    // public Button   cutButton;
    @FXML public TabPane tabpane;
    @FXML public GridPane mainGridPane;

    Tab newtab = new Tab();
    private void setupTabPane(){
        Tab tab = new Tab("New Text");
        WebView w = new WebView();
        applyTemplate(w, initCode);
        tab.setContent(w);

        tabpane.getTabs().addAll(tab);
    }

    public void createTab(ActionEvent actionEvent){
        Tab selectedTab = tabpane.getSelectionModel().getSelectedItem();
        if(selectedTab == newtab){
            Tab tab = new Tab("New Txt");

        }
    }
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
                    "    indentUnit: 4," +
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
//        setupCopyButton();
//        setupPasteButton();
//        setupCutButton();
        setupTabPane();
        setupIncFontButton();
        setupDecFontButton();
        setupPrintButton();

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        mainGridPane.getColumnConstraints().add(column1);
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
        Scene scene = openButton.getScene();

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
    //setup decrease font button
    private void setupDecFontButton() {
        Scene scene = decFontButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN);
        decFontButton.setText("Font -- (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        decFontButton.fire();
                    }
                }
        );
    }
    //setup increase font button
    private void setupIncFontButton() {
        Scene scene = incFontButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN);
        incFontButton.setText("Font ++ (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        incFontButton.fire();
                    }
                }
        );
    }
    //setup print button
    private void setupPrintButton() {
        Scene scene = printButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN);
        printButton.setText("Print (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        printButton.fire();
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

            Tab tab = new Tab(f.getName());

            // We need to create a new webview, as each view belongs to an individual tab
            try {
                WebView w = new WebView();

                applyTemplate(w, new String(Files.readAllBytes(p)));

                tab.setContent(w);
                tabpane.getTabs().add(tab);
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

    public void incFont(ActionEvent actionEvent, WebView w)
    {
        w.setFontScale(1.10);
    }
    
    public void decFont(ActionEvent actionEvent, WebView w)
    {
        w.setFontScale(0.90);
    }
    
    public void printFile(ActionEvent actionEvent, WebView w)
    {
        Printer printer = Printer.getDefaultPrinter();
        printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.EQUAL);
        WebEngine file = w.getEngine();
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        job.showPrintDialog(owner);
        if (job != null){
            file.print(job);
            job.endJob();
        }
    }
        
    
    
    
    
    //Todo implement the copy function
    public void copyText(ActionEvent actionEvent)
    {

    }

    //Todo implement the paste function
    public void pasteText(ActionEvent actionEvent)
    {

    }

    //Todo implement the cut function
    public void cutText(ActionEvent actionEvent)
    {

    }

    // Applies the template to the editing code to create the html+javascript source
    private void applyTemplate(WebView w, String in) {
        w.getEngine().loadContent(template.replace("${code}", in));
    }

    // Returns the value of the code being edited through codemirror functions
    // Gets code from active tab
    private String getCode() {
        return (String ) ((WebView)tabpane.getSelectionModel().getSelectedItem().getContent()).getEngine().executeScript("editor.getValue();");
    }
}
