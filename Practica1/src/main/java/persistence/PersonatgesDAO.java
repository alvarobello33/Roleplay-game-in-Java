package persistence;

import business.entities.personatge.Personatge;

import java.util.ArrayList;

/**
 * Esta interfaz proporciona métodos para leer y escribir los datos de los personajes en formato JSON.
 */
public interface PersonatgesDAO {

    /**
     * Lee todos los personajes almacenados en el archivo JSON.
     *
     * @return Una lista de objetos Personatge que representa los personajes leídos.
     * @throws PersistenceException Si ocurre algún error durante la lectura del archivo.
     */
    ArrayList<Personatge> readPersonatges() throws PersistenceException;

    /**
     * Crea un nuevo personaje y lo guarda en el archivo JSON.
     *
     * @param p El personaje que se desea crear y guardar.
     * @throws PersistenceException Si ocurre algún error durante la operación de creación y guardado.
     */
    void createPersonatge(Personatge p) throws PersistenceException;

    /**
     * Elimina un personaje existente del archivo JSON.
     *
     * @param p El personaje que se desea eliminar.
     * @throws PersistenceException Si ocurre algún error durante la operación de eliminación.
     */
    void deletePersonatge(Personatge p) throws PersistenceException;

    /**
     * Actualiza los datos de un personaje existente en el archivo JSON.
     *
     * @param p El personaje que se desea actualizar.
     * @throws PersistenceException Si ocurre algún error durante la operación de actualización.
     */
    void updateCharacter(Personatge p) throws PersistenceException;

}


