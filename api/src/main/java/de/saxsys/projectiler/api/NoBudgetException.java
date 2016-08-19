package de.saxsys.projectiler.api;

/**
 * Exception to be thrown by Crawler implementations if the booking would consume more than the remaining budget for
 * this project.
 * 
 * @author stefan.bley
 */
public class NoBudgetException extends ProjectileApiException {

    private static final long serialVersionUID = 7232508869516591946L;

    public NoBudgetException() {
        super("Time has not been clocked because there is no more budget for this project.");
    }
}
