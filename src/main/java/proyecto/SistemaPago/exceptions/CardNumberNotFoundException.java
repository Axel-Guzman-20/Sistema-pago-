package proyecto.SistemaPago.exceptions;

public class CardNumberNotFoundException extends RuntimeException {
    public CardNumberNotFoundException(String message) {
        super(message);
    }
}
