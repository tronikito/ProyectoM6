package com.github.db;

/**
 * Posibles plataformas para los videojuegos.
 * @author Edgar Luque
 */
public enum Platform {
    PC,
    PS3,
    PS4,
    PS5,
    Xbox,
    XboxSeriesX,
    Switch;

    @Override
    public String toString() {
        switch (this) {
            case PC:
                return "PC";
            case PS3:
                return "PlayStation 3";
            case PS4:
                return "PlayStation 4";
            case PS5:
                return "PlayStation 5";
            case Xbox:
                return "Xbox";
            case XboxSeriesX:
                return "Xbox Series X";
            case Switch:
                return "Nintendo Switch";
        }
        return "";
    }
}
