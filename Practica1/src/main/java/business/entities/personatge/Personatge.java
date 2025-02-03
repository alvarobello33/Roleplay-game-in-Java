package business.entities.personatge;

import com.google.gson.annotations.SerializedName;

/**
 Clase abstracta que representa a un personaje genérico.
 */
public abstract class Personatge {

    public final static String PHYSICAL = "Physical";
    public final static String MAGICAL = "Magical";
    public final static String PSYCHICAL = "Psychical";


    private String name;
    private String player;
    //protected int nivell;
    @SerializedName("class")
    private String clase;   //"Adventurer"
    protected int xp;
    //private int[] estadistiques = new int[3];
    protected int body;
    protected int mind;
    protected int spirit;


    /**
     Constructor de la clase Personatge por defecto.
     */
    public Personatge() {
    }

    /**
     Constructor de la clase Personatge.
     @param name Nombre del personaje.
     @param nivell Nivel del personaje.
     @param clase Clase del personaje.
     @param xp Experiencia del personaje.
     @param body Valor del atributo "cos" del personaje.
     @param mind Valor del atributo "ment" del personaje.
     @param spirit Valor del atributo "esperit" del personaje.
     */
    public Personatge(String name, String player, int nivell, String clase, int xp, int body, int mind, int spirit) {
        this.name = name;
        this.player = player;
        this.clase = clase;
        this.xp = xp;
        this.body = body;
        this.mind = mind;
        this.spirit = spirit;
    }

    /**
     Obtiene el nombre del personaje.
     @return El nombre del personaje.
     */
    public String getName() {
        return name;
    }

    /**
     Obtiene el nombre del jugador del personaje.
     @return El nombre del jugador del personaje.
     */
    public String getPlayer() {
        return player;
    }

    /**
     Obtiene el nivel del personaje.
     @return El nivel del personaje.
     */
    public int getNivell() {
        return xp/100 +1;
    }

    /**
     Obtiene la clase del personaje.
     @return La clase del personaje.
     */
    public String getClase() {
        return clase;
    }

    /**
     Obtiene la experiencia del personaje.
     @return La experiencia del personaje.
     */
    public int getXp() {
        return xp;
    }

    /**
     Obtiene el valor del atributo "cos" del personaje.
     @return El valor del atributo "cos" del personaje.
     */
    public int getBody() {
        return body;
    }

    /**
     Obtiene el valor del atributo "ment" del personaje.
     @return El valor del atributo "ment" del personaje.
     */
    public int getMind() {
        return mind;
    }

    /**
     Obtiene el valor del atributo "esperit" del personaje.
     @return El valor del atributo "esperit" del personaje.
     */
    public int getSpirit() {
        return spirit;
    }

    /**
     Establece el nombre del personaje.
     @param name El nombre del personaje.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     Establece el nombre del jugador del personaje.
     @param player El nombre del jugador del personaje.
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     Establece la clase del personaje.
     @param clase La clase del personaje.
     */
    public void setClase(String clase) {
        this.clase = clase;
    }

    /**
     Establece la experiencia del personaje.
     @param xp La experiencia del personaje.
     */
    public void setXp(int xp) {
        this.xp = xp;
    }

    /**
     Establece el valor del atributo "cos" del personaje.
     @param body El valor del atributo "cos" del personaje.
     */
    public void setBody(int body) {
        this.body = body;
    }

    /**
     Establece el valor del atributo "ment" del personaje.
     @param mind El valor del atributo "ment" del personaje.
     */
    public void setMind(int mind) {
        this.mind = mind;
    }

    /**
     Establece el valor del atributo "esperit" del personaje.
     @param spirit El valor del atributo "esperit" del personaje.
     */
    public void setSpirit(int spirit) {
        this.spirit = spirit;
    }


    /**
     Devuelve la información del personaje en forma de cadena de texto.

     @return La información del personaje.
     */
    public String getInfo() {
        String info = "* Name:\t" + name + "\n";
        info += "* Player:\t" + player + "\n";
        info += "* Class:\t" + clase + "\n";
        info += "* Level:\t" + getNivell() + "\n";
        info += "* XP:\t" + xp + "\n";
        if (body >= 0) {
            info += "* Body:\t+" + body + "\n";
        } else {
            info += "* Body:\t" + body + "\n";
        }
        if (body >= 0) {
            info += "* Mind:\t+" + mind + "\n";
        } else {
            info += "* Mind:\t" + mind + "\n";
        }
        if (body >= 0) {
            info += "* Spirit:\t+" + spirit + "\n";
        } else {
            info += "* Spirit:\t" + spirit + "\n";
        }

        return info;
    }

    /**
     Calcula la iniciativa del personaje.
     */
    public abstract void calcularIniciativa();

    /**
     Obtiene el valor de la iniciativa del aventurero.
     @return El valor de la iniciativa.
     */
    public abstract int getIniciativa();

    /**
     Obtiene los puntos de vida actuales del aventurero.
     @return Los puntos de vida actuales.
     */
    public abstract int getHp();

    /**
     Obtiene los puntos de vida máximos del aventurero.
     @return Los puntos de vida máximos.
     */
    public abstract int getHpMax();

    /**
     Método abstracto para recibir daño.
     @param damage El daño recibido.
     */
    public abstract void receiveDamage(int damage, String tipusMal);

    /**
     Método abstracto para verificar si el personaje está muerto.
     @return true si el personaje está muerto, false en caso contrario.
     */
    public abstract boolean isDead();

    /**
     Método abstracto para verificar si el personaje tiene menos de la mitad de la vida.
     @return true si el personaje tiene menos de la mitad de la vida, false en caso contrario.
     */
    public abstract boolean needsHelp();

    /**
     Método abstracto para aumentar la vida del personaje.
     */
    public abstract void curar(int curacion);

    /**
     Método abstracto para subir de nivel al personaje.
     */
    protected abstract void subirNivel();

    /**
     Incrementa la experiencia del personaje y verifica si ha subido de nivel.
     @param xp La experiencia ganada.
     @return true si ha subido de nivel, false en caso contrario.
     */
    public boolean ganarXP(int xp) {
        //devuelve true si ha subido de nivel y false en caso contrario

        int previousLevel = getNivell();
        this.xp += xp;
        if (this.xp >= previousLevel*100) {
            //subir de nivel
            subirNivel();
            return true;
        } else {
            return false;
        }
    }



}
