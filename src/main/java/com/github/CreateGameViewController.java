package com.github;

import com.github.db.Platform;
import com.github.db.Videogame;
import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Logger;

/**
 * @author Kevin Fernandez
 */
public class CreateGameViewController {
    private static final Logger LOGGER = Logger.getLogger(CreateGameViewController.class);

    @FXML
    private Label txtError;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtID;
    @FXML
    private DatePicker dateRelease;
    @FXML
    private TextField txtPreu;
    @FXML
    private ChoiceBox<Platform> choicePlatform;

    /**
     * Adding available platforms to the choiceBox
     */
    public void initialize() {
        for (Platform platform : Platform.values()) {
            choicePlatform.getItems().add(platform);
        }
    }

    /**
     * Validate the form and try to add to bd<br>
     * Give feedback to user
     */
    @FXML
    private void clickBtnAdd() {
        try {
            String name = txtNom.getText();

            if (txtID.getText().isBlank()) {
                txtError.setText("La ID no puede estar vacia.");
                return;
            }

            int id = Integer.parseInt(txtID.getText());
            Platform platform = choicePlatform.getValue();

            if (dateRelease.getValue() == null) {
                txtError.setText("La data no puede estar vacia.");
                return;
            }
            java.sql.Date date = java.sql.Date.valueOf(dateRelease.getValue());

            if (txtPreu.getText().isBlank()) {
                txtError.setText("El precio no puede estar vacio.");
                return;
            }

            int preu = Integer.parseInt(txtPreu.getText());

            Videogame newVideoGame = new Videogame(id, name, platform, date, preu);

            App.gestorPersistencia.getVideogameDAO().insert(newVideoGame);
            ClientListViewController.stageCreate.close();
            ClientListViewController.stageCreate = null;
        } catch (DuplicatedException e) {
            LOGGER.debug(e);
            txtError.setText("Ya existe un cliente con este ID.");
        } catch (DatabaseException e) {
            txtError.setText("Error interno de la base de datos.");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
        } catch (NumberFormatException e) {
            txtError.setText("La ID no puede estar vacia y debe ser un numero.");
        }
    }

    /**
     * close the windows
     */
    @FXML
    private void clickBtnBack() {
        ClientListViewController.stageCreate.close();
        ClientListViewController.stageCreate = null;
    }

}
