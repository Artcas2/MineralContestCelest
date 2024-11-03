package fr.synchroneyes.mineral.Exception;

public class EventAlreadyHandledException extends Exception {
    public EventAlreadyHandledException(String message) {
        super(message);
    }

    public EventAlreadyHandledException() {
    }
}

