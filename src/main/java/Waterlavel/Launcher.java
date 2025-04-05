package Waterlavel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fazecast.jSerialComm.SerialPort;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Launcher extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {


      
        scene = new Scene(loadFXML("primary"), 800, 600);

        stage.setScene(scene);
     /*    stage.setFullScreen(true);        // Force fullscreen
        stage.setAlwaysOnTop(true);       // Always on top (useful in kiosk)
        stage.setFullScreenExitHint("");  // Remove "Press ESC to exit full screen"
        stage.setResizable(false);  */
        stage.show();

        //stage.setOnCloseRequest(event -> loader.getController().shutdown());
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

      SerialPort[] ports = SerialPort.getCommPorts();
      System.out.println("Available ports:");
      for (int i = 0; i < ports.length; i++) {
          System.out.println(i + ": " + ports[i].getSystemPortName());
      }

/* /
      SerialPort serialPort = SerialPort.getCommPort("/dev/ttyACM0");
      serialPort.setComPortParameters(115200, 8, 1, 0);
      serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
      if (serialPort.openPort()) {
          System.out.println("Port is open");
      } else {

          System.out.println("Failed to open port");
      }

          System.out.println("Port opened: " + serialPort.getSystemPortName());

        try {
          
          OutputStream out = serialPort.getOutputStream();

          InputStream in = serialPort.getInputStream();
          while (true) {
          
            
        
            // Write some data
            String message = "SYNC\\r\\n";
            out.write(message.getBytes());
            out.flush();
            System.out.println("Sent: " + message);

            // Read a response (blocking read)
            byte[] buffer = new byte[1024];
            int numRead = in.read(buffer);
            if (numRead > 0) {
                String received = new String(buffer, 0, numRead);
                System.out.println("Received: " + received);
            }


          }

            // Close the streams and port
            // in.close();
            // out.close();
            // serialPort.closePort();
            // System.out.println("Port closed.");

        } catch (IOException e) {
            e.printStackTrace();
        }*/
      
        launch();
    }

}