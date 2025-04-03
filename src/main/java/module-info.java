module Waterlavel {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    opens Waterlavel to javafx.fxml;
    exports Waterlavel;
}
