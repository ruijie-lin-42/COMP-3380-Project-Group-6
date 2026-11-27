package exceptions;

public class QueriesParseException extends RuntimeException {

    public enum Field {CONFIGURATION, PARSE}

    private final Field field;

    public QueriesParseException(String message, Field field) {
        super(message);
        this.field = field;
    }

    public QueriesParseException(Throwable cause, Field field) {
        super(cause);
        this.field = field;
    }

    public QueriesParseException(String message, Throwable cause, Field field) {
        super(message, cause);
        this.field = field;
    }

    public Field getField() {
        return field;
    }
}
