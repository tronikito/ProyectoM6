package com.github.db;

import com.github.exceptions.DatabaseException;
import com.github.exceptions.DuplicatedException;
import com.github.exceptions.NotFoundException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * La implementacion del videogame DAO para mongo.
 *
 * @author Kevin Fernandez
 */
public class VideogameMongoDAO implements VideogameDAO {

    private static final Logger LOGGER = Logger.getLogger(ClientMongoDAO.class);

    private MongoCollection<Document> collection;

    public VideogameMongoDAO(MongoDatabase database) {
        this.collection = database.getCollection("videogames");
    }


    @Override
    public void insert(Videogame vi) throws DatabaseException, DuplicatedException {
        LOGGER.debug("Insertando cliente: " + vi.getName());

        try {
            this.getByID(vi.getId());
            throw new DuplicatedException("el videogame con id " + vi.getId() + " ya existe");
        } catch (NotFoundException e) {
            Document newClient = gameToDocument(vi);
            collection.insertOne(newClient);
        }
    }

    public Document gameToDocument(Videogame vi) {

        Document newVi = new Document("id", vi.getId())
                .append("name", vi.getName())
                .append("platform", vi.getPlatform().ordinal())
                .append("releaseDate", vi.getReleaseDate())
                .append("price", vi.getPrice());

        return newVi;
    }

    private Videogame documentToGame(Document vi) throws NotFoundException {

        int id = vi.getInteger("id");
        String name = vi.getString("name");
        Platform platform = Platform.values()[vi.getInteger("platform")];
        Date releaseDate = new java.sql.Date(vi.getDate("releaseDate").getTime());
        int price = vi.getInteger("price");

        Videogame newVi = new Videogame(id, name, platform, releaseDate, price);

        return newVi;
    }

    @Override
    public Videogame getByID(Integer id) throws NotFoundException, DatabaseException {

        Document vi = new Document();

        LOGGER.debug("Buscando cliente por id: " + id);

        Videogame newVi = null;

        try {
            MongoCursor<Document> cursor = collection.find(Filters.eq("id", id)).iterator();

            if (cursor.hasNext()) {
                Document d = cursor.next();
                newVi = documentToGame(d);
            } else {
                throw new NotFoundException(String.format("el videogame con id %d no se ha encontrado", id));
            }
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener videogame con id " + id, e);
        }


        return newVi;
    }

    @Override
    public Collection<Videogame> getAll() throws DatabaseException {

        LOGGER.debug("Buscando todos los videojuegos");

        List<Videogame> videogames = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document d = cursor.next();
                videogames.add(documentToGame(d));
            }
        } catch (MongoException | NotFoundException throwables) {
            throw new DatabaseException("error al obtener todos los videojuegos", throwables);
        }
        videogames.sort(Videogame::compareTo);
        return videogames;
    }

    @Override
    public void delete(Integer id) throws NotFoundException, DatabaseException {
        try {
            DeleteResult cursor = collection.deleteOne(Filters.eq("id", id));

            if (cursor.wasAcknowledged()) {
                LOGGER.debug("Videogame id: " + id + " deleted");
            } else {
                throw new NotFoundException(String.format("el videogame con id %d no se ha encontrado", id));
            }
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener videogame con id " + id, e);
        }
    }

    @Override
    public void deleteAll(Iterable<Videogame> objects) throws DatabaseException {
        try {
            collection.drop();
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener videojuego con id ", e);
        }
    }

    @Override
    public void update(Videogame object) throws DatabaseException, NotFoundException, DuplicatedException {
        LOGGER.debug("Buscando videogame:");

        Document videogameBBDD = null;
        MongoCursor<Document> cursor = collection.find(Filters.eq("id", object.getId())).iterator();

        if (cursor.hasNext()) {
            videogameBBDD = cursor.next();
        }

        LOGGER.debug("Update videogame:");
        if (videogameBBDD != null) {
            Videogame oldVideogame = documentToGame(videogameBBDD);
            this.delete(oldVideogame.getId());
            this.insert(object);
        } else {
            this.insert(object);
        }

    }

    @Override
    public Collection<Videogame> getByName(String name) throws DatabaseException {
        LOGGER.debug("Buscando todos los videojuegos de nombre: " + name);

        List<Videogame> videogames = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find(Filters.eq("name", name)).iterator();
            if (cursor.hasNext()) {
                Document d = cursor.next();
                videogames.add(documentToGame(d));
            }
        } catch (MongoException | NotFoundException throwables) {
            throw new DatabaseException("error al obtener todos los videojuegos de nombre: " + name, throwables);
        }

        return videogames;
    }

    @Override
    public Collection<Videogame> getByPlatform(Platform platform) throws DatabaseException {
        LOGGER.debug("Buscando todos los videojuegos de nombre: " + platform.toString());

        List<Videogame> videogames = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find(Filters.eq("platform", platform.ordinal())).iterator();
            if (cursor.hasNext()) {
                Document d = cursor.next();
                videogames.add(documentToGame(d));
            }
        } catch (MongoException | NotFoundException throwables) {
            throw new DatabaseException("error al obtener todos los videojuegos de nombre: " + platform.toString(), throwables);
        }

        return videogames;
    }
}
