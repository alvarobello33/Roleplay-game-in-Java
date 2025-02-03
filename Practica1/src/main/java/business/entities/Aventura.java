package business.entities;

import java.util.ArrayList;

/**
 Clase que representa una aventura.
 */
public class Aventura {
    private String name;
    private int nCombats;
    private ArrayList<Combat> combats;

    public Aventura() {

    }

    /**
     Constructor de la clase Aventura que recibe el nombre y el número de combates.
     @param name El nombre de la aventura.
     @param nCombats El número de combates de la aventura.
     */
    public Aventura(String name, int nCombats) {
        this.name = name;
        this.nCombats = nCombats;
        combats = new ArrayList<>();
    }

    /**
     Añade un combate a la aventura.
     @param c El combate a añadir.
     */
    public void addCombat(Combat c) {
        combats.add(c);
    }


    /**
     Obtiene el nombre de la aventura.
     @return El nombre de la aventura.
     */
    public String getName() {
        return name;
    }

    /**
     Obtiene el número de combates de la aventura.
     @return El número de combates de la aventura.
     */
    public int getnCombats() {
        return nCombats;
    }

    /**
     Obtiene la lista de combates de la aventura.
     @return La lista de combates de la aventura.
     */
    public ArrayList<Combat> getCombats() {
        return combats;
    }

    /**
     Establece el nombre de la aventura.
     @param name El nombre de la aventura.
     */
    public void setName(String name) {
        this.name = name;
    }

}
