package event.logging.transformer;

public class SchemaTransformerException extends RuntimeException {

    public SchemaTransformerException(String message) {
        super(message);
    }

    public SchemaTransformerException(String message, Throwable cause) {
        super(message, cause);
    }
}
