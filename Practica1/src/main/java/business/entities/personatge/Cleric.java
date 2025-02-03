package business.entities.personatge;

import business.Dado;

/**
 * Esta clase representa un clérigo, que es una subclase de Personatge.
 * Tiene habilidades de curación y características específicas de los clérigos.
 */
public class Cleric extends Personatge {

    public final static String CLERIC = "Cleric";
    public final static String PALADIN = "Paladin";

    private int hp;
    private int hpMax;

    private String tipusMal;
    private int iniciativa;

    /**
     * Constructor de la clase Cleric.
     *
     * @param nom     Nombre del aventurero.
     * @param player  Nombre del jugador.
     * @param nivell  Nivel del aventurero.
     * @param clase   Clase del aventurero.
     * @param xp      Experiencia del aventurero.
     * @param cos     Valor del atributo "cos" del aventurero.
     * @param ment    Valor del atributo "ment" del aventurero.
     * @param esperit Valor del atributo "esperit" del aventurero.
     */
    public Cleric(String nom, String player, int nivell, String clase, int xp, int cos, int ment, int esperit) {
        super(nom, player, nivell, clase, xp, cos, ment, esperit);

        hpMax = (10 + cos) * nivell;
        hp = hpMax;

        tipusMal = PSYCHICAL; // 3 tipos de daño: "Physical", "Magical", "Psychical"

    }

    /**
     * Obtiene el tipo de daño del clérigo.
     *
     * @return El tipo de daño del clérigo.
     */
    public String getTipusMal() {
        return tipusMal;
    }

    @Override
    public void calcularIniciativa() {
        iniciativa = Dado.lanzar(10) + spirit;
    }

    @Override
    public int getIniciativa() {
        return iniciativa;
    }

    @Override
    public int getHp() {
        return hp;
    }

    @Override
    public int getHpMax() {
        return hpMax;
    }

    @Override
    public void receiveDamage(int damage, String tipusMal) {
        if (getClase().equals(PALADIN) && tipusMal.equals(this.tipusMal)) {
            hp -= damage / 2;
        } else {
            hp -= damage;
        }

        if (hp < 0) {
            hp = 0;
        }
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    protected void subirNivel() {
        hpMax = (10 + body) * getNivell();
        hp = hpMax;

        // Evolución en caso que sea necesario
        if (getClase().equals(Cleric.CLERIC)) {
            if (getNivell() > 4) {
                setClase(Cleric.PALADIN);
            }
        }
    }

    @Override
    public boolean needsHelp() {
        return hp < hpMax / 2;
    }

    @Override
    public void curar(int curacion) {
        if (!isDead()) {
            hp += curacion;
            if (hp > hpMax) {
                hp = hpMax;
            }
        }
    }

    /**
     * Realiza una acción de buena suerte.
     *
     * @return El resultado de la acción de buena suerte.
     */
    public int goodLuck() {
        if (getClase().equals(CLERIC)) {
            return 1;
        } else {
            return Dado.lanzar(3);
        }
    }

    /**
     * Realiza una acción de curación.
     *
     * @return El valor de la curación realizada.
     */
    public int heal() {
        return Dado.lanzar(10) + mind;
    }

    /**
     * Realiza un ataque.
     *
     * @return El valor del daño realizado en el ataque.
     */
    public int attack() {
        if (getClase().equals(CLERIC)) {
            return Dado.lanzar(4) + spirit;
        } else {
            return Dado.lanzar(8) + spirit;
        }
    }
}

