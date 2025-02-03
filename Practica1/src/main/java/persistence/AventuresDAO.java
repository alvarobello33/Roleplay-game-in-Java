package persistence;

import business.entities.Aventura;

import java.util.ArrayList;

/**
 * Interfaz que define las operaciones para acceder a los datos de las aventuras.
 */
public interface AventuresDAO {

    /**
     * Lee todas las aventuras almacenadas.
     *
     * @return Una lista de objetos Aventura que representa las aventuras leídas.
     * @throws PersistenceException Si ocurre algún error durante la lectura.
     */
    ArrayList<Aventura> getAventures() throws PersistenceException;

    /**
     * Guarda una aventura en el almacenamiento.
     *
     * @param aventura La aventura que se desea guardar.
     * @throws PersistenceException Si ocurre algún error durante la operación de guardado.
     */
    void saveAventura(Aventura aventura) throws PersistenceException;
}

