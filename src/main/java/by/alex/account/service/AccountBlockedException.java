package by.alex.account.service;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException() {
    }

    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountBlockedException(Throwable cause) {
        super(cause);
    }
}
