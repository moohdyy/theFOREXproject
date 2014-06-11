/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamt4interface;

import forexstrategies.Testing;
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
        scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(getTopContent());
        borderPane.setLeft(getLeftContent());
        root.getChildren().add(borderPane);

        primaryStage.show();
    }
    private Label labelOHLC;
    private Label labelTRADES;
    File lastFile = new File(System.getProperty("user.dir"));

    public Node getTopContent() {
        Button ohlcBtn = new Button();
        ohlcBtn.setId("ohlcFileChooser");
        ohlcBtn.setText("Choose OHLC File");
        labelOHLC = new Label("");
        labelOHLC.setId("ohlcFileChosen");
        labelOHLC.setContentDisplay(ContentDisplay.LEFT);
        ohlcBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                if (lastFile != null) {
                    File existDirectory = lastFile.getParentFile();
                    fc.setInitialDirectory(existDirectory);
                }
                fc.setTitle("ohlcFile");
                lastFile = fc.showOpenDialog(new Popup());
                if (lastFile != null) {
                    io.setOhlcFileName(lastFile.getAbsolutePath());
                    labelOHLC.setText(lastFile.getAbsolutePath());
                } else {
                    showDialog("No file selected.");
                }
            }
        });

        Button tradesBtn = new Button();
        tradesBtn.setId("tradesFileChooser");
        tradesBtn.setText("Choose Trade File");
        labelTRADES = new Label("");
        labelTRADES.setId("ohlcFileChosen");
        labelTRADES.setContentDisplay(ContentDisplay.LEFT);
        tradesBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                if (lastFile != null) {
                    File existDirectory = lastFile.getParentFile();
                    fc.setInitialDirectory(existDirectory);
                }
                fc.setTitle("Trade File");
                lastFile = fc.showOpenDialog(new Popup());
                if (lastFile != null) {
                    io.setTradesFileName(lastFile.getAbsolutePath());
                    labelTRADES.setText(lastFile.getAbsolutePath());
                } else {
                    showDialog("No file selected.");
                }
            }
        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(ohlcBtn, labelOHLC, tradesBtn, labelTRADES);
        return vbox;
    }
    Button startSim;
    Button stopSim;
    static TextArea simOutput;
    StrategyProcesser sp;

    private Node getLeftContent() {
        simOutput = new TextArea();
        simOutput.setPrefWidth(800);
        simOutput.setPrefHeight(300);
        startSim = new Button();
        startSim.setId("startTrading");
        startSim.setText("Start");
        startSim.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (io.isValid()) {
                    startSim.setDisable(true);
                    stopSim.setDisable(false);
                    sp = new StrategyProcesser(io, new Testing());
                    sp.start();
                } else {
                    showDialog("Please select two files for processing");
                }
            }
        });
        stopSim = new Button();
        stopSim.setId("stopTrading");
        stopSim.setText("Stop");
        stopSim.setDisable(true);
        stopSim.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sp.stopThread();
                stopSim.setDisable(true);
                startSim.setDisable(false);
            }
        });

        VBox hbox = new VBox();
        hbox.getChildren().addAll(startSim,stopSim, simOutput);
        return hbox;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static void printToOutput(String text) {
        simOutput.appendText("\n" + text);
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
