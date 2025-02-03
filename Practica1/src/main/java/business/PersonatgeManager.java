package business;

import business.entities.personatge.Aventurer;
import business.entities.personatge.Cleric;
import business.entities.personatge.Personatge;
import business.entities.personatge.PersonatgeJSON;
import persistence.*;

import java.util.ArrayList;

/**
 * Clase que gestiona los personajes.
 */
public class PersonatgeManager {

    public static final int LOCAL_DATA = 1;
    public static final int CLOUD_DATA = 2;
    private int dataType;

    private JSONPersonatgesDAO personatgesDAO;
    private PersonatgesAPI personatgesAPI;

    private ArrayList<Personatge> personatgesFiltrats;
    private Personatge personatgeAux;


    /**
     * Constructor de la clase PersonatgeManager.
     * Inicializa el objeto JSONPersonatgesDAO.
     */
    public PersonatgeManager() {
    }


    /**
     * Inicializa aventuresDAO, monstresDAO y personatgesDAO con su clase pertinente
     * dependiendo del tipo de datos utilizado en la aplicación.
     *
     * @param dataType El tipo de datos a establecer. Puede ser LOCAL_DATA o REMOTE_DATA.
     * @throws BusinessException Si ocurre un error al establecer el tipo de datos.
     */
    public void setDataType(int dataType) throws BusinessException {
        this.dataType = dataType;
        if (dataType == LOCAL_DATA) {
            personatgesDAO = new JSONPersonatgesDAO();
        } else {
            try {
                personatgesAPI = new PersonatgesAPI();
            } catch (PersistenceException e) {
                throw new BusinessException("Error conecting to the API.", e);
            }
        }
    }

    /**
     * Calcula el valor estadístico en base a una suma dada.
     *
     * @param suma La suma de dos valores.
     * @return El valor estadístico calculado.
     */
    private int calculateStat(int suma) {
        if (suma == 2){
            return 1;
        }
        else if (suma >= 3 && suma <= 5){
            return 0;
        }
        else if (suma >= 6 && suma <= 9){
            return 1;
        }
        else if (suma >= 10 && suma <= 11){
            return 2;
        }
        else {
            return 3;
        }
    }

    /**
     * Genera las estadísticas de un personaje.
     *
     * @param p El personaje al que se generarán las estadísticas.
     * @return Un mensaje con las estadísticas generadas.
     */
    private String generateStats(Personatge p) {
        Dado dado = new Dado();
        String msg = "";

        //cos
        int tirada1 = dado.lanzar(6);
        int tirada2 = dado.lanzar(6);
        int suma = tirada1 + tirada2;

        p.setBody(calculateStat(suma));
        msg += "Body:   You rolled "+suma+" ("+tirada1+" and "+tirada2+").\n";

        //ment
        tirada1 = dado.lanzar(6);
        tirada2 = dado.lanzar(6);
        suma = tirada1 + tirada2;

        p.setMind(calculateStat(suma));
        msg += "Mind:   You rolled "+suma+" ("+tirada1+" and "+tirada2+").\n";

        //esperit
        tirada1 = dado.lanzar(6);
        tirada2 = dado.lanzar(6);
        suma = tirada1 + tirada2;

        p.setSpirit(calculateStat(suma));
        msg += "Spirit:   You rolled "+suma+" ("+tirada1+" and "+tirada2+").\n";


        return msg;
    }

    /**
     * Verifica si el nombre del jugador es correcto.
     *
     * @param playerName El nombre del jugador.
     * @return true si el nombre es correcto, false en caso contrario.
     */
    public boolean isCharacterNameCorrect(char[] playerName) {
        boolean esCorrecto = true;

        // Verifica si el nombre contiene caracteres especiales además de "!"
        for (char c : playerName) {
            if (!Character.isLetter(c)) {
                esCorrecto = false;
                break;
            }
        }

        if (esCorrecto) {
            // Verifica y corrige las mayúsculas del nombre
            playerName[0] = Character.toUpperCase(playerName[0]);
            for (int i = 1; i < playerName.length; i++) {
                playerName[i] = Character.toLowerCase(playerName[i]);
            }
        }

        return esCorrecto;
    }

