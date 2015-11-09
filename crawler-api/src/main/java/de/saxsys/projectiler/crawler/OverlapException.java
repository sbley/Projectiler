package de.saxsys.projectiler.crawler;

/**
 * Exception to be thrown by Crawler implementations if the booking overlaps an existing booking.
 * 
 * @author stefan.bley
 */
public class OverlapException extends CrawlingException {

    private static final long serialVersionUID = 7232508869516591946L;

    public OverlapException() {
        super("Time has not been clocked because it overlaps an existing booking.");
    }
}
