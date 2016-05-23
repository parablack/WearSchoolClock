package net.parablack.clocktest.json;


public class InvalidDataException extends  Exception{

    public InvalidDataException() {
    }

    public InvalidDataException(String detailMessage) {
        super(detailMessage);
    }
}

