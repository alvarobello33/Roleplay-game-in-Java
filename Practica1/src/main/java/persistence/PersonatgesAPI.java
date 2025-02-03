package persistence;

import business.BusinessException;
import business.entities.personatge.Personatge;
import business.entities.personatge.PersonatgeJSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Implementación de la interfaz PersonatgesDAO que utiliza una API para acceder a los datos de los personajes.
 */
public class PersonatgesAPI implements PersonatgesDAO {
    public static final String HOST = "https://balandrau.salle.url.edu/dpoo";
    public static final String ID = "S1-Project_43";

    ApiHelper apiHelper;
    Gson gson;

    /**
     * Constructor de la clase PersonatgesAPI.
     *
     * @throws PersistenceException Si ocurre un error al crear la instancia de PersonatgesAPI.
     */
    public PersonatgesAPI() throws PersistenceException {
        apiHelper = new ApiHelper();
        gson = new Gson();
    }

    /**
     * Lee los personajes desde la API.
     *
     * @return Una lista de objetos Personatge que representa los personajes obtenidos.
     * @throws PersistenceException Si ocurre un error al intentar leer los personajes.
     */
    public ArrayList<Personatge> readPersonatges() throws PersistenceException {
        // CLOUD_DATA
        String personatgesString = apiHelper.getFromUrl(HOST + "/" + ID + "/characters");
        Type type = new TypeToken<ArrayList<PersonatgeJSON>>() {}.getType();
        return gson.fromJson(personatgesString, type);
    }

    /**
     * Crea un nuevo personaje en la API.
     *
     * @param p El personaje que se desea crear.
     * @throws PersistenceException Si ocurre un error al intentar crear el personaje.
     */
    public void createPersonatge(Personatge p) throws PersistenceException {
        // CLOUD_DATA
        String personatgeJson = gson.toJson(p);
        apiHelper.postToUrl(HOST + "/" + ID + "/characters", personatgeJson);
    }

    /**
     * Elimina un personaje dada su referencia.
     *
     * @param removedPersonatge El personaje que se desea eliminar.
     * @throws PersistenceException Si ocurre un error al intentar eliminar el personaje.
     */
    public void deletePersonatge(Personatge removedPersonatge) throws PersistenceException {
        // CLOUD_DATA
        apiHelper.deleteFromUrl(HOST + "/" + ID + "/characters" +
                "?name=" + removedPersonatge.getName());//+ "&player=" +removedPersonatge.getPlayer());
    }

    /**
     * Actualiza un personaje en la API.
     *
     * @param p El personaje actualizado.
     * @throws PersistenceException Si ocurre un error al intentar actualizar el personaje.
     */
    @Override
    public void updateCharacter(Personatge p) throws PersistenceException {
        // Borrar y añadir personaje de nuevo en remoto
        deletePersonatge(p);
        createPersonatge(p);
    }
}

