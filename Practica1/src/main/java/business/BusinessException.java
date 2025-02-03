package business;

public class BusinessException extends Exception{
    /**
     * Constructor de la clase PresentationException.
     * @param message El mensaje que describe la excepción.
     * @param cause La causa subyacente de la excepción.
     */
    public BusinessException(String message, Exception cause) {
        super(message, cause);
    }
}
