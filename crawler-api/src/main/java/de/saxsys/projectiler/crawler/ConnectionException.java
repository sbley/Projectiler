package de.saxsys.projectiler.crawler;

/**
 * Exception to be thrown by Crawler implementations if the connection to Projectile fails.
 * 
 * @author stefan.bley
 */
public class ConnectionException extends CrawlingException {
    private static final long serialVersionUID = 6824662682328693910L;

    public ConnectionException(final Throwable cause) {
        super("Unable to connect to Projectile", cause);
    }
}
