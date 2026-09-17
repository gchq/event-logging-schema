package event.logging.transformer.schemas;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/// Simple error handler for testing that collects warnings, errors and fatal errors in lists.
class ListErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListErrorHandler.class);

    private final List<String> warnings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final List<String> fatalErrors = new ArrayList<>();

    public void handleSaxException(final SAXException exception) {
        if (exception != null) {
            final String message = exception.getMessage();
            LOGGER.debug("SAX Exception - {}", message);
            errors.add(message);
        }
    }

    @Override
    public void warning(final SAXParseException exception) {
        if (exception != null) {
            final String message = exception.getMessage();
            LOGGER.debug("SAX WARNING - {}", message);
            warnings.add(message);
        }
    }

    @Override
    public void error(final SAXParseException exception) {
        if (exception != null) {
            final String message = exception.getMessage();
            LOGGER.debug("SAX ERROR - {}", message);
            errors.add(exception.getMessage());
        }
    }

    @Override
    public void fatalError(final SAXParseException exception) {
        if (exception != null) {
            final String message = exception.getMessage();
            LOGGER.debug("SAX FATAL - {}", message);
            fatalErrors.add(exception.getMessage());
        }
    }

    int getWarningCount() {
        return warnings.size();
    }

    int getErrorCount() {
        return errors.size();
    }

    int getFatalErrorCount() {
        return fatalErrors.size();
    }

    boolean hasAnyErrors() {
        return !errors.isEmpty() || !fatalErrors.isEmpty();
    }

    boolean errorsContainString(final String string) {
        Objects.requireNonNull(string);
        final String lowerStr = string.toLowerCase();
        return errors.stream()
                .map(String::toLowerCase)
                .anyMatch(error -> error.contains(lowerStr));
    }

    void assertErrorsContainString(final String string) {
        Objects.requireNonNull(string);
        final String lowerStr = string.toLowerCase();
        assertThat(errors.stream()
                .map(String::toLowerCase)
                .anyMatch(error -> error.contains(lowerStr)))
                .withFailMessage("Expected errors to contain string '%s'", string)
                .isTrue();
    }
}
