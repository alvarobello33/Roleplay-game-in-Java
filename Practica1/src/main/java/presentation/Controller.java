package presentation;


import business.AdventureManager;
import business.BusinessException;
import business.PersonatgeManager;
import business.entities.personatge.Aventurer;
import business.entities.personatge.Cleric;
import business.entities.personatge.Mage;

/**
 * Controlador principal del juego.
 */
public class Controller {

    private Menu menu;
    private PersonatgeManager personatgeManager;
    private AdventureManager adventureManager;

    private static final int CHARACTER_CREATION = 1;
    private static final int LIST_CHARACTERS = 2;
    private static final int CREATE_ADVENTURE = 3;
    private static final int START_ADVENTURE = 4;
    private static final int EXIT = 5;


    /**
     * Constructor de la clase Controller.
     *
     * @param menu Objeto Menu utilizado para mostrar las opciones del menú.
     * @param personatgeManager Objeto PersonatgeManager utilizado para gestionar los personajes.
     * @param adventureManager Objeto AdventureManager utilizado para gestionar las aventuras.
     */
    public Controller(Menu menu, PersonatgeManager personatgeManager, AdventureManager adventureManager) {
        this.menu = menu;
        this.personatgeManager = personatgeManager;
        this.adventureManager = adventureManager;
    }

    /**
     * Método principal que ejecuta el juego.
     *
     * @throws BusinessException Excepción lanzada si ocurre algún error en la ejecución del juego.
     */
    public void run() throws BusinessException {
        int option;
        boolean stop;
        int typeData;

        menu.showWelcome();

        do {
            typeData = menu.askForInteger("-> Answer: ");

            if (typeData != 1 && typeData != 2) {
                menu.showMessage("\nInvalid number.\n");
            }

        } while (typeData != 1 && typeData != 2);

        //Cargar tipo de Storage
        try {
            menu.showMessage("Loading data...");
            adventureManager.setDataType(typeData);
            personatgeManager.setDataType(typeData);

            //Comprobar que se carga correctamente la informacion de Storage
            adventureManager.readMonsters();
            adventureManager.getAventures();
            personatgeManager.readPersonatges();

        } catch (BusinessException e) {
            //En caso de no conectar correctamente con la API se cambiara a LOCAL_DATA (aqui solo puede causar excepccion el modo CLOUD_DATA)
            menu.showMessage("Couldn’t connect to the remote server.");
            menu.showMessage("Reverting to local data.\n");
            menu.showMessage("Loading data...");
            typeData = PersonatgeManager.LOCAL_DATA;
            adventureManager.setDataType(typeData);
            personatgeManager.setDataType(typeData);
        }

        //Comprobar si se efectuan correctamente la lectura de Storage Local (en caso contrario salta exception)
        if (typeData == PersonatgeManager.LOCAL_DATA) {
            try {
                adventureManager.readMonsters();
                adventureManager.getAventures();
                personatgeManager.readPersonatges();

            } catch (BusinessException e) {
                throw new BusinessException("Error: Can not acces to the LOCAL STORAGE.", null);
            }
        }

        menu.showMessage("Data was successfully loaded.\n");
        do {
            menu.showMenu(personatgeManager.getPersonatgesLength());
            option = menu.askForInteger("Your answer: ");
            stop = executeOption(option);

            System.out.println("end");
        } while (!stop);
    }


    /**
     * Ejecuta la opción seleccionada por el usuario.
     *
     * @param option Opción seleccionada por el usuario.
     * @return Booleano que indica si se debe detener la ejecución del juego.
     */
    private boolean executeOption(int option) {

        try {
            menu.showMessage("");

            switch (option) {
                case CHARACTER_CREATION:
                    createPersonatge();
                    break;
                case LIST_CHARACTERS:
                    listPersonatges();
                    break;
                case CREATE_ADVENTURE:
                    createAdventure();
                    break;
                case START_ADVENTURE:
                    startAdventure();
                    break;

                case EXIT:
                    menu.showMessage("Tavern keeper: “Are you leaving already? See you soon, adventurer.”");
                    return true;

                default:
                    menu.showMessage("Incorrect option");
                    break;
            }

            return false;

        } catch (BusinessException e) {
            menu.showMessage("error executing option");
            return false;
        }
    }

