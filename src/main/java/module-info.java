module co.edu.uniquindio.poo.monederovirtual {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.poo.monederovirtual.viewController to javafx.fxml;
    exports co.edu.uniquindio.poo.monederovirtual.app;
    exports co.edu.uniquindio.poo.monederovirtual.model;
    exports co.edu.uniquindio.poo.monederovirtual.controller;
    exports co.edu.uniquindio.poo.monederovirtual.viewController;
}