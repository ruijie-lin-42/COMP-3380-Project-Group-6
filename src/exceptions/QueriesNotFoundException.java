package exceptions;

public class QueriesNotFoundException extends RuntimeException {
    public QueriesNotFoundException(Throwable cause) {
        super(cause);
    }
}
