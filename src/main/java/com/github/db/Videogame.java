package com.github.db;

import java.sql.Date;

/**
 * Representa un videojuego.
 * @author Edgar Luque
 */
public class Videogame implements Comparable<Videogame> {
    private int id;
    private String name;
    private Platform platform;
    private Date releaseDate;
    private int price;

    public Videogame(int id, String name, Platform platform, Date releaseDate, int price) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.releaseDate = releaseDate;
        this.price = price;
    }

    public int getId() {
        return id;
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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int compareTo(Videogame videogame) {
        return Integer.compare(getId(), videogame.getId());
    }
}
