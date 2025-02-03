package business.entities.personatge;

import business.Dado;

/**
 * Esta clase representa un mago, que es una subclase de Personatge.
 * Tiene habilidades mágicas y características específicas de los magos.
 */
public class Mage extends Personatge {
    public final static String MAGE = "Mage";

    private int hp;
    private int hpMax;
    private int shield;

    private String tipusMal;
    private int iniciativa;

    /**
     * Constructor de la clase Mage.
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
    public Mage(String nom, String player, int nivell, String clase, int xp, int cos, int ment, int esperit) {
        super(nom, player, nivell, clase, xp, cos, ment, esperit);

        hpMax = (10 + cos) * nivell;
        hp = hpMax;

        tipusMal = MAGICAL; // 3 tipos de daño: "Physical", "Magical", "Psychical"

    }

    /**
     * Obtiene el tipo de daño del mago.
     *
     * @return El tipo de daño del mago.
     */
    public String getTipusMal() {
        return tipusMal;
    }

    @Override
    public void calcularIniciativa() {
        iniciativa = Dado.lanzar(20) + mind;
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
        if (tipusMal.equals(this.tipusMal)) {
            damage = damage - getNivell();
            if (damage < 0) {
                damage = 0;
            }
        }

        if (shield > 0) {
            if (damage > shield) {
                shield = 0;
                damage = damage - shield;
                hp -= damage;
            } else {
                shield -= damage;
            }
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
     * Actualiza el escudo del mago.
     *
     * @return El nuevo valor del escudo.
     */
    public int updateShield() {
        shield = (Dado.lanzar(6) + mind) * getNivell();
        return shield;
    }

    /**
     * Devuelve el daño de un ataque de bolas de fuego.
     *
     * @return El valor del daño realizado.
     */
    public int fireball() {
        return Dado.lanzar(4) + mind;
    }

    /**
     * Devuelve el daño de un ataque de misiles arcánicos.
     *
     * @return El valor del daño realizado.
     */
    public int arcaneMissile() {
        return Dado.lanzar(6) + mind;
    }

    /**
     * Obtiene el valor del escudo del mago.
     *
     * @return El valor del escudo.
     */
    public int getShield() {
        return shield;
    }
}

