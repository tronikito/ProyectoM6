package com.github.exceptions;

/**
 * Excepcion para errores al no encontrarse un objecto en la base de datos.
 * @author Edgar Luque
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