    /**
     * Crea un nuevo personaje.
     *
     * @throws BusinessException Excepción lanzada si ocurre algún error al crear el personaje.
     */
    private void createPersonatge () throws BusinessException {
        //Personatge personatge = new Personatge();
        menu.showMessage("Tavern keeper: “Oh, so you are new to this land.”");

        boolean correctName;
        String nom;
        do {
            menu.showMessage("“What’s your name?”");
            menu.showMessage("");
            nom = menu.askForString("-> Enter your name: ");

            char[] nomChars = nom.toCharArray();
            correctName = personatgeManager.isCharacterNameCorrect(nomChars);
            nom = new String(nomChars);
            if (!correctName) {
                System.out.println("Invalid character name. The name should only contain letters and start with uppercase.");
            }

        } while (!correctName);

        //Pedimos nombre de player y comprobamos que sea correcto
        menu.showMessage("");
        menu.showMessage("Tavern keeper: “Hello, "+nom+", be welcome.”");
        menu.showMessage("“And now, if I may break the fourth wall, who is your Player?”");
        menu.showMessage("");
        String player = menu.askForString("-> Enter the player’s name: ");

        menu.showMessage("");
        menu.showMessage("Tavern keeper: “I see, I see...”");
        menu.showMessage("“Now, are you an experienced adventurer?”");
        menu.showMessage("");

        int nivell;
        do {
            nivell = menu.askForInteger("Enter the character’s level [1..10]: ");

            if (nivell < 1 || nivell > 10) {
                menu.showMessage("\nSorry, this level is out of range.\n");
            }
        } while (nivell < 1 || nivell > 10);

        menu.showMessage("");
        menu.showMessage("Tavern keeper: “Oh, so you are level "+nivell+"!”");
        menu.showMessage("“Great, let me get a closer look at you...”");
        menu.showMessage("");
        menu.showMessage("Generating your stats...");
        menu.showMessage("");

        //String statsStr = personatgeManager.generateStats(personatge);
        String str = personatgeManager.createPersonatge(nom, player, nivell);
        menu.showMessage(str);

        menu.showMessage("\nTavern keeper: “Looking good!”");
        menu.showMessage("“And, lastly, ?”");

        //Elegir tipo de personaje
        String type;
        do {
            type = menu.askForString("\n-> Enter the character’s initial class [Adventurer, Cleric, Mage]: ");
            if ( !(type.equals(Aventurer.AVENTURER) || type.equals(Cleric.CLERIC) || type.equals(Mage.MAGE)) ) {
                menu.showMessage("\nThis type of character is not in the game.");
            }
        } while ( !(type.equals(Aventurer.AVENTURER) || type.equals(Cleric.CLERIC) || type.equals(Mage.MAGE)) );
        str = personatgeManager.savePersonatge(type);
        menu.showMessage(str);

    }

    /**
     * Método para listar los personajes y realizar acciones relacionadas.
     * @throws BusinessException Si ocurre una excepción de negocio.
     */
    private void listPersonatges () throws BusinessException {

        menu.showMessage("Tavern keeper: “Lads! They want to see you!”");
        menu.showMessage("“Who piques your interest?”");
        menu.showMessage("");
        String player = menu.askForString("-> Enter the name of the Player to filter: ");
        menu.showMessage("");
        menu.showMessage("You watch as some adventurers get up from their chairs and approach you.");
        menu.showMessage("");

        //buscamos los personajes cuyo player contenga el name introducido por usuario
        String personatges = personatgeManager.searchPersonatges(player);
        menu.showMessage(personatges);

        menu.showMessage("\t0. Back\n");
        int eleccio = menu.askForInteger("Who would you like to meet [0.." + personatgeManager.getPersonatgesFiltratsLength() + "]:");

        if (eleccio == 0) {
            return;
        }

        String name = personatgeManager.getNameByIndex(eleccio -1);

        menu.showMessage("\nTavern keeper: “Hey " + name + " get here; the boss wants to see you!”\n");
        menu.showMessage("");
        menu.showMessage(personatgeManager.getInfoByIndex(eleccio -1));

        menu.showMessage("\n[Enter name to delete, or press enter to cancel]");
        String confirmation = menu.askForString("Do you want to delete " +name+ "? ");

        if (confirmation.length() == 0){
            return;
        }

        boolean deleted = personatgeManager.deletePersonatgeWithIndex(eleccio -1, confirmation);
        if (deleted) {
            //eliminar personatge
            //...

            menu.showMessage("\nTavern keeper: “I’m sorry kiddo, but you have to leave.”");
            menu.showMessage("\nCharacter " + name + " left the Guild.");

        }
    }

