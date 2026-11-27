package exceptions.repository;

public class IncorrectParameterTypeException extends RuntimeException {
    public IncorrectParameterTypeException(String message) {
        super(message);
    }
}
