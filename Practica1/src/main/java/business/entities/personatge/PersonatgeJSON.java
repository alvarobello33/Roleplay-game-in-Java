package business.entities.personatge;

/**
 Clase utilizada unicamente para guardar objetos de la clase abstracta Personatge en formato JSON.
 */
public class PersonatgeJSON extends Personatge{

    @Override
    public void calcularIniciativa() {

    }

    @Override
    public int getIniciativa() {
        return 0;
    }

    @Override
    public int getHp() {
        return 0;
    }

    @Override
    public int getHpMax() {
        return 0;
    }

    @Override
    public void receiveDamage(int damage, String tipusMal) {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean needsHelp() {
        return false;
    }

    @Override
    public void curar(int curacion) {

    }

    @Override
    protected void subirNivel() {

    }
}
