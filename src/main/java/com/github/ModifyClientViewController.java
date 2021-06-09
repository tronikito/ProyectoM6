package com.github;

import com.github.db.Client;
import com.github.db.Platform;
import com.github.db.Videogame;
import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import com.github.exceptions.NotFoundException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Fernandez
 */
public class ModifyClientViewController {

    private static final Logger LOGGER = Logger.getLogger(ModifyClientViewController.class);

    public static Client client;

    @FXML
    private TextField txtNomGame;
    @FXML
    private TextField txtIDGame;
    @FXML
    Button btnCancel;
    @FXML
    Button btnSave;
    @FXML
    Button btnRemove;
    @FXML
    TextField txtNomClient;
    @FXML
    TextField txtIDClient;
    @FXML
    RadioButton radioYes;
    @FXML
    RadioButton radioNo;
    @FXML
    private ChoiceBox<Platform> choicePlatform;
    @FXML
    private TableView<Videogame> tableClient;
    @FXML
    private TableView<Videogame> tableGame;
    @FXML
    private TableColumn colIDclientgame;
    @FXML
    private TableColumn colNameclientgame;
    @FXML
    private TableColumn colPlatformclientgame;
    @FXML
    private TableColumn colPriceclientgame;

    @FXML
    private TableColumn colID;
    @FXML
    private TableColumn colName;
    @FXML
    private TableColumn colPlatform;
    @FXML
    private TableColumn colPrice;
    @FXML
    private RadioButton radioName;
    @FXML
    private RadioButton radioID;
    @FXML
    private RadioButton radioPlatform;
    @FXML
    private Button btnFindAll;

    private final ToggleGroup group = new ToggleGroup();
    private String selectedFilter = "";

    private List<Videogame> videogamesClient;
    public ObservableList<Videogame> videogamesClientObservableList;

    private List<Videogame> videogames;
    public ObservableList<Videogame> videogamesObservableList;

    /**
     * check for radiobutton checked in filters options
     */
    @FXML
    private void clickBtnFind() {
        if (radioPlatform.isSelected()) {
            refreshTableGames(choicePlatform.getValue());
        } else if (radioID.isSelected()) {
            refreshTableGames(txtIDGame.getText());
        } else if (radioName.isSelected()) {
            refreshTableGames(txtNomGame.getText());
        }

    }

    /**
     * manage radiobutton group
     */
    private void clickRadio(String radio) {
        if (radio.equals("radioName")) {
            txtIDGame.setDisable(true);
            txtNomGame.setDisable(false);
            choicePlatform.setDisable(true);

        } else if (radio.equals("radioID")) {
            txtIDGame.setDisable(false);
            txtNomGame.setDisable(true);
            choicePlatform.setDisable(true);
        } else if (radio.equals("radioPlatform")) {
            txtIDGame.setDisable(true);
            txtNomGame.setDisable(true);
            choicePlatform.setDisable(false);

        }
    }

