package serverRfid.exception;

public class ReaderException extends RuntimeException {
    public ReaderException(Exception ex) {
        super(ex);
    }

    public ReaderException(String msg) {
        super(msg);
    }
}
