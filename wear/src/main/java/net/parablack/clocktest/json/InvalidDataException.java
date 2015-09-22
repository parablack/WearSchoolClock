package net.parablack.clocktest.json;

/**
 * Created by Simon on 16.09.2015.
 */
public class InvalidDataException extends  Exception{

    public InvalidDataException() {
    }

    public InvalidDataException(String detailMessage) {
        super(detailMessage);
    }
}

