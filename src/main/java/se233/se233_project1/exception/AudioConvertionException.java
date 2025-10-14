package se233.se233_project1.exception;

public class AudioConvertionException extends RuntimeException {
    public AudioConvertionException(String message) {
        super(message);
    }

    public AudioConvertionException(String message, Throwable cause) {
        super(message, cause);
    }
}
