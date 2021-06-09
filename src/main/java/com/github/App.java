package com.github;

import com.github.db.ClientJDBCDAO;
import com.github.db.Conexio;
import com.github.db.GestorPersistencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class);
    private static Scene scene;
    public static Conexio conexio;
    public static GestorPersistencia gestorPersistencia;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("ConnectionView"), 339, 339);
        stage.setResizable(false);
        stage.setTitle("Logging");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        LOGGER.debug("Cargando fitxero fxml: " + fxml);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        launch();
    }

}