package business.entities;

import java.util.ArrayList;

/**
 Clase que representa un combate.
 */
public class Combat {
    private int nCombat;
    private ArrayList<Monstre> monstres;

    /**
     Constructor de la clase Combat.
     @param nCombat El número del combate.
     */
    public Combat(int nCombat) {
        this.nCombat = nCombat;
        monstres = new ArrayList<>();
    }

    /**
     Obtiene la lista de monstruos del combate.
     @return La lista de monstruos del combate.
     */
    public ArrayList<Monstre> getMonstres() {
        return monstres;
    }

    /**
     Obtiene el número del combate.
     @return El número del combate.
     */
    public int getnCombat() {
        return nCombat;
    }

    /**
     Añade un monstruo al combate.
     @param m El monstruo a añadir.
     */
    public void addMonstre(Monstre m) {
        monstres.add(m);
    }



}