    /**
     * Método para crear una aventura.
     * @throws BusinessException Si ocurre una excepción de negocio.
     */
    private void createAdventure () throws BusinessException {

        menu.showMessage("\nTavern keeper: “Planning an adventure? Good luck with that!”\n");
        String name = menu.askForString("-> Name your adventure: ");

        menu.showMessage("\nTavern keeper: “You plan to undertake " + name + ", really?”\n" + "“How long will that take?”\n");
        int encounters;
        int wrongEncounters = 0;
        do {
            encounters = menu.askForInteger("-> How many encounters do you want [1..4]: ");
            if (!(encounters > 0 && encounters <= 4)) {
                menu.showMessage("\nSorry this number is out of range, try again.\n");

                //Controlar caso en que se introduce encounters más de 3 veces mal
                wrongEncounters++;
                if (wrongEncounters > 3) {
                    throw new BusinessException("You inserted more than 3 times worng the encounters number.", null);
                }
            }
        } while(!(encounters > 0 && encounters <= 4));

        adventureManager.createAdventure(name, encounters);

        if (encounters > 1) {
            menu.showMessage("\nTavern keeper: “"+ encounters +" encounters? That is too much for me...”\n\n");
        } else {
            menu.showMessage("\nTavern keeper: “"+ encounters +" encounter? That is too much for me...”\n\n");
        }


        //creacio de combats

        for (int i=0; i < encounters; i++) {
            int option;
            do {
                adventureManager.createCombat(i);
                menu.showMessage("\n\n* Encounter " + (i+1) + " / " + encounters);
                menu.showMessage("* Monsters in encounter");
                //mostrar monstruos
                menu.showMessage(adventureManager.getMonstresCombat(true));

                menu.showMessage("1. Add monster");
                menu.showMessage("2. Remove monster");
                menu.showMessage("3. Continue");
                menu.showMessage("");

                option = menu.askForInteger("-> Enter an option [1..3]: ");
                switch (option) {
                    case 1:
                        menu.showMessage(adventureManager.getMonstres());
                        int indexMonstre = menu.askForInteger("-> Choose a monster to add [1..31]: ");
                        String nomMonstre = adventureManager.getMonsterName(indexMonstre-1);
                        int quantitat = menu.askForInteger("-> How many " +nomMonstre+ "(s) do you want to add: ");
                        boolean correct = adventureManager.addMonstre(quantitat);
                        if (!correct) {
                            menu.showMessage("\nError: You tried to add more than 1 Boss monster.");
                        }
                        break;
                    case 2:
                        int index = menu.askForInteger("-> Which monster do you want to delete: ");
                        menu.showMessage(adventureManager.deleteMonstre(index-1));
                        break;
                    case 3:
                        //Comprobar que se haya añadido a algun monstruo
                        if (adventureManager.emptyCombat()) {
                            menu.showMessage("\nYou need to choose at least one monster for the encounter.");
                            option = 0;
                        }
                        break;
                    default:
                        menu.showMessage("\nThis is not a valid option.");
                        break;
                }

            }while (option !=3);
        }

        adventureManager.saveAventura();
        menu.showMessage("\nTavern keeper: “Great plan lad! I hope you won’t die!”");
        menu.showMessage("\nThe new adventure " +name+ " has been created.");
    }

