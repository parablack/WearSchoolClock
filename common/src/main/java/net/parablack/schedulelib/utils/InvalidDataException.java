package net.parablack.schedulelib.utils;


public class InvalidDataException extends  Exception{

    public InvalidDataException() {
    }

    public InvalidDataException(String detailMessage) {
        super(detailMessage);
    }
}

