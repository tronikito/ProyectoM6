package com.github.db;

/**
 * Gestor de persistencia generico.
 * Tiene metodos para obtener los DAO genericos.
 */
public interface GestorPersistencia {
    ClientDAO getClientDAO();
    VideogameDAO getVideogameDAO();
}