    /**
     * Método para iniciar una aventura.
     * @throws BusinessException Si ocurre una excepción de negocio.
     */
    private void startAdventure () throws BusinessException {
        if (personatgeManager.getPersonatgesLength() >=3) {
            menu.showMessage("\nTavern keeper: “So, you are looking to go on an adventure?”");
            menu.showMessage("“Where do you fancy going?”");
            menu.showMessage("\nAvailable adventures:");
            menu.showMessage(adventureManager.showAdventures());

            int indexAventura = menu.askForInteger("-> Choose an adventure: ");

            String name = adventureManager.setAdventure(indexAventura -1);
            menu.showMessage("Tavern keeper: “" +name+ "”");
            menu.showMessage("“And how many people shall join you?”");


            //Pedir numero de personajes para jugar
            int nPersonatges;
            do {
                nPersonatges = menu.askForInteger("\n-> Choose a number of characters [3.." +personatgeManager.getPersonatgesLength()+ "]:");
                adventureManager.createParty(nPersonatges);

                if (nPersonatges < 3) {
                    menu.showMessage("\nNot enough characters.");
                } else if (nPersonatges > personatgeManager.getPersonatgesLength()) {
                    menu.showMessage("\nYou don't have that ammount of characters.");
                }

            } while (nPersonatges < 3 || nPersonatges > personatgeManager.getPersonatgesLength());

            //elegimos la party
            menu.showMessage("\nTavern keeper: “Great, " +nPersonatges+ " it is.”");
            menu.showMessage("“Who among these lads shall join you?”");

            for(int i = 0; i < nPersonatges; i++) {
                menu.showMessage("\n\n------------------------------");
                menu.showMessage("Your party (" +i+ " / " +nPersonatges+ "):");
                menu.showMessageWithoutEnter(adventureManager.showParty(nPersonatges));
                menu.showMessage("------------------------------");
                menu.showMessage("Available characters:");

                //Fem que mostri tots els personatges fent que busqui un sense nom
                //(com quan en la opcio de llistar personatges no ens introduien res)
                menu.showMessage(personatgeManager.searchPersonatges(""));
                int index = menu.askForInteger("-> Choose character " +(i+1)+ " in your party: ");

                adventureManager.addCharacterToParty(index -1);
            }

            menu.showMessage("\n\n------------------------------");
            menu.showMessage("Your party (" +nPersonatges+ " / " +nPersonatges+ "):");
            menu.showMessageWithoutEnter(adventureManager.showParty(nPersonatges));
            menu.showMessage("------------------------------");

            menu.showMessage("\nTavern keeper: “Great, good luck on your adventure lads!”");
            menu.showMessage("\n\nThe “" +name+ "” will start soon...");


            //PLAY

            //bucle que recorre tots els combats de laventura
            for (int i=0; i < adventureManager.getAdventureNCombats(); i++) {
                menu.showMessage("\n---------------------");
                menu.showMessage("Starting Encounter " +(i+1)+ ":");
                adventureManager.setCombat(i);
                menu.showMessageWithoutEnter(adventureManager.getMonstresCombat(false));
                menu.showMessage("---------------------");
                menu.showMessage("\n\n-------------------------");
                menu.showMessage("*** Preparation stage ***");
                menu.showMessage("-------------------------");

                //preparation stage
                menu.showMessage(adventureManager.preparationStage());

                //Calcular iniciativa
                menu.showMessage("Rolling initiative...");
                menu.showMessage(adventureManager.calcularIniciativa());


                //combat stage

                menu.showMessage("--------------------");
                menu.showMessage("*** Combat stage ***");
                menu.showMessage("--------------------");

                int round = 0;
                while ( !(adventureManager.personatgesMorts() || adventureManager.monstresMorts()) ) {
                    //se haran rondas hasta que mueran todos los personajes o todos los monstruos
                    menu.showMessage("Round " +(round +1)+ ":");
                    menu.showMessage("Party:");
                    menu.showMessage(adventureManager.showPartyInBattle());

                    //Se hara un ataque por personaje/monstruo en batalla
                    menu.showMessage(adventureManager.playRound());



                    round++;
                }



                //fi combat
                if (adventureManager.personatgesMorts()) {
                    //cas derrota
                    menu.showMessage("Tavern keeper: “Lad, wake up. Yes, your party fell unconscious.”");
                    menu.showMessage("“Don’t worry, you are safe back at the Tavern.”");
                    break;
                } else {
                    //cas victoria, Fase descans curt
                    menu.showMessage("End of round " +(round)+ ".");
                    menu.showMessage("All enemies are defeated.\n");

                    menu.showMessage("------------------------");
                    menu.showMessage("*** Short rest stage ***");
                    menu.showMessage("------------------------");

                    //obtindre experiencia
                    menu.showMessage(adventureManager.aumentarXP());

                    //accions
                    menu.showMessage(adventureManager.accionsFiRonda());

                }


            }

            if (adventureManager.monstresMorts()) {
                menu.showMessage("\nCongratulations, your party completed “" +name+ "”");
            }


        } else {
            menu.showMessage("\nSorry you have less than 3 characters.");
        }
    }



}