package Waterlavel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;

public class Launcher extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("primary"), 800, 600);

        stage.setScene(scene);

        stage.setFullScreen(true); // Force fullscreen
        stage.setAlwaysOnTop(true); // Always on top (useful in kiosk)
        stage.setFullScreenExitHint(""); // Remove "Press ESC to exit full screen"
        stage.setResizable(false);

        stage.show();

        // stage.setOnCloseRequest(event -> loader.getController().shutdown());
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        // SerialPort[] ports = SerialPort.getCommPorts();
        // System.out.println("Available ports:");
        // for (int i = 0; i < ports.length; i++) {
        // System.out.println(i + ": " + ports[i].getSystemPortName());
        // }
        launch();
    }

}