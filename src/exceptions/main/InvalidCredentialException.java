package exceptions.main;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(Throwable cause) {
        super(cause);
    }
}