    /**
     * Crea un nuevo personaje con los atributos especificados.
     *
     * @param nom     El nombre del personaje.
     * @param player  El nombre del jugador.
     * @param nivell  El nivel del personaje.
     * @return Un mensaje con las estadísticas del personaje creado.
     * @throws BusinessException Si ocurre un error al crear el personaje.
     */
    public String createPersonatge(String nom, String player, int nivell) throws BusinessException {
        String statsStr = "";
        //creem personatge i li donem els atributs obtinguts pel controller
        Personatge p = new PersonatgeJSON();
        p.setName(nom);
        p.setPlayer(player);
        //p.setNivell(nivell);
        p.setXp(nivell*100 -100);

        //generem les seves estadistiques
        statsStr += generateStats(p);
        statsStr += "\nYour stats are:";
        statsStr += "\n -Body: "+ p.getBody();
        statsStr += "\n -Mind: "+p.getMind();
        statsStr += "\n -Spirit: "+p.getSpirit();

        personatgeAux = p;

        return statsStr;
    }

    /**
     * Guarda un personaje en el almacenamiento con una clase especificada.
     *
     * @param clase La clase del personaje a guardar.
     * @return Una cadena de texto que representa el resultado de la operación.
     * @throws BusinessException Si ocurre un error al intentar guardar el personaje.
     */
    public String savePersonatge(String clase) throws BusinessException {
        String output = "";

        personatgeAux.setClase(clase);

        //Evolucion en caso que haga falta
        if (personatgeAux.getClase().equals(Aventurer.AVENTURER)) {
            if (personatgeAux.getNivell() > 7 ) {
                personatgeAux.setClase(Aventurer.CHAMPION);
            }else if (personatgeAux.getNivell() > 3) {
                personatgeAux.setClase(Aventurer.WARRIOR);
            }
        } else if (personatgeAux.getClase().equals(Aventurer.WARRIOR)) {
            if (personatgeAux.getNivell() > 7) {
                personatgeAux.setClase(Aventurer.CHAMPION);
            }
        }

        if (personatgeAux.getClase().equals(Cleric.CLERIC)) {
            if (personatgeAux.getNivell() > 4) {
                personatgeAux.setClase(Cleric.PALADIN);
            }
        }

        output += "\nTavern keeper: “Any decent party needs one of those.”\n";
        output += "“I guess that means you’re a " +personatgeAux.getClase()+ " by now, nice!”\n";
        //Guardamos el personaje en Storage
        createPersonatge(personatgeAux);

        output += "\nThe new character " +personatgeAux.getName()+ " has been created.\n";
        return output;
    }

    /**
     * Busca personajes por nombre de jugador.
     *
     * @param name El nombre del jugador.
     * @return Una cadena con los personajes filtrados.
     * @throws BusinessException Si ocurre un error al buscar los personajes.
     */
    public String searchPersonatges(String name) throws BusinessException {

        ArrayList<Personatge> allPersonatges;
        allPersonatges = readPersonatges();

        personatgesFiltrats = new ArrayList<>();

        //obtindre personatges
        String personatgesFiltratsTxt = "";

        int i = 0;
        if (name.length() == 0) {
            for (Personatge a: allPersonatges) {
                personatgesFiltratsTxt += "\t" +(i+1)+ ". " + a.getName() + "\n";
                personatgesFiltrats.add(a);
                i++;
            }

        } else {
            for (Personatge a: allPersonatges) {
                if (a.getPlayer().contains(name)) {
                    personatgesFiltratsTxt += "\t" +(i+1)+ ". " + a.getName() + "\n";
                    personatgesFiltrats.add(a);
                    i++;
                }
            }
        }

        return personatgesFiltratsTxt;
    }


