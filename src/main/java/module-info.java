module Waterlavel {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fazecast.jSerialComm;
    
    opens Waterlavel to javafx.fxml;
    exports Waterlavel;
}
