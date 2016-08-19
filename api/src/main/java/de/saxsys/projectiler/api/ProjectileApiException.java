package de.saxsys.projectiler.api;

/**
 * Exception to be thrown by Crawler implementations if there is any failure.
 * 
 * @author stefan.bley
 */
public class ProjectileApiException extends Exception {

    private static final long serialVersionUID = 7232508869516591946L;

    public ProjectileApiException(String message) {
        super(message);
    }

    public ProjectileApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
