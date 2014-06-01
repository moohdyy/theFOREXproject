/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamt4interface;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author Moohdyy
 */
public class JavaMT4Interface extends Application {

    final InputOrganizer io = new InputOrganizer();

    @Override
    public void start(Stage primaryStage) {

        Button ohlcBtn = new Button();
        ohlcBtn.setId("ohlcFileChooser");
        ohlcBtn.setText("Choose OHLC File");
        ohlcBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("ohlcFile");
                File ohlcFile = fc.showOpenDialog(new Popup());
                io.setOHLCFile(ohlcFile);
            }
        });

        Button tradesBtn = new Button();
        tradesBtn.setId("tradesFileChooser");
        tradesBtn.setText("Choose Trade File");
        tradesBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Trade File");
                File tradeFile = fc.showOpenDialog(new Popup());
                io.setTradesFile(tradeFile);
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(ohlcBtn);
        root.getChildren().add(tradesBtn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
