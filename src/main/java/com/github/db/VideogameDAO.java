package com.github.db;

import com.github.exceptions.DatabaseException;

import java.util.Collection;

/**
 * Interfície para el videogame.
 * @author Edgar Luque
 */
public interface VideogameDAO extends DAO<Videogame, Integer> {
    /**
     * Busca videojuegos por su nombre.
     * @param name El nombre.
     * @return La lista de videojuegos encontrados.
     * @throws DatabaseException Si hay un error interno.
     */
    Collection<Videogame> getByName(String name) throws DatabaseException;

    /**
     * Busca videojuegos por su plataforma.
     * @param platform La plataforma.
     * @return La lista de videojuegos encontrados.
     * @throws DatabaseException Si hay un error interno.
     */
    Collection<Videogame> getByPlatform(Platform platform) throws DatabaseException;
}
