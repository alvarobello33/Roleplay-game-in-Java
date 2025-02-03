package persistence;

import business.BusinessException;
import business.entities.Aventura;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Implementación de la interfaz AventuresDAO que utiliza una API para acceder a los datos de las aventuras.
 */
public class AventuresAPI implements AventuresDAO {
    public static final String HOST = "https://balandrau.salle.url.edu/dpoo";
    public static final String ID = "S1-Project_43";

    ApiHelper apiHelper;
    Gson gson;

    /**
     * Constructor de la clase AventuresAPI.
     *
     * @throws PersistenceException Si ocurre un error al crear la instancia de AventuresAPI.
     */
    public AventuresAPI() throws PersistenceException {
        apiHelper = new ApiHelper();
        gson = new Gson();
    }

    /**
     * Obtiene una lista de aventuras desde la API.
     *
     * @return Una lista de objetos Aventura que representa las aventuras obtenidas.
     * @throws PersistenceException Si ocurre un error al intentar obtener las aventuras.
     */
    @Override
    public ArrayList<Aventura> getAventures() throws PersistenceException {
        // CLOUD_DATA
        String aventuresString = apiHelper.getFromUrl(HOST + "/" + ID + "/adventures");
        Type type = new TypeToken<ArrayList<Aventura>>() {}.getType();
        return gson.fromJson(aventuresString, type);
    }

    /**
     * Guarda una aventura en la API.
     *
     * @param aventura La aventura que se desea guardar.
     * @throws PersistenceException Si ocurre un error al intentar guardar la aventura.
     */
    @Override
    public void saveAventura(Aventura aventura) throws PersistenceException {
        // CLOUD_DATA
        String aventuraJson = gson.toJson(aventura);
        apiHelper.postToUrl(HOST + "/" + ID + "/adventures", aventuraJson);
    }
}

