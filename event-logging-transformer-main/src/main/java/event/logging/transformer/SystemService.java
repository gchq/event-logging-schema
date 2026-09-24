package event.logging.transformer;


public interface SystemService {

    void exit(int status);

    void println();

    void println(String x);

    void printf(String format, Object ... args);
}
