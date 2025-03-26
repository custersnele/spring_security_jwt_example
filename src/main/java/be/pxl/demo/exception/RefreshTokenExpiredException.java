package be.pxl.demo.exception;

public class RefreshTokenExpiredException extends RuntimeException{
    public RefreshTokenExpiredException() {
        super("Refresh token expired.");
    }
}
