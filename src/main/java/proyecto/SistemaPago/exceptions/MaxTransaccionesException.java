package proyecto.SistemaPago.exceptions;

public class MaxTransaccionesException extends RuntimeException{

    public MaxTransaccionesException(String message) {
        super(message);
    }
}
