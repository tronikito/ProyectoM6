package com.github.db;

import com.github.Configuration;
import com.github.exceptions.DatabaseException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Conexion a mongodb.
 * @author Kevin Fernandez
 */
public class ConexioMongo implements Conexio {
    private String host;
    private int port;
    private String usuario;
    private String password;
    private MongoClient connection;

    public ConexioMongo(Configuration configuration) {
        this.host = configuration.getHost();
        this.port = configuration.getPort();
        this.usuario = configuration.getUsuario();
        this.password = configuration.getPassword();
    }

    @Override
    public void connect() throws DatabaseException {

        try {
            if(connection != null)
                return;

            connection = MongoClients.create();

        } catch (MongoException exception) {
            throw new DatabaseException("error al conectarse", exception);
        }
    }

    @Override
    public void close() throws DatabaseException {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (MongoException exception) {
            throw new DatabaseException("error al cerrar la conexion", exception);
        }
    }

    public MongoClient getConnection() {
        return connection;
    }

}
