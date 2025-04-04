module com.mycompany.gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.gui to javafx.fxml;
    exports com.mycompany.gui;
}
