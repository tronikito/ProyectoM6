package com.github.db;

import com.mongodb.client.MongoDatabase;

/**
 * TODO: Implementar
 */
public class GestorPersistenciaMongo implements GestorPersistencia {
    private ClientMongoDAO clientMongoDAO;
    private VideogameMongoDAO videogameMongoDAO;

    public GestorPersistenciaMongo(ConexioMongo conexioMongo) {
        MongoDatabase database = conexioMongo.getConnection().getDatabase("gameshop");
        videogameMongoDAO = new VideogameMongoDAO(database);
        clientMongoDAO = new ClientMongoDAO(database, videogameMongoDAO);
    }

    @Override
    public ClientDAO getClientDAO() {
        return clientMongoDAO;
    }

    @Override
    public VideogameDAO getVideogameDAO() {
        return videogameMongoDAO;
    }
}
