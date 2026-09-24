package event.logging.transformer.schemas;


import event.logging.transformer.SystemService;
import event.logging.transformer.SystemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MockSystemService implements SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSystemService.class);

    private final SystemService delegate;
    private final List<String> lines = new ArrayList<>();
    private Integer exitStatus = null;

    MockSystemService() {
        this.delegate = new SystemServiceImpl();
    }

    @Override
    public void exit(final int status) {
        LOGGER.debug("exit() - status: {}", status);
        throw new SystemExitException(status);
    }

    @Override
    public void println() {
        delegate.println();
        lines.add("");
    }

    @Override
    public void println(final String str) {
        delegate.println(str);
        lines.add(str);
    }

    @Override
    public void printf(final String format, final Object... args) {
        delegate.printf(format, args);
        lines.add(String.format(format, args));
    }

    Integer getExitStatus() {
        return exitStatus;
    }

    List<String> getLines() {
        return lines;
    }

    void reset() {
        lines.clear();
        exitStatus = null;
    }


    // --------------------------------------------------------------------------------


    static class SystemExitException extends RuntimeException {
        private final int status;

        SystemExitException(final int status) {
            super("System exited with status " + status);
            this.status = status;
        }

        int getStatus() {
            return status;
        }
    }
}
