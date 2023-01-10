package khovanets.restsimulateapp;

public class MyStatementException extends Exception {
    public MyStatementException(String message) {
        super(message);
    }
    public MyStatementException(Throwable cause) {
        super(cause);
    }
}
