package com.github.db;

import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import com.github.exceptions.NotFoundException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * La implementacion del videogame DAO para sql.
 * @author Edgar Luque
 */
public class VideogameJDBCDAO implements VideogameDAO {
    private static final Logger LOGGER = Logger.getLogger(VideogameJDBCDAO.class);
    private Connection connection;

    public VideogameJDBCDAO(ConexioJDBC connection) {
        this.connection = connection.getConnection();
    }

    private Videogame fromResultSet(ResultSet resultSet) throws SQLException {
        return new Videogame(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                Platform.values()[resultSet.getInt("platform")],
                resultSet.getDate("releaseDate"),
                resultSet.getInt("price")
        );
    }

    @Override
    public void insert(Videogame object) throws DuplicatedException, DatabaseException {
        LOGGER.debug("Insertando videogame: " + object.getName());
        try(PreparedStatement stmt = connection.prepareStatement(
                "insert into videogame (id, name, platform, releaseDate, price) values (?,?,?,?,?)"
        )) {
            stmt.setInt(1, object.getId());
            stmt.setString(2, object.getName());
            stmt.setInt(3, object.getPlatform().ordinal());
            stmt.setDate(4, object.getReleaseDate());
            stmt.setInt(5, object.getPrice());
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            if(throwables.getErrorCode() == 1062) {
                throw new DuplicatedException(
                        String.format("error al obtener insertar videojuego con nombre %s, ya existe", object.getName()),
                        throwables
                );
            } else {
                throw new DatabaseException("error al insertar videojuego con nombre " + object.getName(), throwables);
            }

        }
    }

    @Override
    public Videogame getByID(Integer id) throws NotFoundException, DatabaseException {
        LOGGER.debug("Buscando videogame por id: " + id);

        Videogame videogame = null;

        try(PreparedStatement stmt = connection.prepareStatement(
                "select * from videogame where id = ?"
        )) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if(resultSet.next()) {
                videogame = fromResultSet(resultSet);
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener videogame con id " + id , throwables);
        }

        if(videogame == null)
            throw new NotFoundException(String.format("el videogame con id %d no se ha encontrado", id));

        return videogame;
    }

    @Override
    public Collection<Videogame> getAll() throws DatabaseException {
        LOGGER.debug("Buscando todos los videogames");

        List<Videogame> videogames = new ArrayList<>();

        try(PreparedStatement stmt = connection.prepareStatement(
                "select * from videogame"
        )) {
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                videogames.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los videogames", throwables);
        }

        return videogames;
    }

    @Override
    public void delete(Integer id) throws DatabaseException {
        LOGGER.debug("Borrando videogame con id " + id);
        try(PreparedStatement stmt = connection.prepareStatement(
                "delete from videogame where id = ?"
        )) {
            stmt.setInt(1, id);
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al borrar videogame con id " + id , throwables);
        }
    }

    @Override
    public void deleteAll(Iterable<Videogame> objects) throws DatabaseException {
        LOGGER.debug("Borrando lista de videogames");
        try(PreparedStatement stmt = connection.prepareStatement(
                "delete from videogame where id = ?"
        )) {
            for(Videogame client : objects) {
                stmt.setInt(1, client.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al borrar videogames", throwables);
        }
    }

    public Collection<Videogame> getByClientID(int clientID) throws DatabaseException {
        LOGGER.debug("Buscando videogames por client id: " + clientID);

        List<Videogame> videogames = new ArrayList<>();

        try(PreparedStatement stmt = connection.prepareStatement(
                "select v.* from videogame v " +
                        "inner join client_videogames cv on cv.client_id = ? and cv.videogame_id = v.id"
        )) {
            stmt.setInt(1, clientID);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                videogames.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los videogames por client id", throwables);
        }

        return videogames;
    }

    @Override
    public void update(Videogame object) throws DatabaseException {
        LOGGER.debug("Actualizando videogame: " + object.getId());
        try(PreparedStatement stmt = connection.prepareStatement(
                "update videogame set name=?, platform=?, releaseDate=?, price=? where id = ?"
        )) {
            stmt.setString(1, object.getName());
            stmt.setInt(2, object.getPlatform().ordinal());
            stmt.setDate(3, object.getReleaseDate());
            stmt.setInt(4, object.getPrice());
            stmt.setInt(5, object.getId());
            stmt.execute();
            connection.commit();
        } catch (SQLException throwables) {
            throw new DatabaseException("error al actualizar videogame con id " + object.getId(), throwables);
        }
    }

    @Override
    public Collection<Videogame> getByName(String name) throws DatabaseException {
        LOGGER.debug("Buscando videogame por nombre: " + name);

        List<Videogame> videogames = new ArrayList<>();

        try(PreparedStatement stmt = connection.prepareStatement(
                "select * from videogame where name like ?"
        )) {
            stmt.setString(1, "%" + name + "%");
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                videogames.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los videogames por nombre", throwables);
        }

        return videogames;
    }

    @Override
    public Collection<Videogame> getByPlatform(Platform platform) throws DatabaseException {
        LOGGER.debug("Buscando videogame por plataforma: " + platform.toString());

        List<Videogame> videogames = new ArrayList<>();

        try(PreparedStatement stmt = connection.prepareStatement(
                "select * from videogame where platform = ?"
        )) {
            stmt.setInt(1, platform.ordinal());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                videogames.add(fromResultSet(resultSet));
            }

        } catch (SQLException throwables) {
            throw new DatabaseException("error al obtener todos los videogames por plataforma", throwables);
        }

        return videogames;
    }
}
