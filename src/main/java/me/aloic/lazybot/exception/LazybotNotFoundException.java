package me.aloic.lazybot.exception;

public class LazybotNotFoundException extends RuntimeException
{
    public LazybotNotFoundException(String message) {
        super(message);
    }

    public LazybotNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public LazybotNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
