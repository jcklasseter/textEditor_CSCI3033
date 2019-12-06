package textEditor;

import java.lang.Math;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.input.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.*;
import java.io.*;
import java.net.URL;
import java.util.Vector;
import org.apache.commons.io.FilenameUtils;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert.AlertType;
import javafx.print.PrinterJob;
import javafx.scene.transform.Scale;
import javafx.print.PageLayout;
import javafx.scene.transform.Transform;
import javafx.scene.Node;
import javafx.print.PageOrientation;


//Controller for textEditor.fxml
public class Controller {
    @FXML public Button   saveButton;
    @FXML public Button   openButton;
    @FXML public Button   incFontButton;
    @FXML public Button   decFontButton;
    @FXML public Button   printButton;
     @FXML public Button compileButton;
    @FXML public TabPane tabpane;
    @FXML public GridPane mainGridPane;
    @FXML public TextField compilerField;
    @FXML public TextField javaField;

    private void setupCompile(){
        Scene scene = compileButton.getScene();

        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN);
        compileButton.setText("Compile (" + kc.getDisplayText() + ")");

        scene.getAccelerators().put(kc ,
                new Runnable() {
                    @FXML public void run() {
                        compileButton.fire();
                    }
                }
        );
    }

    public void compileCode(ActionEvent event){
        try
        {
            String name = tabpane.getSelectionModel().getSelectedItem().getText() + ".java";

            String contents = getCode();

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "utf-8"));
            writer.write(contents);
            writer.close();

            String cmd = compilerField.getText();
            String arg =  FileSystems.getDefault().getPath("").toAbsolutePath() + "/" + name ;
            System.out.println("Trying to execute string: " + cmd + " " + arg);
            Process p = Runtime.getRuntime().exec(new String[] {cmd, arg});
            p.waitFor();

            System.out.println("");
            System.out.println("Outputs:");
            String results = "Outputs:\n";
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
                results += line + "\n";
            }

            System.out.println("");
            System.out.println("Errors: ");
            results += "\nErrors:\n";
            r = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
                results += line + "\n";
            }

            System.out.println("");
            System.out.println("Exit code:" + p.exitValue());
            results += "\nExit code: " + p.exitValue();

            ScrollPane sa = new ScrollPane();
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initOwner((Stage) saveButton.getScene().getWindow()); // Sets to main stage
            VBox dialogVbox = new VBox(20);

            Text t = new Text("File compilation results");
            Text res = new Text(results);
            dialogVbox.getChildren().add(t);
            dialogVbox.getChildren().add(res);

            //If compilation was successful, offer to run it
            if (p.exitValue() == 0)
            {
                Button b = new Button();
                b.setText("Run " + name.replace(".java", "") + "?");
                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        runCompiledProgram(FilenameUtils.getBaseName(name));
                    }
                });
                dialogVbox.getChildren().add(b);
            }
            sa.setContent(dialogVbox);

            Scene dialogScene = new Scene(sa, 400, 200);
            dialog.setScene(dialogScene);
            dialog.show();

        }catch(Exception e){
            System.out.print("error: " + e);
        }
    }

    //Assumes we are already in the directory of the .class file
    //Name fed must not be the path, but the basename
    // /usr/bin/file1
    //CWD should be /usr/bin/ already
    //Name should be file1
    public void runCompiledProgram(String name)
    {
        try {
            String cmd = javaField.getText();
            System.out.println("Trying to execute java program: " + cmd + " " + name);
            Process p = Runtime.getRuntime().exec(new String[]{cmd, name});
            p.waitFor();

            System.out.println("");
            System.out.println("Outputs:");
            String results = "Outputs:\n";
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
                results += line + "\n";
            }

            System.out.println("");
            System.out.println("Errors: ");
            results += "\nErrors:\n";
            r = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
                results += line + "\n";
            }

            System.out.println("");
            System.out.println("Exit code:" + p.exitValue());
            results += "\nExit code: " + p.exitValue();

            ScrollPane sa = new ScrollPane();
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            dialog.initOwner((Stage) saveButton.getScene().getWindow()); // Sets to main stage
            VBox dialogVbox = new VBox(20);

            Text t = new Text("Java execution results");
            Text res = new Text(results);

            dialogVbox.getChildren().add(t);
            dialogVbox.getChildren().add(res);
            sa.setContent(dialogVbox);

            Scene dialogScene = new Scene(sa, 300, 300);
            dialog.setScene(dialogScene);
            dialog.show();

        } catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }


    private Tab newtab = new Tab("+");

    private void setupTabPane(){

        Tab tab = new Tab("HelloWorld");
        WebView w = new WebView();
        applyTemplate(w, initCode);
        tab.setContent(w);

        newtab.setOnSelectionChanged(event);
        tabpane.getTabs().addAll(tab,newtab);
    }

    EventHandler<Event> event =
            new EventHandler<Event>() {

                public void handle(Event e)
                {
                    if (newtab.isSelected())
                    {

                        // create Tab
                        WebView w = new WebView();
                        applyTemplate(w,initCode);
                        Tab tab = new Tab("New Text");
                        tab.setContent(w);

                        // add tab
                        tabpane.getTabs().add(
                                tabpane.getTabs().size() - 1, tab);

                        // select the last tab
                        tabpane.getSelectionModel().select(
                                tabpane.getTabs().size() - 2);
                    }
                }
            };


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
                    "  <link rel=\"stylesheet\" href=\"/opt/local/lib/node_modules/codemirror/lib/codemirror.css\">" +
                    "  <script src=\"/opt/local/lib/node_modules/codemirror/lib/codemirror.js\"></script>" +
                    "  <script src=\"/opt/local/lib/node_modules/codemirror/mode/clike/clike.js\"></script>" +
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

    private final String onlineTemplate =
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
        setupTabPane();
        setupCompile();
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

            Tab tab = new Tab(f.getName());

            // We need to create a new webview, as each view belongs to an individual tab
            try {
                WebView w = new WebView();

                String toWrite = new String(Files.readAllBytes(p));
                applyTemplate(w, toWrite);

                tab.setContent(w);
                tabpane.getTabs().add(tabpane.getTabs().size() - 1, tab);
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
    
     public void incFont(ActionEvent actionEvent)
    {
        Tab incTab = tabpane.getSelectionModel().getSelectedItem();
        WebView myWeb = (WebView) incTab.getContent();
        myWeb.setFontScale(myWeb.getFontScale() * 1.1);
    }
    
    public void decFont(ActionEvent actionEvent)
    {
       Tab decTab = tabpane.getSelectionModel().getSelectedItem();
       WebView myWeb = (WebView) decTab.getContent();
       myWeb.setFontScale(myWeb.getFontScale() * 0.90);
    }
    
    public void printFile(ActionEvent actionEvent)
    {
        Tab pTab = tabpane.getSelectionModel().getSelectedItem();
        WebView file = (WebView) pTab.getContent();
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job != null && job.showPrintDialog(null)){
            
            PageLayout pageLayout = job.getPrinter().getDefaultPageLayout();
            double scaleX = pageLayout.getPrintableWidth() / file.getBoundsInParent().getWidth();
            double scaleY = pageLayout.getPrintableHeight() / file.getBoundsInParent().getHeight();
            double minimumScale = Math.min(scaleX, scaleY);
            Scale scale = new Scale(minimumScale, minimumScale);
      
            file.getTransforms().add(scale);
        
            boolean success = job.printPage(file);
            if (success){
                job.endJob();
            }
            
        }
    }

    // Applies the template to the editing code to create the html+javascript source
    private void applyTemplate(WebView w, String in) {
        List<String> lines = Arrays.asList(template.replace("${code}", in));
        Path file = Paths.get("temp.html").toAbsolutePath();
        //Path f2 = Paths.get("C:\\Users\\etter\\IdeaProjects\\textEditor_CSCI3033\\out\\production\\textEditor_CSCI3033temp.html");
        Path f2 = Paths.get("../textEditor_CSCI3033/temp.html");

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);

            Files.write(f2, lines, StandardCharsets.UTF_8);

            URL u = getClass().getResource("/temp.html");

            // Can be null if resources not set correctly, so fallback to online
            if (u != null && Files.exists(Paths.get("/opt/local/lib/node_modules/codemirror/lib/codemirror.css"))) {
                System.out.println("Using offline template");
                w.getEngine().load(u.toExternalForm());
            }
            else {
                System.out.println("Using online template");
                w.getEngine().loadContent(onlineTemplate.replace("${code}", in));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Returns the value of the code being edited through codemirror functions
    // Gets code from active tab
    private String getCode() {
        return (String ) ((WebView)tabpane.getSelectionModel().getSelectedItem().getContent()).getEngine().executeScript("editor.getValue();");
    }
}
