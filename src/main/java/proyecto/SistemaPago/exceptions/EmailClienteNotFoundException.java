package proyecto.SistemaPago.exceptions;

public class EmailClienteNotFoundException extends RuntimeException {

    public EmailClienteNotFoundException(String message) {
        super(message);
    }
}