    /**
     * Obtiene la longitud de la lista de personajes filtrados.
     *
     * @return La longitud de la lista de personajes filtrados.
     */
    public int getPersonatgesFiltratsLength() {return personatgesFiltrats.size();}

    /**
     * Obtiene el nombre de un personaje dado su índice.
     *
     * @param index El índice del personaje.
     * @return El nombre del personaje.
     */
    public String getNameByIndex(int index) {return personatgesFiltrats.get(index).getName();}

    /**
     * Obtiene la información de un personaje dado su índice.
     *
     * @param index El índice del personaje.
     * @return La información del personaje.
     */
    public String getInfoByIndex(int index) {return personatgesFiltrats.get(index).getInfo();}


    /**
     * Obtiene la longitud de la lista de personajes.
     *
     * @return La longitud de la lista de personajes.
     * @throws BusinessException Si ocurre un error al leer el archivo de personajes.
     */
    public int getPersonatgesLength() throws BusinessException {
        return readPersonatges().size();
    }

    /**
     * Lee y obtiene una lista de personajes desde el almacenamiento.
     *
     * @return Una lista de objetos Personatge que representa los personajes obtenidos.
     * @throws BusinessException Si ocurre un error al intentar obtener los personajes.
     */
    public ArrayList<Personatge> readPersonatges() throws BusinessException {
        try {
            if (dataType == LOCAL_DATA) {
                return personatgesDAO.readPersonatges();
            } else {
                //CLOUD_DATA
                return personatgesAPI.readPersonatges();

            }
        } catch (PersistenceException e) {
            throw new BusinessException("Error trying to get Characters from storage.", e);
        }
    }

    /**
     * Crea un nuevo personaje y lo guarda en el almacenamiento.
     *
     * @param p El objeto Personatge que se desea crear y guardar.
     * @throws BusinessException Si ocurre un error al intentar crear el personaje.
     */
    public void createPersonatge(Personatge p) throws BusinessException {
        try {
            if (dataType == LOCAL_DATA) {
                personatgesDAO.createPersonatge(p);
            } else {
                //CLOUD_DATA
                personatgesAPI.createPersonatge(p);

            }
        } catch (PersistenceException e) {
            throw new BusinessException("Error trying to create Characters in storage.", e);
        }
    }


    /**
     * Elimina un personaje en el almacenamiento dado su índice y confirmación del nombre.
     *
     * @param index        El índice del personaje.
     * @param confirmation La confirmación del nombre del personaje.
     * @return true si el personaje fue eliminado, false en caso contrario.
     * @throws BusinessException Si ocurre un error al eliminar el personaje.
     */
    public boolean deletePersonatgeWithIndex(int index, String confirmation) throws BusinessException {
        if (confirmation.equals(personatgesFiltrats.get(index).getName())) {

            try {
                Personatge removedPersonatge = personatgesFiltrats.get(index);

                if (dataType == LOCAL_DATA) {
                    personatgesAPI.deletePersonatge(removedPersonatge);
                } else {
                    //CLOUD_DATA
                    personatgesAPI.deletePersonatge(removedPersonatge);
                }

            } catch (PersistenceException e) {
                throw new BusinessException("Error deleting Characters from Storage", e);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Elimina un personaje dada su referencia.
     *
     * @param removedPersonatge        El personaje que se desea eliminar.
     * @return true si el personaje fue eliminado, false en caso contrario.
     * @throws BusinessException Si ocurre un error al eliminar el personaje.
     */
    public boolean deletePersonatgeWithObject(Personatge removedPersonatge) throws BusinessException {

        try {

            if (dataType == LOCAL_DATA) {
                personatgesAPI.deletePersonatge(removedPersonatge);
            } else {
                //CLOUD_DATA
                personatgesAPI.deletePersonatge(removedPersonatge);
            }

        } catch (PersistenceException e) {
            throw new BusinessException("Error: Can not modify the Characters file", e);
        }

        return true;

    }


}
