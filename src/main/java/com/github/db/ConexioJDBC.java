package com.github.db;

import com.github.Configuration;
import com.github.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexion JDBC
 * @author Edgar Luque
 */
public class ConexioJDBC implements Conexio {
    private String host;
    private int port;
    private String usuario;
    private String password;
    private Connection connection;

    public ConexioJDBC(Configuration configuration) {
        this.host = configuration.getHost();
        this.port = configuration.getPort();
        this.usuario = configuration.getUsuario();
        this.password = configuration.getPassword();
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            if(connection != null && !connection.isClosed())
                return;

            connection = DriverManager.getConnection(String.format(
                    "jdbc:mysql://%s:%d/gameshop",
                    host, port
            ), usuario, password);
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            throw new DatabaseException("error al conectarse", exception);
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("error al cerrar la conexion", exception);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
