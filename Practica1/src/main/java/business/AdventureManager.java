package business;

import business.entities.Aventura;
import business.entities.Combat;
import business.entities.Monstre;
import business.entities.personatge.Aventurer;
import business.entities.personatge.Cleric;
import business.entities.personatge.Mage;
import business.entities.personatge.Personatge;
import persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Clase que gestiona las aventuras y los combates.
 */
public class AdventureManager {

    public static final int LOCAL_DATA = 1;
    public static final int CLOUD_DATA = 2;


    private int dataType;
    private AventuresDAO aventuresDAO;
    private MonstresDAO monstresDAO;
    private PersonatgesDAO personatgesDAO;

    //variables auxiliares
    private Aventura aventura;
    private Combat combat;
    private ArrayList<Monstre> monstresAgrupats;
    private Monstre monstre;

    //En este array los objetos ya han sido parseados a sus subclases (Aventurer, ...)
    private ArrayList<Personatge> party;

    //Los personajes y monstruos en batalla (tambien parseados)
    private ArrayList<Object> combatients;


    /**
     * Constructor de la clase AdventureManager.
     *
     */
    public AdventureManager() {

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
            this.aventuresDAO = new JSONAventuresDAO();
            this.monstresDAO = new JSONMonstresDAO();
            this.personatgesDAO = new JSONPersonatgesDAO();
        } else {
            try {
                this.aventuresDAO = new AventuresAPI();
                this.monstresDAO = new MonstresAPI();
                this.personatgesDAO = new PersonatgesAPI();
            } catch (PersistenceException e) {
                throw new BusinessException("Error connecting with the API.", e);
            }
        }
    }

    /**
     * Crea una nueva aventura con el nombre y la cantidad de encuentros especificados.
     *
     * @param name      nombre de la aventura
     * @param encounters cantidad de encuentros en la aventura
     */
    public void createAdventure(String name, int encounters) {
        aventura = new Aventura(name, encounters);
    }

    /**
     * Crea un nuevo combate con el número de combate especificado.
     * Si ya existe un combate o el número de combate no coincide, se crea un nuevo combate y se agrega a la aventura.
     *
     * @param nCombat número del combate
     */
    public void createCombat(int nCombat) {
        if (combat == null || nCombat != combat.getnCombat()) {
            combat = new Combat(nCombat);
            aventura.addCombat(combat);
        }
    }

    /**
     * Obtiene los monstruos disponibles y los muestra como una cadena de texto.
     * Si ocurre un error al leer el archivo de monstruos, se lanza una BusinessException.
     *
     * @return cadena de texto con los monstruos disponibles
     * @throws BusinessException si ocurre un error al leer el archivo de monstruos
     */
    public String getMonstres() throws BusinessException {
        //muestra todos los tipos de monstruos (en memoria)
        String monstres = "";
        ArrayList<Monstre> monstresArrayList = readMonsters();

        int i = 0;
        for(Monstre m:monstresArrayList) {
            monstres += (i+1) + ". " + m.getName() + " (" + m.getChallenge() + ")\n";
            i++;
        }

        return monstres;
    }

    /**
     * Obtiene la lista de monstruos del combate actual.
     *
     * @param creantAventura Indica si se está creando una aventura.
     * @return String que representa los monstruos del combate actual.
     */
    public String getMonstresCombat(boolean creantAventura) {
        //muestra los monstruos de un combate (el combate guardado en memoria)
        String monstres = "";
        ArrayList<Monstre> monstresArrayList = combat.getMonstres();
        monstresAgrupats = new ArrayList<>();

        if (monstresArrayList.size() == 0) {
            return "\t# Empty\n";
        }

        int i = 0;
        int index = 0;
        for(Monstre m:monstresArrayList) {
            int cuenta = 0;
            boolean firstMatch = true;

            int j = 0;
            //contamos los monstruos iguales
            for (Monstre m2:monstresArrayList) {
                if(m.getName().equals(m2.getName())) {
                    //en caso que ya hayamos pasado por un monstruo con el mismo nombre, cancelamos
                    if (j<i) {
                        firstMatch = false;
                        break;
                    }
                    cuenta++;

                }
                j++;
            }

            if (firstMatch) {
                if (creantAventura) {
                    monstres += "\t" + (index+1) + ". " + m.getName() + " (x" + cuenta + ")\n";
                } else {
                    monstres += "\t- " +cuenta+ "x " + m.getName()+ "\n";
                }
                monstresAgrupats.add(m);
                index++;
            }
            i++;
        }

        return monstres;
    }

    /**
     * Devuelve el nombre del monstruo en la posición dada.
     *
     * @param index El índice del monstruo.
     * @return El nombre del monstruo.
     * @throws BusinessException Si ocurre un error al obtener el nombre del monstruo.
     */
    public String getMonsterName(int index) throws BusinessException {
        ArrayList<Monstre> monstresArrayList;
        monstresArrayList = readMonsters();

        monstre = monstresArrayList.get(index);
        return monstre.getName();
    }

    /**
     * Lee la lista de monstruos desde la persistencia.
     *
     * @return Una lista de objetos Monstre que representa los monstruos leídos.
     * @throws BusinessException Si ocurre un error al intentar obtener los monstruos desde el almacenamiento.
     */
    public ArrayList<Monstre> readMonsters() throws BusinessException {
        try {
            return monstresDAO.readMonsters();

        } catch (PersistenceException e) {
            throw new BusinessException("Error trying to get monsters from storage.", e);
        }
    }


    /**
     * Devuelve falso si no se pudo añadir el monstruo debido a que se intentó añadir más de un jefe.
     *
     * @param quantitat La cantidad de monstruos a añadir.
     * @return True si se añadieron correctamente los monstruos, falso en caso contrario.
     */
    public boolean addMonstre(int quantitat) {
        //Si es Boss comprobamos que unicamente haya un Boss en el combate
        if (monstre.getChallenge().equals(Monstre.BOSS)) {
            if (quantitat > 1) {
                return false;
            } else {
                //Comprobar que no haya otro Boss anadido
                for (Monstre m:combat.getMonstres()) {
                    if (m.getChallenge().equals(Monstre.BOSS)) {
                        return false;
                    }
                }
            }
        }
        for (int i = 0; i < quantitat; i++) {
            combat.addMonstre(monstre);
        }
        return true;
    }

    /**
     * Elimina los monstruos del tipo especificado mediante la posicion de monstresAgrupats.
     *
     * @param index El índice del monstruo a eliminar.
     * @return Un mensaje indicando el índice y el nombre del monstruo eliminado.
     */
    public String deleteMonstre(int index) {
        String name = monstresAgrupats.get(index).getName();
        monstresAgrupats.remove(index);

        //Se borran de final a principio ya que de hacerlo al reves habrian problemas con el indice
        for (int i = combat.getMonstres().size() -1; i >= 0; i--) {
            if (name.equals(combat.getMonstres().get(i).getName())) {
                combat.getMonstres().remove(i);
            }
        }

        return (index +1) + ". " +name+ " were removed from the encounter.";
    }

    /**
     * Verifica si el combate está vacío, es decir, si no hay monstruos.
     *
     * @return True si el combate está vacío, falso en caso contrario.
     */
    public boolean emptyCombat() {
        return combat.getMonstres().isEmpty();
    }

    /**
     * Guarda la aventura en Storage.
     *
     * @throws BusinessException Si ocurre un error al guardar la aventura.
     */
    public void saveAventura() throws BusinessException {
        try {
            aventuresDAO.saveAventura(aventura);

        } catch (PersistenceException e) {
            throw new BusinessException("Error trying to create Adventure in storage.", e);
        }

    }

    /**
     * Muestra las aventuras disponibles.
     *
     * @return Una cadena de texto que muestra las aventuras disponibles.
     * @throws BusinessException Si ocurre un error al obtener las aventuras.
     */
    public String showAdventures() throws BusinessException {
        String aventuresTxt = "";
        ArrayList<Aventura> aventures = getAventures();

        int i = 0;
        for (Aventura a:aventures) {
            aventuresTxt += "\t" +(i+1)+ ". " +a.getName()+ "\n";
            i++;
        }

        return aventuresTxt;

    }

    /**
     * Devuelve la longitud de la lista de aventuras.
     *
     * @return La longitud de la lista de aventuras.
     * @throws BusinessException Si ocurre un error al abrir el archivo de aventuras.
     */
    public int getAventuresLength() throws BusinessException {
        return getAventures().size();
    }


    /**
     * Guarda la aventura en memoria y devuelve el nombre de esta.
     *
     * @param index El índice de la aventura.
     * @return El nombre de la aventura.
     * @throws BusinessException Si ocurre un error al abrir el archivo de aventuras.
     */
    public String setAdventure(int index) throws BusinessException {
        aventura = getAventures().get(index);
        return aventura.getName();
    }

    /**
     * Crea un grupo de aventureros con la longitud especificada.
     *
     * @param partyLength La longitud del grupo.
     */
    public void createParty(int partyLength) {
        party = new ArrayList<>(partyLength);
    }

    /**
     * Muestra el grupo de aventureros.
     *
     * @param partyLength La longitud del grupo.
     * @return Una cadena que representa el grupo de aventureros.
     */
    public String showParty(int partyLength) {
        String output = "";

        for (int i=0; i < partyLength; i++) {
            if (i < party.size()) {
                output += "\t" +(i+1)+ ". " +party.get(i).getName()+ "\n";
            } else {
                output += "\t" +(i+1)+ ". Empty\n";
            }
        }

        return output;
    }

    /**
     * Agrega un personaje al grupo de aventureros.
     *
     * @param index El índice del personaje.
     * @throws BusinessException Si ocurre un error al abrir el archivo de personajes.
     */
    public void addCharacterToParty(int index) throws BusinessException {
        Personatge p;
        try {
            p = personatgesDAO.readPersonatges().get(index);
        } catch (PersistenceException e) {
            throw new BusinessException("Error reading the Characters from Storage.", e);
        }

        //Aventurers
        if (p.getClase().equals(Mage.MAGE)) {
            //Mages
            Mage m = new Mage(p.getName(), p.getPlayer(), p.getNivell(), p.getClase(), p.getXp(),
                    p.getBody(), p.getMind(), p.getSpirit()) ;//meter atributos de p (no se puede casting)
            party.add(m);
        } else if (p.getClase().equals(Cleric.CLERIC) || p.getClase().equals(Cleric.PALADIN)) {
            //Clerics
            Cleric c = new Cleric(p.getName(), p.getPlayer(), p.getNivell(), p.getClase(), p.getXp(),
                    p.getBody(), p.getMind(), p.getSpirit()) ;//meter atributos de p (no se puede casting)
            party.add(c);
        } else {
            Aventurer a = new Aventurer(p.getName(), p.getPlayer(), p.getNivell(), p.getClase(), p.getXp(),
                    p.getBody(), p.getMind(), p.getSpirit()) ;//meter atributos de p (no se puede casting)
            party.add(a);

        }

    }

    /**
     * Establece el combate en función del índice especificado.
     *
     * @param index El índice del combate.
     */
    public void setCombat(int index) {
        combat = aventura.getCombats().get(index);
    }

    /**
     * Devuelve el número de combates de la aventura actual.
     *
     * @return El número de combates de la aventura.
     */
    public int getAdventureNCombats() { return aventura.getnCombats();}

    /**
     * Realiza la etapa de preparación y devuelve los mensajes generados.
     *
     * @return Los mensajes generados durante la etapa de preparación.
     */
    public String preparationStage() {
        String output = "";

        for (Personatge p:party) {
            if (p instanceof Aventurer) {
                //Aventurers
                if(p.getClase().equals(Aventurer.AVENTURER) || p.getClase().equals(Aventurer.WARRIOR)) {
                    //self-motivated
                    Aventurer a = (Aventurer) p;
                    a.selfMotivated();
                    output += p.getName()+ " uses Self-Motivated. Their Spirit increases in +1.\n";

                } else if (p.getClase().equals(Aventurer.CHAMPION)) {
                    //Champions

                    //Aumentar Spirit de la party +1
                    for (Personatge pSpirit:party) {
                        if (!pSpirit.isDead()) {
                            pSpirit.setSpirit(pSpirit.getSpirit() +1);
                        }
                    }
                    output += p.getName()+ " uses Motivational speech. Everyone’s Spirit increases in +1.\n";
                }

            } else if (p instanceof Cleric) {
                //Aumentar Mind de la party
                Cleric c = (Cleric) p;
                int aumento = c.goodLuck();
                for (Personatge pMind:party) {
                    if (!pMind.isDead()) {
                        pMind.setMind(pMind.getMind() + aumento);
                    }
                }

                if (p.getClase().equals(Cleric.CLERIC)) {
                    output += p.getName()+ " uses Prayer of good luck. Everyone’s Mind increases in +" +aumento+ ".\n";
                } else {
                    output += p.getName()+ " uses Blessing of good luck. Everyone’s Mind increases in +" +aumento+ ".\n";
                }

            } else if (p instanceof Mage){
                Mage m = (Mage) p;
                int shield = m.updateShield();
                output += p.getName()+ " uses Mage shield. Shield recharges to " +shield+ ".\n";

            }

        }

        return output;
    }

    /**
     * Calcula la iniciativa de los personajes y monstruos participantes en el combate.
     *
     * @return Una cadena que representa la lista de combatientes ordenados por iniciativa.
     */
    public String calcularIniciativa() {

        //Calculamos iniciativa de los personajes
        for (Personatge p:party) {
            p.calcularIniciativa();
        }

        combatients = new ArrayList<>();
        combatients.addAll(party);
        combatients.addAll(combat.getMonstres());

        //ordenamos el array de combatientes segun su valor de iniciativa
        Collections.sort(combatients, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int iniciativa1, iniciativa2;

                if (o1 instanceof Monstre) {
                    iniciativa1 = ((Monstre) o1).getInitiative();
                } else if (o1 instanceof Aventurer) {
                    iniciativa1 = ((Aventurer) o1).getIniciativa();
                } else if (o1 instanceof Cleric) {
                    iniciativa1 = ((Cleric) o1).getIniciativa();
                } else {
                    iniciativa1 = ((Mage) o1).getIniciativa();
                }

                if (o2 instanceof Monstre) {
                    iniciativa2 = ((Monstre) o2).getInitiative();
                } else if (o2 instanceof Aventurer) {
                    iniciativa2 = ((Aventurer) o2).getIniciativa();
                } else if (o2 instanceof Cleric) {
                    iniciativa2 = ((Cleric) o2).getIniciativa();
                } else {
                    iniciativa2 = ((Mage) o2).getIniciativa();
                }
                return Integer.compare(iniciativa2, iniciativa1);
            }
        });

        String output = "";
        for (Object combatient:combatients) {
            if (combatient instanceof Monstre) {
                output += "\t- " + ((Monstre) combatient).getInitiative() + " \t" + ((Monstre) combatient).getName() +"\n";
            } else {
                output += "\t- " + ((Personatge) combatient).getIniciativa() + " \t" + ((Personatge) combatient).getName() +"\n";

            }
        }
        return output;

    }

    /**
     * Verifica si hay personajes muertos en el grupo de aventureros.
     *
     * @return true si todos los personajes están muertos, false en caso contrario.
     */
    public boolean personatgesMorts() {

        //comprobar si algun personaje sigue vivo
        for (Personatge p:party) {
            if(!p.isDead()) {
                //si hay algun personaje vivo
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si todos los monstruos están muertos en el combate.
     *
     * @return true si todos los monstruos están muertos, false en caso contrario.
     */
    public boolean monstresMorts() {
        //comprobar si algun monstruo sigue vivo
        for (Monstre m: combat.getMonstres()) {

            if( !m.isDead()) {
                //si hay algun monstruo sigue vivo
                return false;
            }

        }

        return true;
    }

    /**
     * Muestra el estado de los personajes del grupo en la batalla.
     *
     * @return Una cadena que representa el estado de los personajes del grupo en la batalla.
     */
    public String showPartyInBattle() {
        String output = "";

        for (Personatge p:party) {
            if (p instanceof Mage) {
                Mage m = (Mage) p;
                output += "\t- " +p.getName()+ "\t" +p.getHp()+ " / " +p.getHpMax()+ " hit points (Shield: " +m.getShield()+ ")\n";
            } else {
                output += "\t- " +p.getName()+ "\t" +p.getHp()+ " / " +p.getHpMax()+ " hit points\n";
            }
        }

        return output;
    }

    /**
     * Realiza una ronda de combate y devuelve los mensajes generados durante la ronda.
     *
     * @return Los mensajes generados durante la ronda de combate.
     */
    public String playRound() {

        String output = "";

        //Cada combatiente hara un movimiento
        for (Object o:combatients) {

            //Si no han muerto todos los combatientes de algun equipo
            if ( !(personatgesMorts() || monstresMorts()) ) {

                //Aventurers
                if (o instanceof Aventurer) {
                    Aventurer a = (Aventurer) o;

                    if (!a.isDead()) {
                        //encontrar objetivo random (y comprobar que no este muerto)
                        int target = Dado.random(combat.getMonstres().size());
                        while ( ((Monstre) combat.getMonstres().get(target)).isDead() ) {
                            target++;
                            if (target >= combat.getMonstres().size()) {
                                target = 0;
                            }
                        }
                        if (a.getClase().equals(Aventurer.AVENTURER)) {
                            output += a.getName()+ " attacks " +combat.getMonstres().get(target).getName()+ " with Sword slash.\n";
                        } else {
                            output += a.getName()+ " attacks " +combat.getMonstres().get(target).getName()+ " with Improved sword slash.\n";
                        }

                        int lanzamiento = Dado.lanzar(10);
                        if (lanzamiento >= 2) {
                            //atacar

                            int dano;
                            if (lanzamiento == 10) {
                                dano = a.swordSlash() *2;
                                output += "Critical hit and deals " +dano+ " " +a.getTipusMal()+ " damage.\n";
                            } else {
                                dano = a.swordSlash();
                                output += "Hits and deals " +dano+ " " +a.getTipusMal()+ " damage.\n";
                            }
                            combat.getMonstres().get(target).receiveDamage(dano, a.getTipusMal());
                        } else {
                            output += "Fails and deals 0 " +a.getTipusMal()+ " damage.\n";
                        }

                        //mostrar si objetivo ha muerto
                        if (combat.getMonstres().get(target).isDead()) {
                            output += combat.getMonstres().get(target).getName() + " dies.\n\n";
                        } else {
                            output += "\n";
                        }
                    }

                } else if (o instanceof Cleric) {
                    Cleric c = (Cleric) o;

                    boolean curado = false; //indica si hemos curado a alguien
                    ArrayList<Personatge> personatgesCurar = new ArrayList<>(); //Personajes que hemos curado
                    int curacion = c.heal(); //Indica cantidad a curar
                    if (c.getClase().equals(Cleric.CLERIC)) {
                        for (Personatge p:party) {
                            if (!p.isDead()) {
                                if (p.needsHelp()) {
                                    p.curar(curacion);
                                    personatgesCurar.add(p);
                                    curado = true;
                                    break;
                                }
                            }
                        }

                    } else {
                        //Paladin

                        for (Personatge p:party) {
                            if (!p.isDead()) {
                                if (p.needsHelp()) {
                                    p.curar(curacion);
                                    personatgesCurar.add(p);
                                    curado = true;
                                }
                            }
                        }

                    }

                    if (curado) {
                        output += c.getName()+ " uses Prayer of healing. Heals " +curacion+ " hit points to ";
                        for (int i = 0; i < personatgesCurar.size(); i++){
                            if (i == personatgesCurar.size() -1) {
                                output += personatgesCurar.get(i).getName() + ".\n";
                            } else if (i == personatgesCurar.size() -2) {
                                output += personatgesCurar.get(i).getName() + " and ";
                            } else {
                                output += personatgesCurar.get(i).getName() + ", ";
                            }
                        }

                    } else {
                        //Ataque

                        //encontrar objetivo random (y comprobar que no este muerto)
                        int target = Dado.random(combat.getMonstres().size());
                        while ( ((Monstre) combat.getMonstres().get(target)).isDead() ) {
                            target++;
                            if (target >= combat.getMonstres().size()) {
                                target = 0;
                            }
                        }

                        //Aplicar dano
                        int dano = c.attack();
                        combat.getMonstres().get(target).receiveDamage(dano, c.getTipusMal());
                        if (c.getClase().equals(Cleric.CLERIC)) {
                            output += c.getName()+ " attacks " +combat.getMonstres().get(target).getName()+ " with Not on my watch.\n";
                        } else {
                            output += c.getName()+ " attacks " +combat.getMonstres().get(target).getName()+ " with Never on my watch. \n";
                        }
                        output += "Hits and deals " +dano+ " psychical damage.\n";

                        //mostrar si objetivo ha muerto
                        if (combat.getMonstres().get(target).isDead()) {
                            output += combat.getMonstres().get(target).getName() + " dies.\n\n";
                        } else {
                            output += "\n";
                        }

                    }
                } else if (o instanceof Mage) {
                    //Mages
                    Mage m = (Mage) o;

                    //Comprobamos si hay más de 3 monstruos vivos y guardamos los monstruos vivos
                    ArrayList<Monstre> monstres = new ArrayList<>();
                    int mVivos = 0;
                    for (Monstre monstre:combat.getMonstres()) {
                        if (!monstre.isDead()) {
                            monstres.add(monstre);
                            mVivos++;
                        }
                    }

                    if (mVivos < 3) {
                        //Arcane missile

                        //encontrar objetivo random (y comprobar que no este muerto)
                        int target = Dado.random(combat.getMonstres().size());
                        while ( combat.getMonstres().get(target).isDead() ) {
                            target++;
                            if (target >= combat.getMonstres().size()) {
                                target = 0;
                            }
                        }

                        //Aplicar dano
                        int dano = m.arcaneMissile();
                        combat.getMonstres().get(target).receiveDamage(dano, m.getTipusMal());
                        output += m.getName()+ " attacks " +combat.getMonstres().get(target).getName()+ " with Arcane missile.\n";
                        output += "Hits and deals " +dano+ " magical damage.\n";

                    } else {
                        //Fireball
                        output += m.getName()+ " attacks ";
                        for (int i = 0; i < monstres.size(); i++){
                            if (i == monstres.size() -1) {
                                output += monstres.get(i).getName() + " with Fireball.\n";
                            } else if (i == monstres.size() -2) {
                                output += monstres.get(i).getName() + " and ";
                            } else {
                                output += monstres.get(i).getName() + ", ";
                            }
                        }

                        //Atacar a todos los monstruos del combate
                        int dano = m.fireball();
                        for (Monstre monstre:combat.getMonstres()) {
                            if (!monstre.isDead()) {
                                monstre.receiveDamage(dano, m.getTipusMal());
                            }
                        }
                        output += "Hits and deals " +dano+ " magical damage.\n";

                        for (Monstre monstre:monstres) {
                            if (monstre.isDead()) {
                                output += monstre.getName() + " dies.\n\n";
                            } else {
                                output += "\n";
                            }

                        }
                    }

                }else if (o instanceof Monstre) {
                    //Monstruos
                    Monstre m = (Monstre) o;
                    if (!m.isDead()) {

                        if (m.getChallenge().equals(Monstre.BOSS)) {
                            //Si el monstruo es un BOSS

                            ArrayList<Personatge> targets = new ArrayList<>();
                            for (Personatge target:party) {
                                if (!target.isDead()){
                                    targets.add(target);
                                }
                            }

                            output += m.getName()+ " attacks ";
                            for (int i = 0; i < targets.size(); i++) {
                                if (i == targets.size() -1) {
                                    output += targets.get(i).getName() + ".\n";
                                } else if (i == targets.size() -2) {
                                    output += targets.get(i).getName() + " and ";
                                } else {
                                    output += targets.get(i).getName() + ", ";
                                }
                            }

                            //Lanzamos dado para ver como sera el ataque del BOSS
                            int lanzamiento = Dado.lanzar(10);
                            if (lanzamiento >= 2) {
                                int dano;
                                if (lanzamiento == 10) {
                                    dano = m.atacar() *2;
                                    output += "Critical hit and deals " +dano+ " " +m.getDamageType()+ " damage.\n";
                                } else {
                                    dano = m.atacar();
                                    output += "Hits and deals " +dano+ " " +m.getDamageType()+ " damage.\n";
                                }

                                //Atacar a toda la party
                                for (Personatge target:targets) {
                                    if (!target.isDead()) {
                                        target.receiveDamage(dano, m.getDamageType());

                                        //mostrar si objetivo ha muerto
                                        if (target.isDead()) {
                                            output += target.getName() + " falls unconscious.\n\n";
                                        } else {
                                            output += "\n";
                                        }
                                    }
                                }

                            } else {
                                //Fallo en el ataque
                                output += "Fails and deals 0 " +m.getDamageType()+ " damage.\n";
                            }


                        } else {
                            //Si el monstruo no es un BOSS

                            //encontrar objetivo random (y comprobar que no este muerto)
                            int target = Dado.random(party.size());
                            while (party.get(target).isDead()) {
                                target++;
                                if (target >= party.size()) {
                                    target = 0;
                                }
                            }
                            output += m.getName()+ " attacks " +party.get(target).getName()+ ".\n";

                            //Atacar objetivo
                            int lanzamiento = Dado.lanzar(10);
                            if (lanzamiento >= 2) {
                                int dano;
                                if (lanzamiento == 10) {
                                    dano = m.atacar() *2;
                                    output += "Critical hit and deals " +dano+ " " +m.getDamageType()+ " damage.\n";
                                } else {
                                    dano = m.atacar();
                                    output += "Hits and deals " +dano+ " " +m.getDamageType()+ " damage.\n";
                                }
                                party.get(target).receiveDamage(dano, m.getDamageType());

                            } else {
                                output += "Fails and deals 0 " +m.getDamageType()+ " damage.\n";
                            }

                            //mostrar si objetivo ha muerto
                            if (party.get(target).isDead()) {
                                output += party.get(target).getName() + " falls unconscious.\n\n";
                            } else {
                                output += "\n";
                            }
                        }

                    }


                }
            }
        }

        return output;
    }

    /**
     * Realiza las acciones al finalizar una ronda y devuelve una cadena
     * de texto representando dicha informacion.
     *
     * @return Cadena de texto con las acciones al finalizar la ronda.
     */
    public String accionsFiRonda() {
        String output = "";

        for (Personatge p:party) {
            //Aventurers
            if (p instanceof Aventurer) {
                Aventurer a = (Aventurer) p;
                if (a.isDead()) {
                    output += a.getName() + " is unconscious\n";
                } else {
                    output += a.getName() + " uses Bandage time. Heals " +a.bandageTime()+ " hit points.\n";
                }

            } else if (p instanceof Cleric) {
                Cleric c = (Cleric) p;
                if (c.isDead()) {
                    output += c.getName() + " is unconscious\n";
                } else {
                    if (c.getClase().equals(Cleric.CLERIC)) {
                        int curacion = c.heal();
                        c.curar(curacion);
                        output += c.getName()+ " uses Prayer of self-healing. Heals " +curacion+ " hit points.\n";
                    } else {
                        //Paladin

                        ArrayList<Personatge> personatgesCurar = new ArrayList<>(); //Personajes que hemos curado
                        int curacion = c.heal(); //Indica cantidad a curar

                        for (Personatge pCurar:party) {
                            if (!pCurar.isDead()) {
                                pCurar.curar(curacion);
                                personatgesCurar.add(pCurar);
                            }
                        }


                        if (!personatgesCurar.isEmpty()) {
                            output += c.getName()+ " uses Prayer of healing. Heals " +curacion+ " hit points to ";
                            for (int i = 0; i < personatgesCurar.size(); i++){
                                if (i == personatgesCurar.size() -1) {
                                    output += personatgesCurar.get(i).getName() + ".\n";
                                } else if (i == personatgesCurar.size() -2) {
                                    output += personatgesCurar.get(i).getName() + " and ";
                                } else {
                                    output += personatgesCurar.get(i).getName() + ", ";
                                }
                            }
                        }

                    }
                }

            }

            //Mages no hacen ninguna accion
        }

        return output;
    }

    /**
     * Aumenta la experiencia de los personajes en el grupo.
     *
     * @return Cadena de texto que indica la cantidad de experiencia ganada.
     * @throws BusinessException Si ocurre algún error en el proceso.
     */
    public String aumentarXP() throws BusinessException {
        String output = "";

        //Calcular experiencia a ganar
        int xp = 0;
        for (Monstre m:combat.getMonstres()) {
            xp += m.getExperience();
        }


        //sumar experiencia a cada personaje
        for (Personatge p:party) {

            output += p.getName() + " gains " + xp + " xp.";
            boolean levelUp = p.ganarXP(xp);

            //Guardar cambios en Storage (persistencia)
            updateCharacter(p);

            if (levelUp) {
                //si ha subido de nivel
                output += " " + p.getName() + " levels up. They are now lvl " + p.getNivell() + "!\n";
            } else {
                output += "\n";
            }

        }

        return output;
    }

    /**
     * Actualiza un personaje en persistencia.
     *
     * @param p El personaje a actualizar.
     * @throws BusinessException Si ocurre un error al intentar actualizar el personaje.
     */
    private void updateCharacter(Personatge p) throws BusinessException {

            //Guardar cambios en JSON
            try {
                if (dataType == LOCAL_DATA) {
                    personatgesDAO.updateCharacter(p);
                } else {
                    personatgesDAO.updateCharacter(p);
                }
            }catch (PersistenceException e) {
                throw new BusinessException("Error: Couldn't modify the Characters file", e);
            }

    }

    /**
     * Obtiene la lista de aventuras desde el almacenamiento en persistencia.
     *
     * @return Una lista de objetos Aventura que representa las aventuras obtenidas.
     * @throws BusinessException Si ocurre un error al intentar obtener las aventuras desde el almacenamiento.
     */
    public ArrayList<Aventura> getAventures() throws BusinessException {
        try {
            return aventuresDAO.getAventures();

        } catch (PersistenceException e) {
            throw new BusinessException("Error trying to get Adventures from storage.", e);
        }
    }
}
