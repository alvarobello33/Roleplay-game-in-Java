package persistence;

import business.BusinessException;
import business.entities.Monstre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Implementación de la interfaz MonstresDAO que utiliza una API para acceder a los datos de los monstruos.
 */
public class MonstresAPI implements MonstresDAO {
    public static final String HOST = "https://balandrau.salle.url.edu/dpoo";
    public static final String ID = "S1-Project_43";

    ApiHelper apiHelper;
    Gson gson;

    /**
     * Constructor de la clase MonstresAPI.
     *
     * @throws PersistenceException Si ocurre un error al crear la instancia de MonstresAPI.
     */
    public MonstresAPI() throws PersistenceException {
        apiHelper = new ApiHelper();
        gson = new Gson();
    }

    /**
     * Lee los monstruos desde la API.
     *
     * @return Una lista de objetos Monstre que representa los monstruos obtenidos.
     * @throws PersistenceException Si ocurre un error al intentar leer los monstruos.
     */
    @Override
    public ArrayList<Monstre> readMonsters() throws PersistenceException {
        // CLOUD_DATA
        String monstersString = apiHelper.getFromUrl(HOST + "/shared/monsters");
        Type type = new TypeToken<ArrayList<Monstre>>() {}.getType();
        return gson.fromJson(monstersString, type);
    }
}

