package de.saxsys.android.projectiler.app.crawler;

/**
 * Exception to be thrown by Crawler implementations if there is any failure.
 * 
 * @author stefan.bley
 */
public class CrawlingException extends Exception {

    private static final long serialVersionUID = 7232508869516591946L;

    public CrawlingException(String message) {
        super(message);
    }

    public CrawlingException(String message, Throwable cause) {
        super(message, cause);
    }
}