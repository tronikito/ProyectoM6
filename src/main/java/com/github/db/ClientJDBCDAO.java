package com.github.db;

import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import com.github.exceptions.NotFoundException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * La implementacion del cliente DAO para sql.
 *
 * @author Edgar Luque
 */
public class ClientJDBCDAO implements ClientDAO {
    private static final Logger LOGGER = Logger.getLogger(ClientJDBCDAO.class);
    private final Connection connection;
    private VideogameJDBCDAO videogameJDBCDAO;

    public ClientJDBCDAO(ConexioJDBC connection, VideogameJDBCDAO videogameJDBCDAO) {
        this.connection = connection.getConnection();
        this.videogameJDBCDAO = videogameJDBCDAO;
    }

    /**
     * Convierte un ResultSet a un Client
     * @param resultSet El resultset
     * @return El cliente
     * @throws SQLException
     */
    private Client fromResultSet(ResultSet resultSet) throws SQLException {
        return new Client(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("country"),
                resultSet.getDate("createdAt"),
                resultSet.getBoolean("isPartner")
        );
    }

    @Override
    public void insert(Client client) throws DuplicatedException, DatabaseException {
        LOGGER.debug("Insertando cliente: " + client.getName());
        try (PreparedStatement stmt = connection.prepareStatement(
                "insert into client (id, name, country, createdAt, isPartner) values (?,?,?,?,?)"
        )) {
            stmt.setInt(1, client.getId());
            stmt.setString(2, client.getName());
            stmt.setString(3, client.getCountry());
            stmt.setDate(4, client.getCreatedAt());
            stmt.setBoolean(5, client.isPartner());
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            LOGGER.error("Error sql: " + throwables.getErrorCode());
            LOGGER.error("Error sql state: " + throwables.getSQLState());
            if (throwables.getErrorCode() == 1062) {
                throw new DuplicatedException(
                        String.format("error al insertar cliente con id %s, ya existe", client.getId()),
                        throwables
                );
            } else {
                throw new DatabaseException("error al insertar cliente con id " + client.getId(), throwables);
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "insert into client_videogames (client_id, videogame_id) values (?,?)"
        )) {
            for (Videogame videogame : client.getVideogames()) {
                stmt.setInt(1, client.getId());
                stmt.setInt(2, client.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            LOGGER.error("Error sql: " + throwables.getErrorCode());
            LOGGER.error("Error sql state: " + throwables.getSQLState());
            if (throwables.getErrorCode() == 1062) {
                throw new DuplicatedException(
                        String.format("error al insertar videogame al" +
                                " cliente con id %s, ya existe", client.getId()),
                        throwables
                );
            } else {
                throw new DatabaseException("error al insertar videojuego al cliente con id " + client.getId(), throwables);
            }
        }
    }

    @Override
    public Client getByID(Integer id) throws NotFoundException, DatabaseException {
        LOGGER.debug("Buscando cliente por id: " + id);

        Client client = null;

        try (PreparedStatement stmt = connection.prepareStatement(
                "select * from client where id = ?"
        )) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                client = fromResultSet(resultSet);
                client.getVideogames().addAll(videogameJDBCDAO.getByClientID(client.getId()));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener cliente con id " + id, throwables);
        }

        if (client == null)
            throw new NotFoundException(String.format("el cliente con id %d no se ha encontrado", id));

        return client;
    }

    @Override
    public Collection<Client> getAll() throws DatabaseException {
        LOGGER.debug("Buscando todos los clientes");

        List<Client> clients = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(
                "select * from client"
        )) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Client client = fromResultSet(resultSet);
                client.getVideogames().addAll(videogameJDBCDAO.getByClientID(client.getId()));
                clients.add(client);
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los clientes", throwables);
        }

        return clients;
    }

    @Override
    public void delete(Integer id) throws DatabaseException {
        LOGGER.debug("Borrando cliente con id " + id);
        try (PreparedStatement stmt = connection.prepareStatement(
                "delete from client where id = ?"
        )) {
            stmt.setInt(1, id);
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al borrar cliente con id " + id, throwables);
        }
    }

    @Override
    public void deleteAll(Iterable<Client> objects) throws DatabaseException {
        LOGGER.debug("Borrando lista de clientes");
        try (PreparedStatement stmt = connection.prepareStatement(
                "delete from client where id = ?"
        )) {
            for (Client client : objects) {
                stmt.setInt(1, client.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al borrar clientes", throwables);
        }
    }

    @Override
    public void update(Client client) throws DatabaseException {
        LOGGER.debug("Actualizando cliente: " + client.getId());
        try (PreparedStatement stmt = connection.prepareStatement(
                "update client set name=?, country=?, createdAt=?, isPartner=? where id = ?"
        )) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getCountry());
            stmt.setDate(3, client.getCreatedAt());
            stmt.setBoolean(4, client.isPartner());
            stmt.setInt(5, client.getId());
            stmt.execute();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al actualizar cliente con id " + client.getId(), throwables);
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "delete from client_videogames where client_id = ?"
        )) {
            stmt.setInt(1, client.getId());
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener videogame_id s del cliente " + client.getId(), throwables);
        }

        // Añadir ids  en client.
        try (PreparedStatement stmt = connection.prepareStatement(
                "insert into client_videogames (client_id, videogame_id) values (?,?)"
        )) {
            for(Videogame videogame: client.getVideogames()) {
                stmt.setInt(1, client.getId());
                stmt.setInt(2, videogame.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al añadir nuevos videojuegos al cliente " + client.getId(), throwables);
        }
    }

    @Override
    public Collection<Client> searchByName(String nameQuery) throws DatabaseException {
        LOGGER.debug("Buscando clientes por nombre: " + nameQuery);

        List<Client> clients = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(
                "select * from client where name like ?"
        )) {
            stmt.setString(1, "%" + nameQuery + "%");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                clients.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los clientes por nombre", throwables);
        }

        return clients;
    }

    @Override
    public Collection<Client> getByCountry(String country) throws DatabaseException {
        LOGGER.debug("Buscando clientes por pais: " + country);

        List<Client> clients = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(
                "select * from client where country like ?"
        )) {
            stmt.setString(1, "%" + country + "%");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                clients.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los clientes por country", throwables);
        }

        return clients;
    }
}
