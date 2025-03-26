package be.pxl.demo.exception;


public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super("Refresh token not found or already used.");
    }
}
