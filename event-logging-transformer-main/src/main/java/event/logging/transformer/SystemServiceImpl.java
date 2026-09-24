package event.logging.transformer;


public class SystemServiceImpl implements SystemService {

    /// Delegates to {@link System#exit(int)}
    @Override
    public void exit(final int status) {
        System.exit(status);
    }

    /// Delegates to {@link java.io.PrintStream#println()} on {@link System#out}
    @Override
    public void println() {
        System.out.println();
    }

    /// Delegates to {@link java.io.PrintStream#println(String)} on {@link System#out}
    @Override
    public void println(final String str) {
        System.out.println(str);
    }

    /// Delegates to {@link java.io.PrintStream#printf(String, Object...)} on {@link System#out}
    @Override
    public void printf(final String format, final Object... args) {
        System.out.printf(format, args);
    }
}
