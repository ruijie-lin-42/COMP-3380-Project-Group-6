package exceptions;

public class IncorrectParameterTypeException extends RuntimeException {
    public IncorrectParameterTypeException(String message) {
        super(message);
    }
}
