package persistence;

import business.entities.Aventura;
import business.entities.personatge.Personatge;
import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Esta clase proporciona métodos para leer y escribir los datos de las aventuras en formato JSON.
 */
public class JSONAventuresDAO implements AventuresDAO{
    private final String ruta = "src/data/aventures.json";
    private ArrayList<Aventura> aventures;
    private Gson gson;

    /**
     * Constructor de la clase JSONAventuresDAO.
     * Inicializa el objeto Gson para el manejo de JSON.
     */
    public JSONAventuresDAO() {
        this.gson = new Gson();
    }


    @Override
    public ArrayList<Aventura> getAventures() throws PersistenceException {
        try {
            // By parsing the file as a JsonObject we can process its internal values without defining an additional class
            JsonObject object = JsonParser.parseReader(new FileReader(ruta)).getAsJsonObject();

            // Obtenemos array de aventuras del objeto Json
            Aventura[] aventuresArray = gson.fromJson(object.get("aventures"), Aventura[].class);
            aventures = new ArrayList<>(aventuresArray.length);

            aventures.addAll(Arrays.asList(aventuresArray));
            return aventures;
        } catch (Exception e) { //file not found Exception
            throw new PersistenceException("Error: Couldn't open the Adventures file", e);
        }
    }

    /**
     * Escribe las aventuras proporcionadas en el archivo JSON.
     *
     * @param avents Lista de aventuras a escribir.
     * @throws PersistenceException Si ocurre algún error durante la escritura en el archivo.
     */
    public void writeAdventures(ArrayList<Aventura> avents) throws PersistenceException {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = gson.toJsonTree(avents).getAsJsonArray();
        jsonObject.add("aventures", jsonArray);

        try {
            FileWriter writer = new FileWriter(ruta);
            gson.toJson(jsonObject, writer);
            writer.close();
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't modify the Adventures file", e);
        }

    }

    @Override
    public void saveAventura(Aventura aventura) throws PersistenceException {
        //guardar aventura en Json
        ArrayList<Aventura> aventuresArrayList= getAventures();
        aventuresArrayList.add(aventura);
        writeAdventures(aventuresArrayList);
    }
}
