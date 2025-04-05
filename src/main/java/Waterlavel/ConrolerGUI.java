package Waterlavel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;


public class ConrolerGUI extends Application {
   
   
    @FXML
    private Label label;


    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello frist time , JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 800, 600);

        stage.setScene(scene);
        stage.setFullScreen(true);        // Force fullscreen
        stage.setAlwaysOnTop(true);       // Always on top (useful in kiosk)
        stage.setFullScreenExitHint("");  // Remove "Press ESC to exit full screen"
        stage.setResizable(false);  
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }


   public void initialize() {
      // Hint: initialize() will be called when the associated FXML has been completely loaded.
   }
}