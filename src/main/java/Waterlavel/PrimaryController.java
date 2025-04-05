package Waterlavel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PrimaryController {

    @FXML private Rectangle waterFill;
    @FXML private ProgressBar waterLevelBar;
    @FXML private Label waterLevelPercent;

    @FXML private Circle statusIndicator;
    @FXML private Label statusText;

 
    @FXML private Label pumpStatus;
     @FXML private Label lastUpdate;

    @FXML private RadioButton manualRadio;
    @FXML private RadioButton autoRadio;
    @FXML private ToggleGroup modeToggleGroup;

    @FXML private Button pumpOnBtn;
    @FXML private Button pumpOffBtn;
    @FXML private Button emergencyStopBtn;

    private boolean pumpRunning = false;
    private boolean emergencyStopped = false;

    @FXML
    public void initialize() {
        modeToggleGroup.selectToggle(manualRadio);
        updateUI(1); // Start at 0%
        setupListeners();
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
        if (!emergencyStopped && manualRadio.isSelected()) {
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
            if (level == 0 && !emergencyStopped) setPumpStatus(true);
            else if (level == 2) setPumpStatus(false);
        }
    }

    public void updateWaterLevel(double percentage) {
    // Clamp value between 0 and 1
    double clampedPercentage = Math.max(0, Math.min(1, percentage));

    // Set progress bar
    waterLevelBar.setProgress(clampedPercentage);

    // Set rectangle height (max 300 as your tank height)
    double maxHeight = 300;
    double newHeight = clampedPercentage * maxHeight;
    waterFill.setHeight(newHeight);

}

    private void updateTankStatus(String status, Color color) {
        statusText.setText(status);
        statusText.setTextFill(color);
        statusIndicator.setFill(color);
    }

    private void setPumpStatus(boolean running) {
        pumpRunning = running;
        pumpStatus.setText(running ? "ON" : "OFF");
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
