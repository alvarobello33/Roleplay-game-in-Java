package business.entities.personatge;

import business.Dado;

/**
 Clase que representa a un aventurero.

 Extiende la clase Personaje.
 */
public class Aventurer extends Personatge{

    public final static String AVENTURER = "Adventurer";
    public final static String WARRIOR = "Warrior";
    public final static String CHAMPION = "Champion";


    private int hp;
    private int hpMax;

    private String tipusMal;
    private int iniciativa;

    /**
     Constructor de la clase Aventurer.

     @param nom Nombre del aventurero.
     @param nivell Nivel del aventurero.
     @param clase Clase del aventurero.
     @param xp Experiencia del aventurero.
     @param cos Valor del atributo "cos" del aventurero.
     @param ment Valor del atributo "ment" del aventurero.
     @param esperit Valor del atributo "esperit" del aventurero.
     */
    public Aventurer(String nom, String player, int nivell, String clase, int xp, int cos, int ment, int esperit) {
        super(nom, player, nivell, clase, xp, cos, ment, esperit);
        if (clase.equals(CHAMPION)) {
            hpMax = ((10 + cos) * nivell) + cos * nivell;
        } else {
            hpMax = (10 + cos) * nivell;
        }
        hp = hpMax;

        tipusMal = PHYSICAL; //3 tippos de dano "Physical" "Magical" "Psychical"

    }


    /**
     Incrementa el valor del atributo "esperit" del aventurero en 1.
     */
    public void selfMotivated() {
        spirit++;
    }


    @Override
    public void calcularIniciativa() {iniciativa = Dado.lanzar(12) + spirit;}

    @Override
    public int getIniciativa() {return iniciativa;}

    /**
     Realiza un ataque con espada.
     @return El daño causado por el ataque.
     */
    public int swordSlash() {
        if (getClase().equals(AVENTURER)) {
            return Dado.lanzar(6) + body;
        } else {
            return Dado.lanzar(10) + body;
        }
    }

    /**
     Realiza una curación utilizando vendajes.
     @return La cantidad de curación realizada.
     */
    public int bandageTime() {
        int curacion;
        if (getClase().equals(CHAMPION)) {
            curacion = hpMax - hp;
            hp = hpMax;
        } else {
            curacion = Dado.lanzar(8) + mind;
            hp += curacion;
            if (hp > hpMax) { hp = hpMax; }
        }
        return curacion;
    }

    @Override
    protected void subirNivel() {
        hpMax = (10 + body) * getNivell();
        hp = hpMax;

        //Evolucion en caso que haga falta
        if (getClase().equals(AVENTURER)) {
            if (getNivell() > 7) {
                setClase(CHAMPION);
            }else if (getNivell() > 3) {
                setClase(WARRIOR);
            }
        } else if (getClase().equals(WARRIOR)) {
            if (getNivell() > 7) {
                setClase(CHAMPION);
            }
        }
    }

    @Override
    public void receiveDamage(int damage, String tipusMal) {
        if (tipusMal.equals(this.tipusMal)) {
            hp -= damage/2;
        } else {
            hp -= damage;
        }
        if (hp < 0) {
            hp = 0;
        }
    }

    @Override
    public boolean isDead() { return hp < 1;}

    @Override
    public boolean needsHelp() {
        return hp < hpMax/2;
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


    @Override
    public int getHp() {
        return hp;
    }


    @Override
    public int getHpMax() {
        return hpMax;
    }


    /**
     Obtiene el tipo de daño del aventurero.
     @return El tipo de daño 'tipusMal'.
     */
    public String getTipusMal() {
        return tipusMal;
    }


}
