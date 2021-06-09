package com.github.db;

import com.github.exceptions.DatabaseException;

import java.util.Collection;

/**
 * Interfície DAO para el cliente.
 * @author Edgar Luque
 */
public interface ClientDAO extends DAO<Client, Integer> {
    /**
     * Buscar clientes por nombre.
     * @param nameQuery El nombre a buscar.
     * @return Una lista de clientes con nombres parecidos.
     * @throws DatabaseException Si hay un error interno.
     */
    Collection<Client> searchByName(String nameQuery) throws DatabaseException;

    /**
     * Buscar clientes por country.
     * @param country El country.
     * @return Una lista de clientes con este country.
     * @throws DatabaseException Si hay un error interno.
     */
    Collection<Client> getByCountry(String country) throws DatabaseException;
}
