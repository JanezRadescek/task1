package si.janez.exceptions;

import org.jboss.logging.Logger;

public class ApplicationException extends RuntimeException {
    protected Logger.Level logLevel = Logger.Level.WARN;
    protected int httpCode = 500;

    public ApplicationException(String message) {
        this(message, 500);
    }

    public ApplicationException(String message, int httpCode) {
        this(message, httpCode, null);
    }

    public ApplicationException(String message, int httpCode, Throwable cause) {
        super(message, cause);
        this.httpCode = httpCode;
    }
}
