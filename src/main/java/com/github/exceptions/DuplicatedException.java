package com.github.exceptions;

/**
 * Excepcion para errores relacionados con datos duplicados en la base de datos.
 * @author Edgar Luque
 */
public class DuplicatedException extends Exception {
    public DuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedException(String message) {
        super(message);
    }
}
