package business.entities;

import business.Dado;

/**
 Clase que representa a un monstruo.
 */
public class Monstre {

    /**
     * Constante que representa al monstruo jefe 'Boss'.
     */
    public final static String BOSS = "Boss";

    private String name;
    private String challenge;
    private int experience;
    private int hitPoints;  //Vida del monstruo
    private int initiative;
    private String damageDice;
    private String damageType;

    /**
     Constructor de la clase Monstre.
     @param name El nombre del monstruo.
     @param challenge La dificultad del monstruo.
     @param experience La experiencia que otorga el monstruo al derrotarlo.
     @param hitPoints La vida del monstruo.
     @param initiative La iniciativa del monstruo.
     @param damageDice El tipo de dado utilizado para calcular el daño del monstruo.
     @param damageType El tipo de daño del monstruo.
     */
    public Monstre(String name, String challenge, int experience, int hitPoints, int initiative, String damageDice, String damageType) {
        this.name = name;
        this.challenge = challenge;
        this.experience = experience;
        this.hitPoints = hitPoints;
        this.initiative = initiative;
        this.damageDice = damageDice;
        this.damageType = damageType;
    }



    /**
     Verifica si el monstruo está muerto.
     @return true si el monstruo está muerto, false en caso contrario.
     */
    public boolean isDead() {
        return hitPoints < 1;
    }

    /**
     Realiza un ataque y retorna el daño realizado por el monstruo.
     @return El daño realizado por el monstruo.
     */
    public int atacar() {
        return Dado.lanzar(Integer.parseInt(damageDice.substring(1)));
    }

    /**
     Recibe un determinado daño y actualiza la vida del monstruo.
     @param damage El daño recibido.
     */
    public void receiveDamage(int damage, String damageType) {
        if (damageType.equals(this.damageType)) {
            hitPoints -= damage/2;
            if (hitPoints < 0) {
                hitPoints = 0;
            }
        } else {
            hitPoints -= damage;
            if (hitPoints < 0) {
                hitPoints = 0;
            }
        }
    }



    /**
     Obtiene el nombre del monstruo.
     @return El nombre del monstruo.
     */
    public String getName() {
        return name;
    }

    /**
     Obtiene la dificultad del monstruo.
     @return La dificultad del monstruo.
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     Obtiene la experiencia que otorga el monstruo al derrotarlo.
     @return La experiencia otorgada por el monstruo.
     */
    public int getExperience() {
        return experience;
    }

    /**
     Obtiene la vida del monstruo.
     @return La vida del monstruo.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     Obtiene la iniciativa del monstruo.
     @return La iniciativa del monstruo.
     */
    public int getInitiative() {
        return initiative;
    }

    /**
     Obtiene el tipo de daño que causa el monstruo.
     @return El tipo de daño que causa el monstruo.
     */
    public String getDamageType() {
        return damageType;
    }

    /**
     Establece el nombre del monstruo.
     @param name El nombre del monstruo.
     */
    public void setName(String name) {
        this.name = name;
    }

}