    /**
     * Every default option and button event
     */
    public void initialize() {

        for (Platform platform : Platform.values()) {
            choicePlatform.getItems().add(platform);
        }

        choicePlatform.getSelectionModel().selectFirst();

        LOGGER.debug("Videogames Size" + client.getVideogames().size());
        txtNomClient.setText(client.getName());
        txtIDClient.setText(client.getId() + "");

        if (client.isPartner()) {
            radioYes.setSelected(true);
        } else {
            radioNo.setSelected(true);
        }

        radioID.setToggleGroup(group);
        radioName.setToggleGroup(group);
        radioPlatform.setToggleGroup(group);

        /** DEFAULT OPTION **/
        radioPlatform.setSelected(true);
        clickRadio("radioPlatform");

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                RadioButton chk = (RadioButton) radioName.getToggleGroup().getSelectedToggle(); // Cast object to radio button
                LOGGER.debug("Selected - " + chk.getId());
                clickRadio(chk.getId());
            }
        });

        colIDclientgame.setCellValueFactory(new PropertyValueFactory<Videogame, Integer>("id"));
        colNameclientgame.setCellValueFactory(new PropertyValueFactory<Videogame, String>("name"));
        colPlatformclientgame.setCellValueFactory(new PropertyValueFactory<Videogame, String>("platform"));
        colPriceclientgame.setCellValueFactory(new PropertyValueFactory<Videogame, Date>("price"));

        colID.setCellValueFactory(new PropertyValueFactory<Videogame, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Videogame, String>("name"));
        colPlatform.setCellValueFactory(new PropertyValueFactory<Videogame, String>("platform"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Videogame, Date>("price"));

        refreshTableClient();
        refreshTableGames();

        radioName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        radioName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        radioName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

        btnRemove.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Videogame videogameSelected;
                if (tableClient.getSelectionModel().getSelectedItem() != null) {
                    videogameSelected = client.getVideogames().get(tableClient.getSelectionModel().getSelectedIndex());
                } else {
                    videogameSelected = client.getVideogames().get((int)tableClient.getItems().stream().count()-1);
                }
                    LOGGER.debug("videogame seleccionado" + videogameSelected.toString());

                    /*falta añadir objeto bbdd*/
                    /* lo haremos con un botón de guardar cambios */

                    client.getVideogames().remove(videogameSelected);

                    refreshTableClient();
            }
        });

        btnCancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clientListWindow();
            }
        });

        btnFindAll.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                refreshTableGames();
            }
        });
        btnSave.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    App.gestorPersistencia.getClientDAO().update(client);
                } catch (DatabaseException e) {
                    //txtError.setText("Error interno de la base de datos.");
                    LOGGER.error("error interno base datos");
                    LOGGER.error(e);
                    LOGGER.error(e.getCause());
                } catch (NotFoundException e) {
                    //txtError.setText("Error interno de la base de datos.");
                    LOGGER.error("error datos no encontrados");
                    LOGGER.error(e);
                    LOGGER.error(e.getCause());
                } catch (DuplicatedException e) {
                    e.printStackTrace();
                }

                LOGGER.debug("Videogames Size" + client.getVideogames().size());

                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ClientListView" + ".fxml"));
                Scene scene;
                try {
                    scene = new Scene(fxmlLoader.load(), 535, 656);
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.setTitle("Clients List");
                    stage.setResizable(false);
                    stage.setScene(scene);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }

        });

        tableGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    if (tableGame.getSelectionModel().getSelectedItem() != null) {
                        Videogame videogameSelected = videogames.get(tableGame.getSelectionModel().getSelectedIndex());
                        LOGGER.debug("videogame seleccionado" + videogameSelected.toString());

                        /*falta añadir objeto bbdd*/
                        /* lo haremos con un botón de guardar cambios */

                        client.getVideogames().add(videogameSelected);

                        refreshTableClient();
                    }
                }
            }
        });
    }

    /**
     * refreshTableGames without filters
     */
    public void refreshTableGames() {
        try {
            videogames = new ArrayList<>(App.gestorPersistencia.getVideogameDAO().getAll());
            videogamesObservableList = FXCollections.observableArrayList(videogames);
            tableGame.setItems(videogamesObservableList);

        } catch (DatabaseException e) {
            LOGGER.error("error al obtener los videogames");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
            e.printStackTrace();
        }
    }
    /**
     * refreshTableGames with filter platform
     */
    public void refreshTableGames(Platform platform) {

        try {
            videogames = new ArrayList<>(App.gestorPersistencia.getVideogameDAO().getByPlatform(platform));
            videogamesObservableList = FXCollections.observableArrayList(videogames);
            tableGame.setItems(videogamesObservableList);

        } catch (DatabaseException e) {
            LOGGER.error("error al obtener los videogames");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * refreshTableGames with filter id
     */
    public void refreshTableGames(int id) {

        try {
            Videogame newVi = App.gestorPersistencia.getVideogameDAO().getByID(id);
            ArrayList<Videogame> newList = new ArrayList<Videogame>();
            newList.add(newVi);
            videogames = newList;
            videogamesObservableList = FXCollections.observableArrayList(videogames);
            tableGame.setItems(videogamesObservableList);

        } catch (DatabaseException | NotFoundException e) {
            LOGGER.error("error al obtener los videogames");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * refreshTableGames with filter name
     */
    public void refreshTableGames(String name) {

        try {
            videogames = new ArrayList<>(App.gestorPersistencia.getVideogameDAO().getByName(name));
            videogamesObservableList = FXCollections.observableArrayList(videogames);
            tableGame.setItems(videogamesObservableList);

        } catch (DatabaseException e) {
            LOGGER.error("error al obtener los videogames");
            LOGGER.error(e);
            LOGGER.error(e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * refreshTableClient without filter
     */
    public void refreshTableClient() {

        videogamesClient = client.getVideogames();
        videogamesClientObservableList = FXCollections.observableArrayList(videogamesClient);
        tableClient.setItems(videogamesClientObservableList);
    }
    /**
     * Go back function calling ClientListView window
     */
    private void clientListWindow() {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ClientListView" + ".fxml"));
        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load(), 535, 656);
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.setTitle("Clients List");
            stage.setResizable(false);
            stage.setScene(scene);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
