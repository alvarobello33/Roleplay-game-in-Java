package persistence;

public class PersistenceException extends Exception{

    /**
     * Constructor de la clase PresentationException.
     * @param message El mensaje que describe la excepción.
     * @param cause La causa subyacente de la excepción.
     */
    public PersistenceException(String message, Exception cause) {
        super(message, cause);
    }
}
