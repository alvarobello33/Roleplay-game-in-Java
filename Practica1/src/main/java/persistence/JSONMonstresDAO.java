package persistence;

import business.entities.Aventura;
import business.entities.Monstre;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Esta clase proporciona métodos para leer los datos de los monstruos en formato JSON.
 */
public class JSONMonstresDAO implements MonstresDAO {
    private final String ruta = "src/data/monsters.json";
    private ArrayList<Monstre> monstres;
    private Gson gson;

    /**
     * Constructor de la clase JSONMonstresDAO.
     * Inicializa el objeto Gson para el manejo de JSON.
     */
    public JSONMonstresDAO() {
        this.gson = new Gson();
    }


    /**
     * Lee todos los monstruos almacenados en el archivo JSON.
     *
     * @return Lista de monstruos leídos.
     * @throws PersistenceException Si ocurre algún error durante la lectura del archivo.
     */
    @Override
    public ArrayList<Monstre> readMonsters() throws PersistenceException {   //persistenceException
        try {
            // By parsing the file as a JsonObject we can process its internal values without defining an additional class
            JsonObject object = JsonParser.parseReader(new FileReader(ruta)).getAsJsonObject();

            // Obtenemos array de aventuras del objeto Json
            Monstre[] monstresArray = gson.fromJson(object.get("monsters"), Monstre[].class);
            monstres = new ArrayList<>(monstresArray.length);

            monstres.addAll(Arrays.asList(monstresArray));
            return monstres;
        } catch (Exception e) { //file not found Exception
            throw new PersistenceException("Error: Couldn't open the Monsters file", e);
        }
    }


}
