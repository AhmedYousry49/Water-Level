package Waterlavel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PrimaryController {

    private final SerialPortManager serialManager = new SerialPortManager();

    private ScheduledExecutorService scheduler;
    private volatile long lastResponseTime = 0;
    private static final long TIMEOUT_MS = 2000;
    private volatile long lastWaterlevelTime = 0;

    private static final long READWATERLEVEL_MS = 3000;

    private volatile long lastMoterstatusTime = 0;

    private static final long READMOTORSTATUS_MS = 5000;

    @FXML
    private Rectangle waterFill;
    @FXML
    private ProgressBar waterLevelBar;
    @FXML
    private Label waterLevelPercent;

    @FXML
    private Circle deviceIndicator;
    @FXML
    private Label deviceText;

    @FXML
    private Circle statusIndicator;
    @FXML
    private Label statusText;

    @FXML
    private Label pumpStatus;
    @FXML
    private Label lastUpdate;

    @FXML
    private RadioButton manualRadio;
    @FXML
    private RadioButton autoRadio;
    @FXML
    private ToggleGroup modeToggleGroup;

    @FXML
    private Button pumpOnBtn;
    @FXML
    private Button pumpOffBtn;
    @FXML
    private Button emergencyStopBtn;

    private boolean pumpRunning = false;
    private boolean emergencyStopped = false;

    @FXML
    public void initialize() {

        boolean opened = serialManager.openPort("/dev/ttyS0", 115200);
        if (!opened) {
            showMessageBox("Failed to open /dev/ttyS0");
            return;
        }

        lastResponseTime = System.currentTimeMillis() + (5 * TIMEOUT_MS);
        modeToggleGroup.selectToggle(manualRadio);
        updateUI(1); // Start at 0%
        setupListeners();
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter);
            Platform.runLater(() -> {
                lastUpdate.setText(formattedDateTime);
            });

            serialManager.sendData("SYNC\r\n");
            // showMessageBox("Writer SYNC.");

            if (currentTime - lastWaterlevelTime > READWATERLEVEL_MS) {
                serialManager.sendData("WATERLEVEL\r\n");
                lastWaterlevelTime = currentTime;

            }
            if (currentTime - lastMoterstatusTime > READMOTORSTATUS_MS) {
                serialManager.sendData("MOTORINFO\r\n");
                lastMoterstatusTime = currentTime;
            }
            if (currentTime - lastResponseTime > TIMEOUT_MS) {

                // showMessageBox("No response from device, check connection.");
                Platform.runLater(() -> {
                    // updateDeviceStatus("Disconnected", Color.RED);
                    setDerviceStatus(false);
                });
                // pumpStatus.setText("OFF");
                // pumpStatus.setTextFill(Color.RED);
                // emergencyStopped = true;
                // setPumpStatus(false);
            } else {
                Platform.runLater(() -> {
                    setDerviceStatus(true);
                });
            }
        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        serialManager.setDataListener(data -> {
            Platform.runLater(() -> {
                System.out.print(" Received: " + data);
                exexcuteCommand(data);
            });
        });

        // // Handle Send button click
        // sendButton.setOnAction(event -> {
        // String text = inputField.getText();
        // if (!text.isEmpty()) {
        // serialManager.sendData(text + "\n");
        // inputField.clear();
        // }
        // });

    }

    public void setDerviceStatus(boolean status) {
        if (status) {
            updateDeviceStatus("Connected", Color.GREEN);

        } else {
            updateDeviceStatus("Disconnected", Color.RED);

        }

        if (autoRadio.isSelected()) {

            pumpOnBtn.setDisable(true);
            pumpOffBtn.setDisable(true);
        } else {
            pumpOnBtn.setDisable(!status);
            pumpOffBtn.setDisable(!status);

        }
        emergencyStopBtn.setDisable(!status);
        autoRadio.setDisable(!status);
        manualRadio.setDisable(!status);
    }

    public void showMessageBox(String message) {
        // Show an alert dialog
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION); // Information type
            alert.setTitle("Sync Message");
            alert.setHeaderText(null); // No header
            alert.setContentText(message); // Message content
            alert.showAndWait(); // Show the alert and wait for user action
        });
    }

    public void showwhowMessageBox(String message) {
        // Show an alert dialog
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION); // Information type
            alert.setTitle("Sync Message");
            alert.setHeaderText(null); // No header
            alert.setContentText(message); // Message content
            // alert.wait(TIMEOUT_MS);
            alert.showAndWait(); // Show the alert and wait for user action
        });
    }

    private void exexcuteCommand(String commString) {
        String data = commString.trim();

        if (data.equals("SYNC")) {
            lastResponseTime = System.currentTimeMillis();
        }
        if (data.equals("ACK")) {
            // Handle ACK
            System.out.println("Acknowledgment received: " + data);
            Platform.runLater(() -> {
                // pumpStatus.setText("ACK");
                // pumpStatus.setTextFill(Color.BLUE);
            });
        } else if (data.startsWith("MOTOR")) {
            Boolean motorStatus = Integer.parseInt(data.substring(data.indexOf('[') + 1, data.indexOf(']'))) != 0;
            System.out.println("MOTOR Status: " + motorStatus);
            Platform.runLater(() -> {
                setPumpStatus(motorStatus);
            });

        } else if (data.startsWith("WATER")) {
            // Handle WATER level
            try {
                int setlevel;
                int waterLevel = Integer.parseInt(data.substring(data.indexOf('[') + 1, data.indexOf(']')));
                System.out.println("Water Level: " + waterLevel);

                // Update UI based on water level

                if (waterLevel <= 600) {
                    setlevel = 0;
                } else if (waterLevel <= 700) {
                    setlevel = 1;

                } else {
                    setlevel = 2;
                }

                Platform.runLater(() -> {
                    int levelToUpdate = setlevel;
                    updateUI(levelToUpdate);
                });
            } catch (NumberFormatException e) {
                System.err.println("Invalid water level format: " + data);
            }
        } else if (data.equalsIgnoreCase("ok")) {
            // Handle OK
            serialManager.sendData("MOTORINFO\r\n");
            System.out.println("Device status: OK");
            Platform.runLater(() -> {
                pumpStatus.setText("OK");
                pumpStatus.setTextFill(Color.GREEN);
            });
        } else if (data.toLowerCase().contains("version")) {
            // Handle Version
            System.out.println("Device Version Info: " + data);
            Platform.runLater(() -> {
                statusText.setText(data);
                statusText.setTextFill(Color.PURPLE);
            });
        } else {
            System.out.println("Unrecognized command: " + data);
        }
    }

    private void setupListeners() {
        modeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == autoRadio) {
                pumpOnBtn.setDisable(true);
                pumpOffBtn.setDisable(true);
            } else {
                pumpOnBtn.setDisable(false);
                pumpOffBtn.setDisable(false);
            }
        });
    }

    @FXML
    public void handlePumpOn() {
        // if (!emergencyStopped && manualRadio.isSelected())
        {
            setPumpStatus(true);
        }
    }

    @FXML
    public void handlePumpOff() {
        if (manualRadio.isSelected()) {
            setPumpStatus(false);
        }
    }

    @FXML
    public void handleEmergencyStop() {
        emergencyStopped = true;
        setPumpStatus(false);
        pumpStatus.setText("EMERGENCY");
        pumpStatus.setTextFill(Color.RED);
    }

    @FXML
    public void handleManualSelected() {
        pumpOnBtn.setDisable(false);
        pumpOffBtn.setDisable(false);
    }

    @FXML
    public void handleAutoSelected() {
        pumpOnBtn.setDisable(true);
        pumpOffBtn.setDisable(true);
    }

    @FXML
    public void updateUI(int level) {
        switch (level) {
            case 0:
                updateWaterLevel(0.0);
                updateTankStatus("Low", Color.RED);
                break;
            case 1:
                updateWaterLevel(0.5);
                updateTankStatus("Medium", Color.ORANGE);
                break;
            case 2:
                updateWaterLevel(1.0);
                updateTankStatus("Full", Color.GREEN);
                break;
        }

        if (autoRadio.isSelected()) {
            if (level == 0 && !emergencyStopped)
                setPumpStatus(true);
            else if (level == 1 && !emergencyStopped)
                setPumpStatus(true);
            else if (level == 2)
                setPumpStatus(false);
        }
    }

    public void updateWaterLevel(double percentage) {

        waterLevelPercent.setText((percentage * 100) + "%");
        // Clamp value between 0 and 1
        double clampedPercentage = Math.max(0, Math.min(1, percentage));

        // Set progress bar
        waterLevelBar.setProgress(clampedPercentage);

        // Set rectangle height 
        double maxHeight = 300;
        double newHeight = clampedPercentage * maxHeight;
        waterFill.setHeight(newHeight);

    }

    private void updateTankStatus(String status, Color color) {
        statusText.setText(status);
        statusText.setTextFill(color);
        statusIndicator.setFill(color);
    }

    private void updateDeviceStatus(String status, Color color) {
        deviceText.setText(status);
        deviceText.setTextFill(color);
        deviceIndicator.setFill(color);
    }

    private void setPumpStatus(boolean running) {
        pumpRunning = running;
        pumpStatus.setText(running ? "ON" : "OFF");

        serialManager.sendData(running ? "MOTOR 90 \r\n" : "MOTOR 0 \r\n");
        pumpStatus.setTextFill(running ? Color.GREEN : Color.RED);
    }

    // To be used with UART input processing
    public void processSensorInput(String input) {
        if (input.startsWith("LEVEL:")) {
            int level = Integer.parseInt(input.substring(6));
            updateUI(level);
        }
    }
}
