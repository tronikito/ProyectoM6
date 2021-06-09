package com.github;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import com.github.db.ConexioJDBC;
import com.github.db.ConexioMongo;
import com.github.db.GestorPersistenciaJDBC;
import com.github.db.GestorPersistenciaMongo;
import com.github.exceptions.DatabaseException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author Kevin Fernandez
 */

public class ConnectionViewController {
    private static final Logger LOGGER = Logger.getLogger(ConnectionViewController.class);

    @FXML
    private RadioButton radioSQL;
    @FXML
    private RadioButton radioNoSQL;
    @FXML
    private TextField txtHost;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtUser;
    @FXML
    private TextField txtPass;
    @FXML
    private Button btnLogin;
    @FXML
    private Label labelError;

    private final ToggleGroup group = new ToggleGroup();
    private String selectedBBDD = "";

    /**
     * Set FieldText text's to default connection settings
     * Save the radioButton selection
     * Limit FieldText of Port to 4 and numeric string
     * Control of button login to launch next View
     */
    public void initialize() {

        radioSQL.setToggleGroup(group);
        radioNoSQL.setToggleGroup(group);

        txtHost.setText("localhost");
        txtPort.setText("3306");
        txtUser.setText("root");
        txtPass.setText("");

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton chk = (RadioButton) radioSQL.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                LOGGER.debug("Selected - " + chk.getText());
                selectedBBDD = chk.getText();
            }
        });

        txtPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (txtPort.getText().length() > 4) {
                    txtPort.setText(txtPort.getText().substring(0, 4));
                }
                if (newValue.matches("\\d*")) {
                    try {
                        int value = Integer.parseInt(newValue);
                    } catch (Exception e) {
                        txtPort.setText("");
                    }

                } else {
                    txtPort.setText(oldValue + "");
                }
            }
        });
        btnLogin.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {

                Configuration configuration = new Configuration(
                        txtHost.getText(),
                        Integer.parseInt(txtPort.getText()),
                        txtUser.getText(),
                        txtPass.getText()
                );


                switch (selectedBBDD) {
                    case "MySQL":
                        try {
                            labelError.setTextFill(Color.GREEN);
                            labelError.setText("Connecting...");
                            App.conexio = new ConexioJDBC(configuration);
                            App.conexio.connect();
                            App.gestorPersistencia = new GestorPersistenciaJDBC((ConexioJDBC) App.conexio);
                            clientListWindow();
                        } catch (DatabaseException e) {
                            LOGGER.error(e);
                            LOGGER.error(e.getCause());
                            labelError.setTextFill(Color.RED);
                            labelError.setText("Error: Cannot connect to BBDD");
                        }

                        break;
                    case "MongoDB":

                        try {
                            labelError.setTextFill(Color.GREEN);
                            labelError.setText("Connecting...");
                            App.conexio = new ConexioMongo(configuration);
                            App.conexio.connect();
                            App.gestorPersistencia = new GestorPersistenciaMongo((ConexioMongo) App.conexio);
                            clientListWindow();
                        } catch (DatabaseException e) {
                            labelError.setTextFill(Color.RED);
                            labelError.setText("Error: Cannot connect to BBDD");
                        }

                        break;
                    case "":
                        labelError.setTextFill(Color.RED);
                        labelError.setText("Error: No BBDD type SELECTED");
                }
            }
        });
    }

    /**
     * Inicia el ClientListView
     */
    private void clientListWindow() {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ClientListView" + ".fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 535, 656);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("Lista de Clientes");
            stage.setResizable(false);
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
