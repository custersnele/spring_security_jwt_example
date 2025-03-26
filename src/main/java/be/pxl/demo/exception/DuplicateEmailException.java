package be.pxl.demo.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Email already taken.");
    }
}
