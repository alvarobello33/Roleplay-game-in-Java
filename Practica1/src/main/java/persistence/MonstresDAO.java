package persistence;

import business.entities.Monstre;

import java.util.ArrayList;

/**
 * Esta interfaz proporciona métodos para leer los datos de los monstruos en formato JSON.
 */
public interface MonstresDAO {
    /**
     * Lee todos los monstruos almacenados en el almacenamiento.
     *
     * @return Lista de monstruos leídos.
     * @throws PersistenceException Si ocurre algún error durante la lectura de los monstruos.
     */
    ArrayList<Monstre> readMonsters() throws PersistenceException;
}
