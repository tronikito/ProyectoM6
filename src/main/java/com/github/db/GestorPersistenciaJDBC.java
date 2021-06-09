package com.github.db;

/**
 * Gestor persistencia JDBC
 * @author Edgar Luque
 */
public class GestorPersistenciaJDBC implements GestorPersistencia {
    private ClientJDBCDAO clientRepo;
    private VideogameJDBCDAO videogameRepo;

    public GestorPersistenciaJDBC(ConexioJDBC conexioJDBC) {
        videogameRepo = new VideogameJDBCDAO(conexioJDBC);
        clientRepo = new ClientJDBCDAO(conexioJDBC, videogameRepo);
    }

    @Override
    public ClientDAO getClientDAO() {
        return clientRepo;
    }

    @Override
    public VideogameDAO getVideogameDAO() {
        return videogameRepo;
    }
}
