package persistence;

import business.BusinessException;
import business.entities.personatge.Personatge;
import business.entities.personatge.PersonatgeJSON;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Esta clase proporciona métodos para leer y escribir los datos de los personajes en formato JSON.
 */
public class JSONPersonatgesDAO implements PersonatgesDAO{
    private final String ruta = "src/data/personatges.json";
    private ArrayList<Personatge> personatges;
    private Gson gson;

    /**
     * Constructor de la clase JSONPersonatgesDAO.
     * Inicializa el objeto Gson para el manejo de JSON.
     */
    public JSONPersonatgesDAO() {
        this.gson = new Gson();
    }


    @Override
    public ArrayList<Personatge> readPersonatges() throws PersistenceException {   //persistenceException
        try {
            // By parsing the file as a JsonObject we can process its internal values without defining an additional class
            JsonObject object = JsonParser.parseReader(new FileReader(ruta)).getAsJsonObject();

            // Obtenemos array de personatges del objeto Json
            Personatge[] personatgesArray = gson.fromJson(object.get("personatges"), PersonatgeJSON[].class);
            personatges = new ArrayList<>(personatgesArray.length);

            personatges.addAll(Arrays.asList(personatgesArray));
            return personatges;
        } catch (Exception e) { //file not found Exception
            throw new PersistenceException("Error: Couldn't open the Characters file", e);
        }
    }


    /**
     * Escribe los personajes proporcionados en el archivo JSON.
     *
     * @param pers Lista de personajes a escribir.
     * @throws PersistenceException Si ocurre algún error durante la escritura en el archivo.
     */
    public void writeCharacters(ArrayList<Personatge> pers) throws PersistenceException {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = gson.toJsonTree(pers).getAsJsonArray();
        jsonObject.add("personatges", jsonArray);

        try {
            FileWriter writer = new FileWriter(ruta);
            gson.toJson(jsonObject, writer);
            writer.close();

        }catch (IOException e) {
            throw new PersistenceException("Error: Can not modify the Characters file", e);
        }

    }

    @Override
    public void createPersonatge(Personatge p) throws PersistenceException {
        //guardar personatge en Json
        ArrayList<Personatge> personatges = readPersonatges();
        personatges.add(p);
        writeCharacters(personatges);
    }

    @Override
    public void deletePersonatge(Personatge p) throws PersistenceException {
        //eliminar personatge en Json
        ArrayList<Personatge> personatges = readPersonatges();
        for (int i=0; i < personatges.size(); i++) {
            if ( personatges.get(i).getName().equals(p.getName()) && personatges.get(i).getPlayer().equals(p.getPlayer())) {
                personatges.remove(i);
            }
        }
        writeCharacters(personatges);
    }

    @Override
    public void updateCharacter(Personatge p) throws PersistenceException {
        ArrayList<Personatge> personatges = readPersonatges();

        //guardar cambios del personaje en memoria (personatge)
        for (int i = 0; i < personatges.size(); i++) {
            if ( personatges.get(i).getName().equals(p.getName()) && personatges.get(i).getPlayer().equals(p.getPlayer()) ) {
                Personatge pJSON = p;
                personatges.set(i, pJSON);
            }
        }

        writeCharacters(personatges);

        //A veces se guardan personajes como su subclase, aqui los volvemos a leer y escribir correctamente como personajes.
        readPersonatges();
        writeCharacters(personatges);
    }


}
