package fudan.se.hardlibrary.exception;

/**
 * @author LBW
 */
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public BadRequestException(String message) {
        super(message);
    }
}
