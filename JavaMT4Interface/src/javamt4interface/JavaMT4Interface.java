/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamt4interface;

import forexstrategies.JapaneseCandlesticksStrategy;
import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import simulation.StrategyProcesser;

/**
 *
 * @author Moohdyy
 */
public class JavaMT4Interface extends Application {

    final InputOrganizer io = new InputOrganizer();
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        primaryStage.setTitle("The Forex Project");
        scene = new Scene(root, 500, 250);
        primaryStage.setScene(scene);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(getTopContent());
        borderPane.setLeft(getLeftContent());
        root.getChildren().add(borderPane);

        primaryStage.show();
    }

    public Node getTopContent() {

        Button ohlcBtn = new Button();
        ohlcBtn.setId("ohlcFileChooser");
        ohlcBtn.setText("Choose OHLC File");
        Label labelOHLC = new Label("", ohlcBtn);
        labelOHLC.setId("ohlcFileChosen");
        labelOHLC.setContentDisplay(ContentDisplay.LEFT);
        ohlcBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                //Set to user directory or go to default if cannot access
                String userDirectoryString = System.getProperty("user.home");
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.canRead()) {
                    userDirectory = new File("c:/");
                }
                fc.setInitialDirectory(userDirectory);
                fc.setTitle("ohlcFile");
                File ohlcFile = fc.showOpenDialog(new Popup());
                if (ohlcFile != null) {
                    io.setOhlcFile(ohlcFile);
                    labelOHLC.setText(ohlcFile.getAbsolutePath());
                } else {
                    showDialog("No file selected.");
                }
            }
        });

        Button tradesBtn = new Button();
        tradesBtn.setId("tradesFileChooser");
        tradesBtn.setText("Choose Trade File");
        Label labelTRADES = new Label("", tradesBtn);
        labelTRADES.setId("ohlcFileChosen");
        labelTRADES.setContentDisplay(ContentDisplay.LEFT);
        tradesBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                //Set to user directory or go to default if cannot access
                String userDirectoryString = System.getProperty("user.home");
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.canRead()) {
                    userDirectory = new File("c:/");
                }
                fc.setInitialDirectory(userDirectory);
                fc.setTitle("Trade File");
                File tradeFile = fc.showOpenDialog(new Popup());
                if (tradeFile != null) {
                    io.setTradesFile(tradeFile);
                    labelTRADES.setText(tradeFile.getAbsolutePath());
                } else {
                    showDialog("No file selected.");
                }
            }
        });

        VBox hbox = new VBox();
        hbox.getChildren().addAll(ohlcBtn, labelOHLC, tradesBtn, labelTRADES);
        return hbox;
    }

    Button startSim;
    static TextArea simOutput;

    private Node getLeftContent() {
        simOutput = new TextArea();
        simOutput.setPrefRowCount(10);
        simOutput.setPrefColumnCount(100);
        simOutput.setWrapText(true);
        simOutput.setPrefWidth(500);
        startSim = new Button();
        startSim.setId("startTrading");
        startSim.setText("Start");
        startSim.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (io.isValid()) {
                    startSim.setDisable(true);
                    StrategyProcesser sp = new StrategyProcesser(io, new JapaneseCandlesticksStrategy());
                    sp.startProcessing();
                } else {
                    showDialog("Please select two files for processing");
                }
            }
        });

        VBox hbox = new VBox();
        hbox.getChildren().addAll(startSim, simOutput);
        return hbox;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void printToOutput(String text) {
        simOutput.appendText(text);
    }

    public void showDialog(String message) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(VBoxBuilder.create().
                children(new Text(message)).
                alignment(Pos.CENTER).padding(new Insets(5)).build()));
        dialogStage.show();
    }
}
