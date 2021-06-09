package com.github;

import com.github.db.Client;
import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;

import java.sql.Date;

/**
 * @author Kevin Fernandez
 */
public class CreateClientViewController {
    private static final Logger LOGGER = Logger.getLogger(CreateClientViewController.class);

    @FXML
    private Button btnBack;
    @FXML
    private Button btnAdd;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtPais;

    @FXML
    private RadioButton radSi;

    @FXML
    private RadioButton radNo;

    @FXML
    private Label txtError;

    private final ToggleGroup group = new ToggleGroup();

    private boolean esSoci = false;

    /**
     * Control textID to limit length characters and only numeric
     */
    public void initialize() {
        radSi.setToggleGroup(group);
        radNo.setToggleGroup(group);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton chk = (RadioButton) radSi.getToggleGroup().getSelectedToggle();
                LOGGER.debug("Selected - " + chk.getText());
                esSoci = chk == radSi;
            }
        });

        txtID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (txtID.getText().length() > 4) {
                    txtID.setText(txtID.getText().substring(0, 4));
                }
                if (newValue.matches("\\d*")) {
                    try {
                        int value = Integer.parseInt(newValue);
                    } catch (Exception e) {
                        txtID.setText("");
                    }

                } else {
                    txtID.setText(oldValue + "");
                }
            }
        });
    }

    /**
     * Call for ClientDAO for add the new client
     * Add feedback information
     */
    @FXML
    private void clickBtnAdd() {
        try {
            if(txtNom.getText().isBlank()) {
                txtError.setText("El nombre no puede estar vacío.");
                return;
            }
            Client client = new Client(
                    Integer.parseInt(txtID.getText()),
                    txtNom.getText(),
                    txtPais.getText(),
                    new Date(System.currentTimeMillis()),
                    esSoci
            );

            App.gestorPersistencia.getClientDAO().insert(client);
            ClientListViewController.stageCreate.close();
            ClientListViewController.stageCreate = null;
        } catch (NumberFormatException e) {
            txtError.setText("El DNI no puede estar vacio y debe ser un numero.");
        } catch (DuplicatedException e) {
            LOGGER.debug(e);
            txtError.setText("Ya existe un cliente con este DNI.");
        } catch (DatabaseException e) {
            txtError.setText("Error interno de la base de datos.");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
        }
    }

    /**
     * Close the window
     */
    @FXML
    private void clickBtnBack() {
        ClientListViewController.stageCreate.close();
        ClientListViewController.stageCreate = null;
    }

}
