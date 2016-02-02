package net.parablack.clocktest.watchface.drawer.mode.wrapper;


public class TimeException extends Exception {
    public TimeException() {
    }

    public TimeException(String detailMessage) {
        super(detailMessage);
    }

    public TimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public TimeException(Throwable throwable) {
        super(throwable);
    }
}
