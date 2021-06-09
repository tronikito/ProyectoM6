package com.github.db;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

/**
 * Representa un cliente.
 * @author Edgar Luque
 */
public class Client implements Comparable<Client> {
    /**
     * El dni.
     */
    private int id;
    private String name;
    private String country;
    private Date createdAt;
    private boolean isPartner;
    private List<Videogame> videogames;
    private ReadOnlyBooleanProperty booleanWrapper = new ReadOnlyBooleanWrapper(false);

    public Client(int id, String name, String country, Date createdAt, boolean isPartner) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.createdAt = createdAt;
        this.isPartner = isPartner;
        videogames = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public ReadOnlyBooleanProperty changeIndicatorProperty() {
        return booleanWrapper;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPartner() {
        return isPartner;
    }

    public void setPartner(boolean partner) {
        isPartner = partner;
    }

    public List<Videogame> getVideogames() {
        return videogames;
    }

    public void setVideogames(List<Videogame> videogames) {
        this.videogames = videogames;
    }

    @Override
    public int compareTo(Client client) {
        return Integer.compare(getId(), client.getId());
    }
}
