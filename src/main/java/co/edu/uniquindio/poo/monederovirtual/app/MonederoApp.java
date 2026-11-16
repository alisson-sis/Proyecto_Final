package co.edu.uniquindio.poo.monederovirtual.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MonederoApp extends Application {
    private static final String BASE = "/co/edu/uniquindio/poo/monederovirtual/";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MonederoApp.class.getResource(BASE + "principal.fxml"));
        Scene scene = new Scene(loader.load(), 1120, 740);
        scene.getStylesheets().add(Objects.requireNonNull(MonederoApp.class.getResource(BASE + "style.css")).toExternalForm());
        stage.setTitle("🌸 Monedero Kawaii");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}