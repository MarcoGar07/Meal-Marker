module edu.utsa.cs.mealmarker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;

    opens edu.utsa.cs3443.mealmarker to javafx.fxml;
    exports edu.utsa.cs3443.mealmarker;
    exports model;
}