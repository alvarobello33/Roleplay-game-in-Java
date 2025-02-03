
import business.AdventureManager;
import business.BusinessException;
import business.PersonatgeManager;

import presentation.Controller;
import presentation.Menu;

/**
 * Clase principal que contiene el método `main` para iniciar la aplicación.
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args Los argumentos de línea de comandos (no se utilizan en esta aplicación).
     */
    public static void main(String[] args) {
        // Crear instancias de las clases necesarias
        Menu menu = new Menu();
        PersonatgeManager personatgeManager = new PersonatgeManager();
        AdventureManager adventureManager = new AdventureManager();

        try {
            // Crear una instancia del controlador y ejecutar la aplicación
            Controller controller = new Controller(menu, personatgeManager, adventureManager);
            controller.run();
        } catch (BusinessException e) {
            // En caso de excepción, mostrar el mensaje de error en el menú
            menu.showMessage(e.toString());
        }
    }
}
