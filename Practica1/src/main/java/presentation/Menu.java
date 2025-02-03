package presentation;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Esta clase representa el menú de interacción con el usuario.
 */
public class Menu {

    private Scanner scanner;

    /**
     * Constructor de la clase Menu.
     * Inicializa el objeto Scanner para recibir la entrada de datos del usuario.
     */
    public Menu() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra un mensaje de bienvenida.
     */
    public void showWelcome() {
        System.out.println("   ____ _ __ __ ____ ___ ___ _____");
        System.out.println("  / __/(_)__ _ ___ / /___ / / / __// _ \\ / _ \\ / ___/");
        System.out.println(" _\\ \\ / // ' \\ / _ \\ / // -_) / /__ _\\ \\ / , _// ___// (_ /");
        System.out.println("/___//_//_/_/_// .__//_/ \\__/ /____//___//_/|_|/_/ \\___/");
        System.out.println("              /_/ \n");
        System.out.println("Welcome to Simple LSRPG.\n");

        System.out.println("Do you want to use your local or cloud data?");
        System.out.println("\t1) Local data");
        System.out.println("\t2) Cloud data\n");

    }

    /**
     * Muestra las opciones del menú.
     *
     * @param personatgesLength Cantidad de personajes disponibles.
     */
    public void showMenu(int personatgesLength) {
        System.out.println();
        System.out.println("The tavern keeper looks at you and says:");
        System.out.println("“Welcome adventurer! How can I help you?”");
        System.out.println();
        System.out.println("\t1) Character creation");
        System.out.println("\t2) List characters");
        System.out.println("\t3) Create an adventure");
        if (personatgesLength < 3) {
            System.out.println("\t4) Start an adventure (disabled: create 3 characters first)");
        } else {
            System.out.println("\t4) Start an adventure");
        }
        System.out.println("\t5) Exit");
        System.out.println();
    }

    /**
     * Solicita al usuario un entero a través de la consola.
     *
     * @param message Mensaje que se muestra para solicitar el entero.
     * @return El entero ingresado por el usuario.
     */
    public int askForInteger(String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("This isn't an integer!");
            } finally {
                scanner.nextLine();     //para barrer \n introducido despues de int
            }
        }
    }

    /**
     * Solicita al usuario una cadena de texto a través de la consola.
     *
     * @param message Mensaje que se muestra para solicitar la cadena de texto.
     * @return La cadena de texto ingresada por el usuario.
     */
    public String askForString(String message) {
        while (true) {
            try {
                System.out.print(message);
                return scanner.nextLine().trim();   //.trim() eliminara \n y posibles espacios introducidos al final del texto
            } catch (InputMismatchException e) {
                System.out.println("This isn't a text!");
            }
        }
    }


    /**
     * Muestra un mensaje en la consola.
     *
     * @param message El mensaje a mostrar.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Muestra un mensaje en la consola sin agregar una nueva línea.
     *
     * @param message El mensaje a mostrar.
     */
    public void showMessageWithoutEnter(String message) {
        System.out.print(message);
    }


}
