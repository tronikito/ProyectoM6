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
 * La implementacion del cliente DAO para mongo.
 *
 * @author Kevin Fernandez
 */
public class ClientMongoDAO implements ClientDAO {

    private static final Logger LOGGER = Logger.getLogger(ClientMongoDAO.class);

    private MongoCollection<Document> collection;
    private VideogameMongoDAO videogameMongoDAO;

    public ClientMongoDAO(MongoDatabase database, VideogameMongoDAO videogameMongoDAO) {
        this.collection = database.getCollection("clients");
        this.videogameMongoDAO = videogameMongoDAO;
    }

    @Override
    public void insert(Client cli) throws DatabaseException, DuplicatedException {
        LOGGER.debug("Insertando cliente: " + cli.getName());

        /*
        try {
            Client checkID = this.getByID(cli.getId());
            if (checkID == null) {
                Document newClient = clientToDocument(cli);
                collection.insertOne(newClient);
            } else {
               throw new DuplicatedException("error al insertar cliente con id " + cli.getId() + " ya existe");
                // TIENE QUE SER NOTFOUND PORQUE NO HAY CODIGO DE ERROR
                //NO PUEDE SER NI DATABASEEXCEPTION NI ERROREXCEPTION
            }
        } catch (MongoException throwables) {
            throw new DatabaseException("error al insertar cliente con id " + cli.getId(), throwables);
        }
        */
        try {
            this.getByID(cli.getId());
            throw new DuplicatedException("el cliente con id " + cli.getId() + " ya existe");
        } catch (NotFoundException e) {
            Document newClient = clientToDocument(cli);
            collection.insertOne(newClient);
        }

    }

    /**
     * @param cli Client Object to document
     * @return Client Object
     */
    private Document clientToDocument(Client cli) {

        ArrayList<Document> videogamesList = new ArrayList<Document>();

        Document newClient = new Document("id", cli.getId())
                .append("name", cli.getName())
                .append("country", cli.getCountry())
                .append("createAt", cli.getCreatedAt())
                .append("isPartner", cli.isPartner());

        if (cli.getVideogames() != null) {
            for (Videogame vi : cli.getVideogames()) {
                Document newVi = new Document();
                newVi.append("id", vi.getId());
                videogamesList.add(newVi);
                LOGGER.debug("añadiendo videojuego " + vi.getId());
            }

            newClient.append("videogames", videogamesList);
        }

        return newClient;
    }

    /**
     * @param cli document to Object Client
     * @return Client Object
     */
    private Client documentToClient(Document cli) throws NotFoundException, DatabaseException {

        int id = cli.getInteger("id");
        String name = cli.getString("name");
        String country = cli.getString("country");
        Date createAt = new java.sql.Date(cli.getDate("createAt").getTime());
        Boolean isPartner = cli.getBoolean("isPartner");

        Client newClient = new Client(id, name, country, createAt, isPartner);

        if (cli.containsKey("videogames")) {

            List<Document> videogamesList = cli.getList("videogames", Document.class);

            for (Document vi : videogamesList) {

                id = vi.getInteger("id");

                Videogame viDatabase = videogameMongoDAO.getByID(id);

                /*
                Videogame newVi = new Videogame(id,
                        viDatabase.getName(),
                        viDatabase.getPlatform(),
                        viDatabase.getReleaseDate(),
                        viDatabase.getPrice());*/

                LOGGER.debug("cargando videojuego " + viDatabase.getId());
                newClient.getVideogames().add(viDatabase);
            }
        }

        return newClient;
    }

    @Override
    public Client getByID(Integer id) throws NotFoundException, DatabaseException {
        LOGGER.debug("Buscando cliente por id: " + id);

        Client client = null;

        try {
            MongoCursor<Document> cursor = collection.find(Filters.eq("id", id)).iterator();

            if (cursor.hasNext()) {
                Document d = cursor.next();
                client = documentToClient(d);
            } else {
                throw new NotFoundException(String.format("el cliente con id %d no se ha encontrado", id));
            }
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener cliente con id " + id, e);
        }
        return client;
    }

    @Override
    public Collection<Client> getAll() throws DatabaseException {
        LOGGER.debug("Buscando todos los clientes");

        List<Client> clients = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document d = cursor.next();
                clients.add(documentToClient(d));
            }
        } catch (MongoException | NotFoundException throwables) {
            throw new DatabaseException("error al obtener todos los clientes", throwables);
        }
        clients.sort(Client::compareTo);
        return clients;
    }

    @Override
    public void delete(Integer id) throws NotFoundException, DatabaseException {
        try {
            DeleteResult cursor = collection.deleteOne(Filters.eq("id", id));

            if (cursor.wasAcknowledged()) {
                LOGGER.debug("Client id: " + id + " deleted");
            } else {
                throw new NotFoundException(String.format("el client con id %d no se ha encontrado", id));
            }
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener client con id " + id, e);
        }
    }

    @Override
    public void deleteAll(Iterable<Client> objects) throws NotFoundException, DatabaseException {
        try {
            collection.drop();
        } catch (MongoException e) {
            throw new DatabaseException("error al obtener client con id ", e);
        }
    }


    @Override
    public void update(Client object) throws DatabaseException, NotFoundException {
        LOGGER.debug("Buscando cliente:");

        try {
            Client oldCliend = this.getByID(object.getId());
            this.delete(oldCliend.getId());
        } finally {
            try {
                this.insert(object);
            } catch (DuplicatedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.debug("Update cliente:");
    }

    @Override
    public Collection<Client> searchByName(String nameQuery) throws DatabaseException {
        LOGGER.debug("Buscando todos los clientes de nombre: " + nameQuery);

        List<Client> clients = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find(Filters.regex("name", nameQuery)).iterator();
            while (cursor.hasNext()) {
                Document d = cursor.next();
                clients.add(documentToClient(d));
            }
        } catch (MongoException | NotFoundException | DatabaseException throwables) {
            throw new DatabaseException("error al obtener todos los clientes de nombre: " + nameQuery, throwables);
        }

        return clients;
    }


    @Override
    public Collection<Client> getByCountry(String country) throws DatabaseException {
        LOGGER.debug("Buscando todos los clientes de país: " + country);

        List<Client> clients = new ArrayList<>();

        try {
            MongoCursor<Document> cursor = collection.find(Filters.eq("country", country)).iterator();
            while (cursor.hasNext()) {
                Document d = cursor.next();
                clients.add(documentToClient(d));
            }
        } catch (MongoException | NotFoundException | DatabaseException throwables) {
            throw new DatabaseException("error al obtener todos los clientes de país: " + country, throwables);
        }

        return clients;
    }
}
